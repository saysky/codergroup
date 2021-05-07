package com.liuyanzhao.forum.controller;

import com.google.code.kaptcha.Constants;
import com.liuyanzhao.forum.controller.common.BaseController;
import com.liuyanzhao.forum.entity.*;
import com.liuyanzhao.forum.enums.SiteTitleEnum;
import com.liuyanzhao.forum.repository.BindRepository;
import com.liuyanzhao.forum.repository.JobRepository;
import com.liuyanzhao.forum.repository.UserRepository;
import com.liuyanzhao.forum.service.AuthorityService;
import com.liuyanzhao.forum.service.MailRetrieveService;
import com.liuyanzhao.forum.service.impl.MailService;
import com.liuyanzhao.forum.service.UserService;
import com.liuyanzhao.forum.util.AvatarUtil;
import com.liuyanzhao.forum.util.BCryptUtil;
import com.liuyanzhao.forum.util.ConstraintViolationExceptionHandler;
import com.liuyanzhao.forum.dto.BindUserDTO;
import com.liuyanzhao.forum.vo.Response;
import com.liuyanzhao.forum.vo.ResultVO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author 言曌
 * @date 2018/2/23 下午5:31
 */

@Controller
public class LoginController extends BaseController {


    //角色(用户)的值
    private static final Integer ROLE_USER_AUTHORITY_ID = 2;

    //未设置职业的值
    private static final Integer JOB_NOTSET_ID = 1;


    //保留字符
    private static final String[] DISABLED_USERNAME = {"admin", "admins", "user", "users", "category", "categorys", "search", "bulletin", "notice", "forum", "question", "questions", "answer", "answers", "manage", "manages", "post", "posts", "article", "articles", "blog", "blogs", "ajax", "css", "js", "forum", "static", "public", "resource", "login", "logout", "forget", "resetpass", "reset", "template", "components", "css", "img", "images", "js", "getKaptchaImage", "匿名", "管理员", "站长", "版主"};

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthorityService authorityService;

    @Autowired
    private UserService userService;

    @Autowired
    private MailRetrieveService mailRetrieveService;

    @Autowired
    protected AuthenticationManager authenticationManager;

    @Autowired
    private MailService mailService;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private BindRepository bindRepository;


    /**
     * 登录页面显示
     *
     * @return
     */
    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("site_title", SiteTitleEnum.LOGIN_PAGE.getTitle());
        return "home/login/login";
    }

    @GetMapping("/loginModal")
    public ModelAndView getLoginModal(@RequestParam(value = "async", required = false, defaultValue = "false") Boolean async) {
        return new ModelAndView(async == true ? "home/common/login_modal :: #box-login" : "home/login/login");
    }


    /**
     * 注册页面显示
     *
     * @return
     */
    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("site_title", SiteTitleEnum.REGISTER_PAGE.getTitle());
        return "home/login/register";
    }


    /**
     * 注册成功跳转页面
     *
     * @return
     */
    @GetMapping("/register-success")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")  // 指定角色权限才能操作方法
    public String registerSuccess(Principal principal, Model model) {
        User user = userService.getUserByUsername(principal.getName());
        Long count = userService.countUser();
        model.addAttribute("site_title", SiteTitleEnum.REGISTER_SUCCESS_PAGE.getTitle());
        model.addAttribute("user", user);
        model.addAttribute("count", count);
        return "home/login/register_success";
    }


    /**
     * 注册提交
     *
     * @param user
     * @return
     */
    @PostMapping("/register")
    @Transactional()
    public ResponseEntity<Response> registerUser(User user) {
        String expect = (String) request.getSession().getAttribute(Constants.KAPTCHA_SESSION_KEY);
        if (expect != null && !expect.equalsIgnoreCase(request.getParameter("kaptcha"))) {
            return ResponseEntity.ok().body(new Response(false, "验证码错误"));
        }
        BindUserDTO bindUser = (BindUserDTO) request.getSession().getAttribute("bindUser");


        //1、验证用户名长度
        if (user.getUsername() != null) {
            if (user.getUsername().trim().length() < 4 || user.getUsername().trim().length() > 20) {
                return ResponseEntity.ok().body(new Response(false, "用户名长度不合法"));
            }
        } else {
            return ResponseEntity.ok().body(new Response(false, "用户名不可为空"));
        }
        //2、验证密码长度
        if (user.getPassword() != null) {
            if (user.getPassword().trim().length() < 6 || user.getPassword().trim().length() > 20) {
                return ResponseEntity.ok().body(new Response(false, "密码长度不合法"));
            }
        } else {
            return ResponseEntity.ok().body(new Response(false, "密码不可为空"));
        }

        //3、验证用户名可用性
        for (String str : DISABLED_USERNAME) {
            if (str.equals(user.getUsername())) {
                return ResponseEntity.ok().body(new Response(false, "用户名不合法，包含保留字符！"));
            }
        }

        //4、验证用户名是否存在
        if (userRepository.findByUsername(user.getUsername()) != null) {
            return ResponseEntity.ok().body(new Response(false, "用户名已被注册！"));
        }

        //5、验证邮箱是否存在
        if (userRepository.findByEmail(user.getEmail()) != null) {
            return ResponseEntity.ok().body(new Response(false, "电子邮箱已被注册！"));
        }

        //6、添加权限
        List<Authority> authorities = new ArrayList<>();
        authorities.add(authorityService.getAuthorityById(ROLE_USER_AUTHORITY_ID));
        user.setAuthorities(authorities);
        if (bindUser != null) {
            user.setNickname(bindUser.getNickname());
            user.setAvatar(bindUser.getAvatar());
        } else {
            user.setNickname(user.getUsername());
            user.setAvatar(AvatarUtil.getGravatar(user.getEmail()));
        }
        String password = user.getPassword();
        user.setEncodePassword(password);
        //添加职业
        user.setJob(jobRepository.findOne(JOB_NOTSET_ID));
        User backUser = null;
        try {
            //6、添加用户
            backUser = userService.saveUser(user);
            //7、创建对应的积分等级数据
        } catch (ConstraintViolationException e) {
            e.printStackTrace();
            return ResponseEntity.ok().body(new Response(false, ConstraintViolationExceptionHandler.getMessage(e)));
        } catch (Exception e) {
            e.printStackTrace();

            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }

        //7、添加授权，自动登录
        //进行授权登录
        try {
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user.getUsername(), password);
            token.setDetails(new WebAuthenticationDetails(request));
            Authentication authenticatedUser = authenticationManager.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(authenticatedUser);
            request.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());

        } catch (Exception e) {
            e.printStackTrace();
        }

        //添加第三方绑定
        if (bindUser != null) {
            BindType bindType = bindUser.getBindType();
            Bind bind = new Bind();
            bind.setBindType(bindType);
            bind.setUser(backUser);
            bind.setIdentifier(bindUser.getOpenId());
            bindRepository.save(bind);
        }
        //数据
        Long count = userService.countUser();

        return ResponseEntity.ok().body(new Response(true, "注册成功", count));

    }


    /**
     * 找回密码页面显示
     *
     * @return
     */
    @GetMapping("/forget")
    public String forget(Model model) {
        model.addAttribute("site_title", SiteTitleEnum.FORGET_PAGE.getTitle());
        return "home/login/forget";
    }


    /**
     * 退出登录
     *
     * @param request
     * @param response
     * @return
     */
    @GetMapping(value = "/logout")
    public String logoutPage(HttpServletRequest request, HttpServletResponse response, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
            //清除记住我cookie
            Cookie cookie = new Cookie("remember-me", null);
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        }
        model.addAttribute("site_title", SiteTitleEnum.LOGIN_PAGE.getTitle());
        return "redirect:/login?logout";
    }

    /**
     * 忘记密码提交,然后跳转登录页面
     *
     * @param request
     * @return
     */
    @PostMapping(value = "/forget")
    public ResponseEntity<Response> forgetUser(HttpServletRequest request, String username, String email) {
        String expect = (String) request.getSession().getAttribute(Constants.KAPTCHA_SESSION_KEY);
        if (expect != null && !expect.equalsIgnoreCase(request.getParameter("kaptcha"))) {
            return ResponseEntity.ok().body(new Response(false, "验证码错误"));
        }
        User user = userRepository.findByUsernameAndEmail(username, email);
        //1、判断用户是否存在
        if (user == null) {
            return ResponseEntity.ok().body(new Response(false, "用户名或邮箱不正确!"));
        }
        //2、判断是否验证了邮箱
        if (!Objects.equals(user.getIsVerifyEmail(), "Y")) {
            return ResponseEntity.ok().body(new Response(false, "该账号邮箱没有验证，无法找回，请联系管理员!"));
        }

        //2、如果已发送，且时间在60s内，则提示
        MailRetrieve mailRetrieve = mailRetrieveService.getMailRetrieveByAccount(username);
        if (mailRetrieve != null && (System.currentTimeMillis() - mailRetrieve.getCreateTime()) < 60 * 1000) {
            return ResponseEntity.ok().body(new Response(false, "邮件已发送，请稍后再试!"));
        }
        //3、生成链接
        String basePath = "";
        if (request.getServerPort() == 80) {
            basePath = "http://codergroup.cn/resetpass";
        } else {
            basePath = "http://codergroup.cn:" + request.getServerPort() + "/resetpass";
        }
        String mailUrl = mailRetrieveService.getEmailUrl(basePath, username);
        //4、发送邮件
        String receiver = user.getEmail();//接收者
        String subject = "【" + SITE_NAME + "】重置密码";//邮件主题(标题)
        StringBuilder content = new StringBuilder();
        content.append("尊敬的用户" + user.getNickname() + "，您好：<br/>");
        content.append("您正在" + SITE_NAME + "进行重置密码操作，请您点击下面的链接完成重置<br/>");
        content.append("<a href=" + mailUrl + " target=\"_blank\">" + mailUrl + "</a><br/><br/>");
        content.append("若这不是您本人要求的，请忽略本邮件，一切如常。<br/><br/>");
        mailService.sendHtmlMail(receiver, subject, content.toString());
        return ResponseEntity.ok().body(new Response(true, "发送成功"));
    }


    @GetMapping("/verify-resetpass-error")
    public String verifyResetpassError(String username, String errorMsg, Model model) {
        model.addAttribute("username", username);
        model.addAttribute("errorMsg", errorMsg);
        model.addAttribute("site_title", SiteTitleEnum.RESET_PASSWORD_VERIFY_FAIL.getTitle());
        return "home/login/verify_resetpass_error";
    }

    /**
     * 重置密码验证
     * 验证通过，显示修改密码页面
     *
     * @param sid
     * @param username
     * @return
     */
    @GetMapping(value = "/resetpass")
    public String verifyMail(String sid, String username, HttpServletRequest request, Model model) {

        ResultVO resultVO = mailRetrieveService.verifyMailUrl(sid, username);
        //验证通过,显示设置密码页面
        if (resultVO.getCode() == 0) {
            //验证成功
            model.addAttribute("username", username);
            model.addAttribute("sid", sid);
            model.addAttribute("site_title", SiteTitleEnum.RESET_PASSWORD_PAGE.getTitle());
            return "home/login/resetpass";
        } else {
            //验证失败
            String errorMsg = resultVO.getMsg();
            return "forward:/verify-resetpass-error?username=" + username + "&errorMsg=" + errorMsg;
        }
    }


    /**
     * 重置密码提交
     *
     * @param sid
     * @param username
     * @param password
     * @return
     */
    @PostMapping(value = "/resetpass")
    public ResponseEntity<Response> resetPass(String sid, String username, String password) {
        ResultVO resultVO = mailRetrieveService.verifyMailUrl(sid, username);
        //如果验证通过(防止用户自定义表单，再次验证)
        if (resultVO.getCode() == 0) {
            //修改密码
            User user = userService.getUserByUsername(username);
            user.setPassword(BCryptUtil.encode(password));
            userService.saveUser(user);
            return ResponseEntity.ok().body(new Response(true, "修改成功"));
        }
        //验证失败
        else {
            return ResponseEntity.ok().body(new Response(false, resultVO.getMsg()));
        }
    }


}
