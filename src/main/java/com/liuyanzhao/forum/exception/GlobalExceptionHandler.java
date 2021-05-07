package com.liuyanzhao.forum.exception;

import com.google.gson.Gson;
import com.liuyanzhao.forum.vo.Response;
import org.hibernate.service.spi.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 全局异常捕获
 *
 * @author 言曌
 * @date 2018/3/20 下午12:57
 */

@ControllerAdvice
public class GlobalExceptionHandler {
    // 日志记录工具
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    //错误显示页面
    public static final String viewName = "error/error";


    /**
     * @Title: IMoocExceptionHandler.java
     * @Package com.imooc.exception
     * @Description: 判断是否是ajax请求
     * Copyright: Copyright (c) 2017
     * Company:FURUIBOKE.SCIENCE.AND.TECHNOLOGY
     * @author leechenxiang
     * @date 2017年12月3日 下午1:40:39
     * @version V1.0
     */
    public static boolean isAjax(HttpServletRequest httpRequest) {
        return (httpRequest.getHeader("X-Requested-With") != null
                && "XMLHttpRequest"
                .equals(httpRequest.getHeader("X-Requested-With").toString()));
    }


    /**
     * 400 - Bad Request
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public String handleMissingServletRequestParameterException(MissingServletRequestParameterException e, Model model) {
        logger.error("缺少请求参数", e);
        String message = "【缺少请求参数】" + e.getMessage();
        model.addAttribute("message", message);
        model.addAttribute("code", 400);
        return viewName;
    }

    /**
     * 400 - Bad Request
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public String handleHttpMessageNotReadableException(HttpMessageNotReadableException e, Model model) {
        logger.error("参数解析失败", e);
        String message = "【参数解析失败】" + e.getMessage();
        model.addAttribute("message", message);
        model.addAttribute("code", 400);
        return viewName;
    }

    /**
     * 400 - Bad Request
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String handleMethodArgumentNotValidException(MethodArgumentNotValidException e, Model model) {
        logger.error("参数验证失败", e);
        BindingResult result = e.getBindingResult();
        FieldError error = result.getFieldError();
        String field = error.getField();
        String code = error.getDefaultMessage();
        String message = "【参数验证失败】" + String.format("%s:%s", field, code);
        model.addAttribute("message", message);
        model.addAttribute("code", 400);
        return viewName;
    }

    /**
     * 400 - Bad Request
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public String handleBindException(BindException e, Model model) {
        logger.error("参数绑定失败", e);
        BindingResult result = e.getBindingResult();
        FieldError error = result.getFieldError();
        String field = error.getField();
        String code = error.getDefaultMessage();
        String message = "【参数绑定失败】" + String.format("%s:%s", field, code);

        model.addAttribute("message", message);
        model.addAttribute("code", 400);
        return viewName;
    }


    /**
     * 400 - Bad Request
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public String handleServiceException(ConstraintViolationException e, Model model) {
        logger.error("参数验证失败", e);
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        ConstraintViolation<?> violation = violations.iterator().next();
        String message = "【参数验证失败】" + violation.getMessage();
        model.addAttribute("message", message);
        model.addAttribute("code", 400);
        return viewName;
    }

    /**
     * 400 - Bad Request
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ValidationException.class)
    public String handleValidationException(ValidationException e, Model model) {
        logger.error("参数验证失败", e);
        String message = "【参数验证失败】" + e.getMessage();
        model.addAttribute("message", message);
        model.addAttribute("code", 400);
        return viewName;
    }

    /**
     * 404 - Not Found
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoHandlerFoundException.class)
    public String noHandlerFoundException(NoHandlerFoundException e, Model model) {
        logger.error("Not Found", e);
        String message = "【页面不存在】" + e.getMessage();
        model.addAttribute("message", message);
        model.addAttribute("code", 404);
        return viewName;
    }


    /**
     * 405 - Method Not Allowed
     */
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public String handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e, Model model) {
        logger.error("不支持当前请求方法", e);
        String message = "【不支持当前请求方法】" + e.getMessage();
        model.addAttribute("message", message);
        model.addAttribute("code", 405);
        return viewName;
    }

    /**
     * 415 - Unsupported Media Type
     */
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public String handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e, Model model) {
        logger.error("不支持当前媒体类型", e);
        String message = "【不支持当前媒体类型】" + e.getMessage();
        model.addAttribute("message", message);
        model.addAttribute("code", 415);
        return viewName;
    }


    /**
     * 业务层需要自己声明异常的情况
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(ServiceException.class)
    public String handleServiceException(ServiceException e, Model model) {
        logger.error("业务逻辑异常", e);
        String message = "【业务逻辑异常】" + e.getMessage();
        model.addAttribute("message", message);
        return viewName;
    }


    /**
     * 获取其它异常。包括404
     *
     * @param e
     * @return
     * @throws Exception
     */
    @ExceptionHandler(value = NotFoundException.class)
    public Object customNotFoundErrorHandler(HttpServletRequest request,
                                             HttpServletResponse response,
                                             Exception e, Model model) throws IOException {
        logger.error("Exception", e);
        model.addAttribute("message", e.getMessage());

        return "error/404";
    }

    /**
     * 获取其它异常。包括500
     *
     * @param e
     * @return
     * @throws Exception
     */
    @ExceptionHandler(value = Exception.class)
    public Object defaultErrorHandler(HttpServletRequest request,
                                      HttpServletResponse response,
                                      Exception e, Model model) throws IOException {

        if (isAjax(request)) {
//            return new Response(false, e.getMessage(), 443);
            ModelAndView mav = new ModelAndView();
            MappingJackson2JsonView view = new MappingJackson2JsonView();
            Map<String, Object> attributes = new HashMap<String, Object>();
            if (e instanceof AccessDeniedException) {
                String msg = "您没有权限操作！";
                attributes.put("message", msg);
            } else {
                attributes.put("message", e.getMessage());
            }
            attributes.put("success", false);
            view.setAttributesMap(attributes);
            mav.setView(view);
            return mav;
        }

        //Spring Security拦截，没有权限访问或者没有登录
        if (e instanceof AccessDeniedException) {
            //请登录
            logger.error("无权访问", e);
            model.addAttribute("message", "无权操作，请先登录！");
            return "redirect:/login";
        }
        //其他异常
        logger.error("Exception", e);
        String message = e.getMessage();
        model.addAttribute("message", message);
        model.addAttribute("code", 500);
        return viewName;
    }
}
