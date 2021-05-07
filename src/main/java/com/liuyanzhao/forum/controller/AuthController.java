package com.liuyanzhao.forum.controller;

import com.liuyanzhao.forum.entity.Bind;
import com.liuyanzhao.forum.entity.BindType;
import com.liuyanzhao.forum.entity.LoginRecord;
import com.liuyanzhao.forum.entity.User;
import com.liuyanzhao.forum.repository.BindTypeRepository;
import com.liuyanzhao.forum.repository.LoginRecordRepository;
import com.liuyanzhao.forum.service.*;
import com.liuyanzhao.forum.service.impl.IpServiceImpl;
import com.liuyanzhao.forum.util.IPUtil;
import com.liuyanzhao.forum.dto.BindUserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 言曌
 * @date 2018/5/9 下午2:59
 */
@Controller
public class AuthController {
    private Logger logger = LoggerFactory.getLogger(AuthController.class);
    @Autowired
    private QQAuthService qqAuthService;

    @Autowired
    private GithubAuthService githubAuthService;

    @Autowired
    private UserService userService;

    @Autowired
    protected AuthenticationManager authenticationManager;

    @Autowired
    private BindTypeRepository bindTypeRepository;

    @Autowired
    private BindService bindService;

    @Autowired
    private IpServiceImpl ipService;

    @Autowired
    private LoginRecordService loginRecordService;

    //第三方授权后会回调此方法，并将code传过来
    @GetMapping("/oauth/{type}/callback")
    public String auth(@RequestParam(value = "code") String code,
                       @PathVariable(value = "type") String type,
                       HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {

        //1、判断类型是否存在
        BindType bindType = bindTypeRepository.findByName(type);
        if (bindType == null) {
            return "error/404";
        }

        //2、根据code获取token,根据openId判断用户是否已经绑定过
        String accessToken = null;
        String openId = null;
        BindUserDTO userInfo = null;
        String identifier = null;
        if ("qq".equals(type)) {
            try {
                accessToken = qqAuthService.getAccessToken(code);
            } catch (Exception e) {
                e.printStackTrace();
                response.sendRedirect(qqAuthService.getAuthorizationUrl());
            }
            openId = qqAuthService.getOpenId(accessToken);
            userInfo = qqAuthService.getUserInfo(accessToken, openId);
            identifier = openId;
        } else if ("github".equals(type)) {
            try {
                accessToken = githubAuthService.getAccessToken(code);
            } catch (Exception e) {
                e.printStackTrace();
                response.sendRedirect(githubAuthService.getAuthorizationUrl());
            }
            userInfo = githubAuthService.getUserInfo(accessToken, null);
            identifier = userInfo.getOpenId();
        } else {
            return "error/404";
        }

        userInfo.setBindType(bindType);
        request.getSession().setAttribute("bindUser", userInfo);
        User user = userService.getUserByCondition(bindType, identifier);

        //3、判断是否登录
        //如果登录，绑定当前用户
        if (SecurityContextHolder.getContext().getAuthentication() != null && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
                && !SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString().equals("anonymousUser")) {
            User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Bind bind = new Bind();
            bind.setBindType(bindType);
            bind.setUser(principal);
            bind.setIdentifier(userInfo.getOpenId());
            bindService.saveBind(bind);
            return "redirect:/manage/account/bind";
        } else {
            //如果未登录，进行登录或注册
            //如果用户不存在，则跳转到绑定页面
            if (user == null) {
                return "redirect:/bind";
            } else {
                //如果用户已存在，则授权登录
                //授权登录
                UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
                token.setDetails(new WebAuthenticationDetails(request));
                Authentication authenticatedUser = authenticationManager.authenticate(token);
                SecurityContextHolder.getContext().setAuthentication(authenticatedUser);
                request.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
                //记录登录信息
                LoginRecord loginRecord = new LoginRecord();
                String ip = IPUtil.getIpAddr(request);
                loginRecord.setIp(ip);
                loginRecord.setUser(user);
                loginRecord.setArea(ipService.getIpArea(ip));
                loginRecord.setLoginType(type + "授权登录");
                loginRecordService.saveLoginRecord(loginRecord);
                //跳到首页
                return "home/common/auth_success";
            }
        }


    }

}