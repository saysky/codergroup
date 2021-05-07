package com.liuyanzhao.forum.controller;

import com.liuyanzhao.forum.entity.Authority;
import com.liuyanzhao.forum.entity.LoginRecord;
import com.liuyanzhao.forum.entity.User;
import com.liuyanzhao.forum.exception.NotFoundException;
import com.liuyanzhao.forum.repository.JobRepository;
import com.liuyanzhao.forum.service.AuthorityService;
import com.liuyanzhao.forum.service.LoginRecordService;
import com.liuyanzhao.forum.service.UserService;
import com.liuyanzhao.forum.util.AvatarUtil;
import com.liuyanzhao.forum.util.ConstraintViolationExceptionHandler;
import com.liuyanzhao.forum.vo.Response;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 言曌
 * @date 2018/3/20 下午3:32
 */

@RestController
@RequestMapping("/admin/user")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")  // 指定角色权限才能操作方法
public class UserController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserService userService;

    @Autowired
    private AuthorityService authorityService;

    @Autowired
    private LoginRecordService loginRecordService;

    @Autowired
    private JobRepository jobRepository;

    //未设置职业的值
    private static final Integer JOB_NOTSET_ID = 1;

    /**
     * 查询所用用户
     *
     * @return
     */
    @GetMapping
    public ModelAndView list(
            @RequestParam(value = "async", required = false, defaultValue = "false") Boolean async,
            @RequestParam(value = "keywords", required = false, defaultValue = "") String keywords,
            @RequestParam(value = "authorityId", required = false, defaultValue = "2") Integer authorityId,
            @RequestParam(value = "orderby", required = false, defaultValue = "createTime") String orderby,
            @RequestParam(value = "pageIndex", required = false, defaultValue = "1") Integer pageIndex,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize, Model model) {

        Sort sort = new Sort(Sort.Direction.DESC, orderby, "status");
        PageRequest pageRequest = new PageRequest(pageIndex - 1, pageSize, sort);
        List<Authority> authorities = new ArrayList<>();
        try {
            authorities.add(authorityService.getAuthorityById(authorityId));
        } catch (Exception e) {
            throw new NotFoundException("角色不存在");
        }
        Page<User> userList = userService.listUsersByRoleOrKeywords(authorities, keywords, pageRequest);
        //页面刷新时加载，ajax不加载
        if (async == false) {
            //用户
            List<Authority> userAuthorityList = new ArrayList<>();
            userAuthorityList.add(authorityService.getAuthorityById(2));
            Integer userSize = userService.countUserByAuthority(userAuthorityList);
            model.addAttribute("userSize", userSize);

            //管理员
            List<Authority> adminAuthorityList = new ArrayList<>();
            adminAuthorityList.add(authorityService.getAuthorityById(1));
            Integer adminSize = userService.countUserByAuthority(adminAuthorityList);
            model.addAttribute("adminSize", adminSize);
        }
        model.addAttribute("page", userList);
        model.addAttribute("authorityId", authorityId);
        model.addAttribute("orderby", orderby);
        return new ModelAndView(async == true ? "admin/user/list :: #mainContainerReplace" : "admin/user/list");
    }


    /**
     * 获取添加用户页面
     *
     * @return
     */
    @GetMapping("/add")
    public ModelAndView addUserForm() {
        return new ModelAndView("admin/user/add");
    }

    /**
     * 获取修改用户页面
     *
     * @param id
     * @return
     */
    @GetMapping("/edit/{id}")
    public ModelAndView editUserForm(@PathVariable("id") Integer id) {
        ModelAndView modelAndView = new ModelAndView("admin/user/edit");
        User user = userService.getUserById(id);
        modelAndView.addObject("user", user);
        return modelAndView;
    }


    /**
     * 获得某个用户信息
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ModelAndView get(@PathVariable("id") Integer id) {
        ModelAndView modelAndView = new ModelAndView("admin/user/profile");
        User user = userService.getUserById(id);
        //1、判断用户是否存在
        if (user != null) {
            modelAndView.addObject("user", user);
        } else {
            modelAndView.addObject("userNotExist", true);
            modelAndView.addObject("errorMsg", "用户不存在");
        }
        return modelAndView;
    }

    /**
     * 删除用户
     *
     * @param id
     * @return
     */
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Response> deleteUser(@PathVariable("id") Integer id) {
        try {
            userService.removeUser(id);
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.ok().body(new Response(true, "处理成功"));
    }


    /**
     * 添加/更新用户
     *
     * @param user
     * @param authorityId
     * @return
     */
    @PostMapping
    public ResponseEntity<Response> saveUser(User user, Integer[] authorityId) {
        List<Authority> authorities = new ArrayList<>();
        for (int i = 0; i < authorityId.length; i++) {
            authorities.add(authorityService.getAuthorityById(authorityId[i]));
        }
        user.setAuthorities(authorities);

        //添加用户
        if (user.getId() == null) {
            user.setAvatar(AvatarUtil.getGravatar(user.getEmail()));
            user.setJob(jobRepository.findOne(JOB_NOTSET_ID));
            user.setEncodePassword(user.getPassword()); // 加密密码
        } else { //更新用户
            // 判断密码是否做了变更
            User originalUser = userService.getUserById(user.getId());
            String rawPassword = originalUser.getPassword();//123456
            PasswordEncoder encoder = new BCryptPasswordEncoder();
            String encodePasswd = encoder.encode(user.getPassword());
            boolean isMatch = encoder.matches(rawPassword, encodePasswd);
            if (!isMatch) {
                //不匹配，重新加密
                user.setEncodePassword(user.getPassword());
            } else {
                //匹配，无需加密
                user.setPassword(user.getPassword());
            }
        }

        try {
            userService.saveUser(user);
        } catch (ConstraintViolationException e) {
            return ResponseEntity.ok().body(new Response(false, ConstraintViolationExceptionHandler.getMessage(e)));
        }


        return ResponseEntity.ok().body(new Response(true, "处理成功", user));
    }

    /**
     * 获得所有用户的登录记录
     *
     * @param async
     * @param pageIndex
     * @param pageSize
     * @param model
     * @return
     */
    @GetMapping("/loginRecords")
    public ModelAndView loginRecord(
            @RequestParam(value = "async", required = false, defaultValue = "false") Boolean async,
            @RequestParam(value = "pageIndex", required = false, defaultValue = "1") Integer pageIndex,
            @RequestParam(value = "pageSize", required = false, defaultValue = "15") Integer pageSize, Model model) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        PageRequest pageRequest = new PageRequest(pageIndex - 1, pageSize, sort);
        Page<LoginRecord> loginRecordList = loginRecordService.listAllLoginRecords(pageRequest);
        model.addAttribute("page", loginRecordList);
        model.addAttribute("url", "/admin/user/loginRecords");
        return new ModelAndView(async == true ? "admin/user/login_record :: #mainContainerReplace" : "admin/user/login_record");
    }

    /**
     * 某个用户登录日志
     *
     * @param id
     * @param async
     * @param pageIndex
     * @param pageSize
     * @param model
     * @return
     */
    @GetMapping("/loginRecord/{id}")
    public ModelAndView loginRecord(@PathVariable("id") Integer id,
                                    @RequestParam(value = "async", required = false, defaultValue = "false") Boolean async,
                                    @RequestParam(value = "pageIndex", required = false, defaultValue = "1") Integer pageIndex,
                                    @RequestParam(value = "pageSize", required = false, defaultValue = "15") Integer pageSize, Model model) {
        User user = userService.getUserById(id);
        if (user == null) {
            throw new NotFoundException("用户不存在");
        }
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        PageRequest pageRequest = new PageRequest(pageIndex - 1, pageSize, sort);
        Page<LoginRecord> loginRecordList = loginRecordService.listLoginRecords(user, pageRequest);
        model.addAttribute("page", loginRecordList);
        model.addAttribute("user", user);
        model.addAttribute("url", "/admin/user/loginRecord/" + user.getId());
        return new ModelAndView(async == true ? "admin/user/login_record :: #mainContainerReplace" : "admin/user/login_record");
    }

    /**
     * 删除登录日志
     *
     * @param id
     * @return
     */
    @DeleteMapping(value = "/loginRecord/{id}")
    public ResponseEntity<Response> deleteLoginRecord(@PathVariable("id") Long id) {
        try {
            loginRecordService.deleteLoginRecord(id);
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.ok().body(new Response(true, "处理成功"));
    }


}
