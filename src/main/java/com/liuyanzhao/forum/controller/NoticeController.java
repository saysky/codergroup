package com.liuyanzhao.forum.controller;

import com.liuyanzhao.forum.controller.common.BaseController;
import com.liuyanzhao.forum.entity.Notice;
import com.liuyanzhao.forum.entity.NoticeType;
import com.liuyanzhao.forum.entity.User;
import com.liuyanzhao.forum.enums.NoticeStatusEnum;
import com.liuyanzhao.forum.enums.SiteTitleEnum;
import com.liuyanzhao.forum.service.NoticeService;
import com.liuyanzhao.forum.service.UserService;
import com.liuyanzhao.forum.util.ConstraintViolationExceptionHandler;
import com.liuyanzhao.forum.util.RedisOperator;
import com.liuyanzhao.forum.vo.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

/**
 * 通知管理
 *
 * @author 言曌
 * @date 2018/6/1 上午12:06
 */

@Controller
public class NoticeController extends BaseController{

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private RedisOperator redisOperator;

    @GetMapping("/manage/notices")
    public ModelAndView noticesList(@RequestParam(value = "async", required = false, defaultValue = "false") Boolean async,
                                    @RequestParam(value = "pageIndex", required = false, defaultValue = "1") Integer pageIndex,
                                    @RequestParam(value = "pageSize", required = false, defaultValue = "15") Integer pageSize,
                                    Model model) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        PageRequest pageRequest = new PageRequest(pageIndex - 1, pageSize, sort);
        List<Integer> statusList = new ArrayList<>();
        statusList.add(NoticeStatusEnum.NOT_READ.getCode());
        statusList.add(NoticeStatusEnum.HAD_READ.getCode());
        Page<Notice> noticePage = noticeService.listNoticesByUser(user, pageRequest);
        model.addAttribute("page", noticePage);
        model.addAttribute("site_title", SiteTitleEnum.NOTICE_MANAGE.getTitle());
        return new ModelAndView(async == true ? "home/userspace/notice :: #tab-pane" : "home/userspace/notice");
    }

    /**
     * 全部清空
     *
     * @return
     */
    @PostMapping("/manage/notices")
    public ResponseEntity<Response> action(@RequestParam(value = "action") String action) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
            if ("markread".equals(action)) {
                noticeService.markreadNoticesByUser(user);
            } else if ("delete".equals(action)) {
                noticeService.clearNoticesByUser(user);
            }
        } catch (ConstraintViolationException e) {
            return ResponseEntity.ok().body(new Response(false, ConstraintViolationExceptionHandler.getMessage(e)));
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }

        try {
            redisOperator.del("noticeSize:" + user.getId());
        }catch (Exception e) {
            logger.error("redis服务故障");
        }
        return ResponseEntity.ok().body(new Response(true, "处理成功"));
    }

    /**
     * 返回未阅读的私信
     *
     * @return
     */
    @GetMapping("/notice/recentNotRead")
    public ResponseEntity<Response> getNotReadMessageList() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<NoticeType> noticeTypeList = null;
        try {
            noticeTypeList = noticeService.mergeAndListNewNotices(user);
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.ok().body(new Response(true, "操作成功", noticeTypeList));

    }


}
