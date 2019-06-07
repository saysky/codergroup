package com.liuyanzhao.forum.controller;

import com.google.gson.Gson;
import com.liuyanzhao.forum.entity.*;
import com.liuyanzhao.forum.enums.SiteTitleEnum;
import com.liuyanzhao.forum.service.*;
import com.liuyanzhao.forum.service.impl.MailService;
import com.liuyanzhao.forum.service.impl.UserServiceImpl;
import com.liuyanzhao.forum.properties.QiNiuProperties;
import com.liuyanzhao.forum.util.ConstraintViolationExceptionHandler;
import com.liuyanzhao.forum.vo.Response;
import com.liuyanzhao.forum.vo.ResultVO;
import com.liuyanzhao.forum.vo.UserSessionVO;
import com.qiniu.common.QiniuException;
import com.qiniu.storage.model.DefaultPutRet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.List;
import java.util.Objects;

/**
 * @author 言曌
 * @date 2018/3/26 下午6:51
 */


@Controller
@RequestMapping("/manage/account")
public class AccountController {


    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private JobService jobService;

    @Autowired
    private QiNiuService qiNiuService;

    @Autowired
    private QiNiuProperties qiNiuProperties;

    @Autowired
    private MailRetrieveService mailRetrieveService;

    @Autowired
    private MailService mailService;

    @Autowired
    private LoginRecordService loginRecordService;

    @Autowired
    private BindService bindService;

    public final static String SITE_NAME = "CoderGroup";


    @GetMapping
    public String index() {
        return "forward:/manage/account/profile";
    }

    /**
     * 个人中心
     *
     * @param model
     * @return
     */
    @GetMapping("/profile")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")  // 指定角色权限才能操作方法
    public ModelAndView profile(@RequestParam(value = "async", required = false) boolean async,
                                Principal principal,
                                Model model) {
        User originalUser = userService.getUserByUsername(principal.getName());

        model.addAttribute("user", originalUser);
        List<Job> jobList = jobService.listJobs();
        model.addAttribute("jobList", jobList);
        model.addAttribute("site_title", SiteTitleEnum.USER_SPACE_PROFILE.getTitle());
        return new ModelAndView(async == true ? "home/userspace/account/profile :: #right-box-body-replace" : "home/userspace/account/profile");
    }

    /**
     * 保存基本资料
     *
     * @param user
     * @param user
     * @return
     */
    @PostMapping("/profile")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")  // 指定角色权限才能操作方法
    public ResponseEntity<Response> saveBasicProfile(User user, Principal principal,
                                                     HttpSession session) {
        //1、用户资料
        User originalUser = userService.getUserByUsername(principal.getName());
        originalUser.setNickname(user.getNickname());
        originalUser.setHomepage(user.getHomepage());
        originalUser.setGithub(user.getGithub());
        originalUser.setContact(user.getContact());
        originalUser.setJob(user.getJob());

        //处理profile
        String profile = user.getProfile();
        if (profile != null) {
            originalUser.setProfile(profile);
        }

        //2、修改资料，捕获异常
        try {
            userService.saveUser(originalUser);
            //3、修改Session
            UserSessionVO userSessionVO = (UserSessionVO) session.getAttribute("user");
            userSessionVO.setNickname(user.getNickname());
            userSessionVO.setUsername(originalUser.getUsername());
            session.setAttribute("user", userSessionVO);
        } catch (TransactionSystemException e) {
            e.printStackTrace();
            Throwable t = e.getCause();
            while ((t != null) && !(t instanceof ConstraintViolationException)) {
                t = t.getCause();
            }
            if (t instanceof ConstraintViolationException) {
                return ResponseEntity.ok().body(new Response(false, ConstraintViolationExceptionHandler.getMessage((ConstraintViolationException) t)));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }

        return ResponseEntity.ok().body(new Response(true, "修改成功"));
    }


    /**
     * 获取编辑头像的界面
     *
     * @param model
     * @return
     */
    @GetMapping("/avatar")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")  // 指定角色权限才能操作方法
    public ModelAndView avatar(@RequestParam(value = "async", required = false) boolean async, Model model, Principal principal) {
        User user = userService.getUserByUsername(principal.getName());
        model.addAttribute("user", user);
        model.addAttribute("site_title", SiteTitleEnum.USER_SPACE_AVATAR.getTitle());
        return new ModelAndView(async == true ? "home/userspace/account/avatar :: #right-box-body-replace" : "home/userspace/account/avatar");
    }


    /**
     * 保存头像
     *
     * @return
     */
    @PostMapping("/avatar")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")  // 指定角色权限才能操作方法
    public ResponseEntity<Response> saveAvatar(@Param("avatarFile") MultipartFile avatarFile,
                                               Principal principal,
                                               HttpSession session) throws IOException {
        //1、后缀过滤，只支持 jpg,jpeg,png,bmp,gif
        String filename = avatarFile.getOriginalFilename();
        String suffix = (filename.substring(filename.lastIndexOf("."))).toLowerCase();
        if (!".jpg".equals(suffix) && !".png".equals(suffix) && !".jpeg".equals(suffix) && !".gif".equals(suffix) && !".bmp".equals(suffix)) {
            return ResponseEntity.ok().body(new Response(false, "文件格式不允许"));
        }

        //2、文件大小过滤
        if (avatarFile.getSize() > 1 * 1024 * 1024) {
            return ResponseEntity.ok().body(new Response(false, "文件太大，请选择小于2MB的图"));
        }

        //3、开始上传
        InputStream inputStream = avatarFile.getInputStream();
        User originalUser = userService.getUserByUsername(principal.getName());
        String avatar = "";
        try {
            com.qiniu.http.Response response = qiNiuService.uploadFile(inputStream);
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            avatar = qiNiuProperties.getCdnPrefix() + putRet.key;
            originalUser.setAvatar(avatar);
            userService.saveUser(originalUser);
        } catch (QiniuException e) {
            logger.error("上传文件失败");
            return ResponseEntity.ok().body(new Response(false, "七牛云接口错误"));
        }

        //4、修改Session
        UserSessionVO userSessionVO = (UserSessionVO) session.getAttribute("user");
        userSessionVO.setAvatar(avatar);
        session.setAttribute("user", userSessionVO);

        return ResponseEntity.ok().body(new Response(true, "上传成功", avatar));
    }


    /**
     * 账号安全页面
     *
     * @return
     */
    @GetMapping("/security")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")  // 指定角色权限才能操作方法
    public ModelAndView security(@RequestParam(value = "async", required = false) boolean async,
                                 Principal principal,
                                 Model model) {
        User user = userService.getUserByUsername(principal.getName());
        model.addAttribute("user", user);
        model.addAttribute("site_title", SiteTitleEnum.USER_SPACE_SECURITY.getTitle());
        return new ModelAndView(async == true ? "home/userspace/account/security :: #right-box-body-replace" : "home/userspace/account/security");
    }


    /**
     * 修改密码
     *
     * @param password
     * @return
     */
    @PostMapping("/password")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")  // 指定角色权限才能操作方法
    public ResponseEntity<Response> savePassword(String password, Principal principal) {
        if (password.trim().length() < 6) {
            return ResponseEntity.ok().body(new Response(false, "密码不符合要求"));
        }
        User originalUser = userService.getUserByUsername(principal.getName());
        originalUser.setEncodePassword(password); // 加密密码
        userService.saveUser(originalUser);
        //修改session
        return ResponseEntity.ok().body(new Response(true, "修改成功"));
    }

    /**
     * 修改Email
     *
     * @return
     */
    @PostMapping("/email")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")  // 指定角色权限才能操作方法
    public ResponseEntity<Response> saveEmail(String email,
                                              Principal principal,
                                              HttpSession session) {
        //1、用户资料
        User user = userService.getUserByUsername(principal.getName());
        //判断Email是否存在
        User checkUser = userService.getUserByEmail(email);
        //如果Email已存在，且不是当前用户
        if (checkUser != null && !Objects.equals(user.getId(), checkUser.getId())) {
            return ResponseEntity.ok().body(new Response(false, "该邮箱已被使用了哦"));
        }
        //邮箱未修改
        if (Objects.equals(user.getEmail(), email)) {
            return ResponseEntity.ok().body(new Response(false, "邮箱好像没有修改呢"));
        }
        user.setEmail(email);
        user.setIsVerifyEmail("N");

        //2、捕获异常
        try {
            userService.saveUser(user);
        } catch (ConstraintViolationException e) {
            return ResponseEntity.ok().body(new Response(false, ConstraintViolationExceptionHandler.getMessage(e)));
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }

        //3、修改session
        UserSessionVO userSessionVO = (UserSessionVO) session.getAttribute("user");
        userSessionVO.setEmail(email);
        session.setAttribute("user", userSessionVO);

        //4、删除找回密码的邮件
        MailRetrieve mailRetrieve = mailRetrieveService.getMailRetrieveByAccount(user.getUsername());
        if (mailRetrieve != null) {
            mailRetrieveService.removeMailRetrieve(mailRetrieve.getId());
        }
        return ResponseEntity.ok().body(new Response(true, "修改成功"));
    }

    /**
     * 发送验证邮件
     *
     * @return
     */
    @PostMapping("/verifyEmail")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")  // 指定角色权限才能操作方法
    public ResponseEntity<Response> verifyEmail(Principal principal,
                                                HttpServletRequest request) {

        String username = principal.getName();
        User user = userService.getUserByUsername(username);
        //1、如果已发送，且时间在60s内，则提示
        MailRetrieve mailRetrieve = mailRetrieveService.getMailRetrieveByAccount(username);
        if (mailRetrieve != null && (System.currentTimeMillis() - mailRetrieve.getCreateTime()) < 60 * 1000) {
            return ResponseEntity.ok().body(new Response(false, "邮件已发送，请稍后再试"));
        }
        //2、生成链接
        String basePath = "";
        if (request.getServerPort() == 80) {
            basePath = "http://codergroup.cn/manage/account/active-email";
        } else {
            basePath = "http://codergroup.cn:" + request.getServerPort()  + "/manage/account/active-email";
        }
        String mailUrl = mailRetrieveService.getEmailUrl(basePath, username);
        //3、配置邮件内容
        String receiver = user.getEmail();//接收者
        String subject = "【" + SITE_NAME + "】激活邮箱";//邮件主题(标题)
        StringBuilder content = new StringBuilder();
        content.append("尊敬的用户" + user.getNickname() + "，您好：<br/>");
        content.append("您正在" + SITE_NAME + "进行激活邮箱操作，请您点击下面的链接完成激活<br/>");
        content.append("<a href=" + mailUrl + " target=\"_blank\">" + mailUrl + "</a><br/><br/>");
        content.append("若这不是您本人要求的，请忽略本邮件，一切如常。<br/><br/>");
        try {
            mailService.sendHtmlMail(receiver, subject, content.toString());
        } catch (Exception e) {
            logger.error("发送邮件出现错误", e);
        }

        return ResponseEntity.ok().body(new Response(true, "发送成功"));
    }


    @GetMapping("/login-record")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")  // 指定角色权限才能操作方法
    public ModelAndView loginRecord(@RequestParam(value = "async", required = false) boolean async,
                                    Principal principal, Model model) {
        User user = userService.getUserByUsername(principal.getName());
        List<LoginRecord> list = loginRecordService.listTop50LoginRecords(user);
        model.addAttribute("loginRecordList", list);
        model.addAttribute("site_title", SiteTitleEnum.USER_SPACE_LOGIN_LOG.getTitle());
        return new ModelAndView(async == true ? "home/userspace/account/login_record :: #right-box-body-replace" : "home/userspace/account/login_record");
    }


    @GetMapping("/bind")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")  // 指定角色权限才能操作方法
    public ModelAndView binding(@RequestParam(value = "async", required = false, defaultValue = "false") Boolean async,
                                Model model) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();        user = userService.getUserByUsername(user.getUsername());
        List<Bind> bindList = bindService.listBinds(user);
        model.addAttribute("bindList", bindList);
        model.addAttribute("site_title", SiteTitleEnum.USER_SPACE_BINGINGS.getTitle());
        return new ModelAndView(async == true ? "home/userspace/account/binding :: #right-box-body-replace" : "home/userspace/account/binding");
    }

    @DeleteMapping("/bind/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")  // 指定角色权限才能操作方法
    public ResponseEntity<Response> deleteBind(@PathVariable("id") Long id) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();        user = userService.getUserByUsername(user.getUsername());

        Bind bind = null;
        //1、验证书签是否存在
        try {
            bind = bindService.getBindById(id);
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, "绑定记录不存在！"));
        }

        //2、验证是否是该用户的
        if (Objects.equals(bind.getUser().getUsername(), user.getUsername())) {
            bindService.removeBind(bind);
        } else {
            return ResponseEntity.ok().body(new Response(false, "没有权限操作！"));
        }

        return ResponseEntity.ok().body(new Response(true, "处理成功"));
    }


    @GetMapping("/verify-email-success")
    public String verifyEmailSuccess(@RequestParam(required = false) String email, Model model) {
        model.addAttribute("email", email);
        model.addAttribute("site_title", SiteTitleEnum.EMAIL_VERIFY_SUCCESS.getTitle());
        return "home/userspace/account/verify_email_success";
    }

    @GetMapping("/verify-email-error")
    public String verifyEmailError(@RequestParam(required = false) String errorMsg, Model model) {
        model.addAttribute("errorMsg", errorMsg);
        model.addAttribute("site_title", SiteTitleEnum.EMAIL_VERIFY_FAIL.getTitle());
        return "home/userspace/account/verify_email_error";
    }

    @GetMapping(value = "/active-email")
    public String verifyMail(String sid, String username, HttpServletRequest request) {
        ResultVO resultVO = mailRetrieveService.verifyMailUrl(sid, username);
        //验证通过
        if (resultVO.getCode() == 0) {
            User user = userService.getUserByUsername(username);
            user.setIsVerifyEmail("Y");
            userService.saveUser(user);
            request.setAttribute("email", user.getEmail());
            return "forward:/manage/account/verify-email-success?email=" + user.getEmail();
        } else {
            return "forward:/manage/account/verify-email-error?errorMsg=" + resultVO.getMsg();
        }
    }


}
