package com.liuyanzhao.forum.config;

import com.liuyanzhao.forum.entity.LoginRecord;
import com.liuyanzhao.forum.entity.User;
import com.liuyanzhao.forum.filter.KaptchaAuthenticationFilter;
import com.liuyanzhao.forum.security.MyBCryptPasswordEncoder;
import com.liuyanzhao.forum.service.IpService;
import com.liuyanzhao.forum.service.LoginRecordService;
import com.liuyanzhao.forum.service.impl.UserServiceImpl;
import com.liuyanzhao.forum.util.IPUtil;
import com.liuyanzhao.forum.vo.UserSessionVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 安全配置类
 *
 * @author 言曌
 * @date 2018/1/23 上午11:37
 */
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true) // 启用方法安全设置
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String KEY = "liuyanzhao.com";

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private LoginRecordService loginRecordService;

    @Autowired
    private IpService ipService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    // 使用 BCrypt 加密
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new MyBCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * 自定义配置
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //在认证用户名之前认证验证码，如果验证码错误，将不执行用户名和密码的认证
        http.addFilterBefore(new KaptchaAuthenticationFilter("/login", "/login?error"), UsernamePasswordAuthenticationFilter.class);

        http.authorizeRequests().antMatchers("/css/**", "/js/**", "/fonts/**", "/index", "/favicon.ico", "/sitemap.txt","static/**").permitAll()
                .antMatchers("/h2-console/**").permitAll()
                .antMatchers("/login").permitAll()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .and()
                .formLogin()   //基于 Form 表单登录验证
                .loginPage("/login")
                .successHandler(new AuthenticationSuccessHandler() {
                    //用户名和密码正确执行
                    @Override
                    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
                        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                        if (principal != null && principal instanceof UserDetails) {
                            //1、添加Session
                            UserSessionVO user = userService.getUserSessionVO((User) principal);
                            httpServletRequest.getSession().setAttribute("user", user);
                            //2、写入日志
                            logger.info("【用户已登录】" + ((User) principal).getUsername());
                            //3、写入数据库login_record表
                            LoginRecord loginRecord = new LoginRecord();
                            String ip = IPUtil.getIpAddr(httpServletRequest);
                            loginRecord.setIp(ip);
                            loginRecord.setUser((User) principal);
                            loginRecord.setArea(ipService.getIpArea(ip));
                            loginRecord.setLoginType("账号密码登录");
                            loginRecordService.saveLoginRecord(loginRecord);
                            //4、返回数据
                            httpServletResponse.setContentType("application/json;charset=utf-8");
                            PrintWriter out = httpServletResponse.getWriter();
                            out.write("{\"status\":\"ok\",\"message\":\"登录成功\"}");
                            out.flush();
                            out.close();
                        }
                    }
                })
                .failureHandler(new AuthenticationFailureHandler() {
                    //用户名密码错误执行
                    @Override
                    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
                        httpServletResponse.setContentType("application/json;charset=utf-8");
                        PrintWriter out = httpServletResponse.getWriter();
                        out.write("{\"status\":\"error\",\"message\":\"用户名或密码错误\"}");
                        out.flush();
                        out.close();
                    }
                })
                .and().rememberMe().key(KEY) // 启用 remember me
                .and().exceptionHandling().accessDeniedPage("/403");  // 处理异常，拒绝访问就重定向到 403 页面

        http.logout().logoutUrl("/logout").logoutSuccessUrl("/login");
        http.csrf().ignoringAntMatchers("/h2-console/**"); // 禁用 H2 控制台的 CSRF 防护
        http.csrf().ignoringAntMatchers("/ajax/**"); // 禁用 H2 控制台的 CSRF 防护
        http.csrf().ignoringAntMatchers("/upload");
        http.headers().frameOptions().sameOrigin(); // 允许来自同一来源的H2 控制台的请求

    }


    /**
     * 认证信息管理
     *
     * @param auth
     * @throws Exception
     */
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService);
        auth.authenticationProvider(authenticationProvider());
    }


}