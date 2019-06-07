package com.liuyanzhao.forum.interceptor;

import com.liuyanzhao.forum.entity.User;
import com.liuyanzhao.forum.repository.UserRepository;
import com.liuyanzhao.forum.service.MessageService;
import com.liuyanzhao.forum.service.NoticeService;
import com.liuyanzhao.forum.service.UserService;
import com.liuyanzhao.forum.vo.UserSessionVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 言曌
 * @date 2018/4/7 上午11:21
 */

public class SessionInterceptor implements HandlerInterceptor {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private NoticeService noticeService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 在请求处理之前进行调用（Controller方法调用之前）
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return true;
    }

    /**
     * 在请求处理之后调用，但是在视图被渲染之前（Controller方法调用之后）
     *
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        UserSessionVO userSession = (UserSessionVO) request.getSession().getAttribute("user");
        if (userSession == null) {
            //如果用户授权存在，添加Session
            if (SecurityContextHolder.getContext().getAuthentication() != null && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
                    && !SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals("anonymousUser")) {
                User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                User user = userRepository.findByUsername(principal.getUsername());
                UserSessionVO userVO = userService.getUserSessionVO(user);
                request.getSession().setAttribute("user", userVO);
            }
        } else {
            User user = new User();
            user.setId(userSession.getId());
            try {
                userSession.setMessageSize(messageService.countNotReadMessageSize(user));
                userSession.setNoticeSize(noticeService.countNotReadNotices(user));
            } catch (Exception e) {
                logger.error("redis服务故障", e);
            }
            request.getSession().setAttribute("user", userSession);
        }


    }

    /**
     * 在整个请求调用之后被调用，也就是在 DispatcherServlet 渲染了对应的视图后执行（主要用于资源清理）
     *
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}