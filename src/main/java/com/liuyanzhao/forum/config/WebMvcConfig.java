package com.liuyanzhao.forum.config;

import com.liuyanzhao.forum.interceptor.SessionInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;


/**
 * @author 言曌
 * @date 2018/3/26 下午12:13
 */

@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {


    @Bean
    SessionInterceptor sessionInterceptor() {
        return new SessionInterceptor();
    }



    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //拦截器按照顺序执行
        registry.addInterceptor(sessionInterceptor()).addPathPatterns("/**")
                .excludePathPatterns("/components/**","/css/**","/js/**","/img/**","/logout");

    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
    }


}
