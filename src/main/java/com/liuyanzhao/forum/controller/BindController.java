package com.liuyanzhao.forum.controller;

import com.google.code.kaptcha.Constants;
import com.liuyanzhao.forum.controller.common.BaseController;
import com.liuyanzhao.forum.entity.Bind;
import com.liuyanzhao.forum.entity.BindType;
import com.liuyanzhao.forum.entity.User;
import com.liuyanzhao.forum.enums.SiteTitleEnum;
import com.liuyanzhao.forum.service.BindService;
import com.liuyanzhao.forum.service.UserService;
import com.liuyanzhao.forum.util.ConstraintViolationExceptionHandler;
import com.liuyanzhao.forum.dto.BindUserDTO;
import com.liuyanzhao.forum.vo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.ConstraintViolationException;

/**
 * @author 言曌
 * @date 2018/5/14 下午11:26
 */

@Controller
public class BindController extends BaseController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private BindService bindService;

    @Autowired
    private UserService userService;


    @GetMapping("/bind")
    private String bind(Model model) {
        BindUserDTO bindUser = (BindUserDTO) request.getSession().getAttribute("bindUser");
        if(bindUser == null) {
            return "error/404";
        }
        model.addAttribute("site_title", SiteTitleEnum.BIND_PAGE.getTitle());
        return "home/common/auth_bind";
    }

    //绑定第三方并登录
    @PostMapping("/bind/login")
    public ResponseEntity<Response> bindLogin(User user) {
        //校验验证码
        String expect = (String) request.getSession().getAttribute(Constants.KAPTCHA_SESSION_KEY);
        if (expect != null && !expect.equalsIgnoreCase(request.getParameter("kaptcha"))) {
            return ResponseEntity.ok().body(new Response(false, "验证码错误"));
        }
        Authentication authenticatedUser = null;

        try {
            //验证用户名和密码
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
            token.setDetails(new WebAuthenticationDetails(request));
            authenticatedUser = authenticationManager.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(authenticatedUser);
            request.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, "用户名或密码错误！"));
        }

        //判断用户是否存在
        User principal = null;
        if (SecurityContextHolder.getContext().getAuthentication() != null && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
                && !SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString().equals("anonymousUser")) {
            principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } else {

        }

        //判断session（记录第三方类型和openid）
        BindUserDTO bindUser = (BindUserDTO) request.getSession().getAttribute("bindUser");
        if (bindUser == null) {
            return ResponseEntity.ok().body(new Response(false, "请重新拉起QQ授权!"));
        }

        //添加绑定
        try {
            //添加绑定关系
            Bind bind = new Bind();
            String identifier = bindUser.getOpenId();
            BindType bindType = bindUser.getBindType();
            bind.setIdentifier(identifier);
            bind.setUser(principal);
            bind.setBindType(bindType);
            bindService.saveBind(bind);

        } catch (ConstraintViolationException e) {
            return ResponseEntity.ok().body(new Response(false, ConstraintViolationExceptionHandler.getMessage(e)));
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.ok().body(new Response(true, "操作成功"));
    }




}
