package com.liuyanzhao.forum.controller;

import com.liuyanzhao.forum.entity.*;
import com.liuyanzhao.forum.enums.IsAcceptedEnum;
import com.liuyanzhao.forum.enums.NoticeTypeEnum;
import com.liuyanzhao.forum.enums.PostStatusEnum;
import com.liuyanzhao.forum.enums.SiteTitleEnum;
import com.liuyanzhao.forum.repository.NoticeRepository;
import com.liuyanzhao.forum.service.AnswerService;
import com.liuyanzhao.forum.service.QuestionService;
import com.liuyanzhao.forum.service.UserService;
import com.liuyanzhao.forum.util.ConstraintViolationExceptionHandler;
import com.liuyanzhao.forum.util.RedisOperator;
import com.liuyanzhao.forum.vo.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author 言曌
 * @date 2018/6/4 下午2:31
 */
@Controller
public class AnswerController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private AnswerService answerService;

    @Autowired
    private UserService userService;

    @Autowired
    private NoticeRepository noticeRepository;

    @Autowired
    private RedisOperator redis;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    /**
     * 发表回答
     *
     * @param questionId
     * @param content
     * @return
     */
    @PostMapping("/answers")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")  // 指定角色权限才能操作方法
    public ResponseEntity<Response> createAnswer(@RequestParam(value = "questionId") Long questionId,
                                                 @RequestParam(value = "content") String content) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();        user = userService.getUserByUsername(user.getUsername());
        Question question = questionService.getQuestionById(questionId);
        if (question == null) {
            return ResponseEntity.ok().body(new Response(false, "问题不存在!"));
        }
        try {
            //1、回答问题
            Answer answer = new Answer();
            answer.setQuestion(question);
            answer.setContent(content);
            answer.setUser(user);
            answer.setReplyUser(question.getUser());
            answerService.saveAnswer(answer);

            //2、修改问题的回答数目
            question.setAnswerSize(question.getAnswerSize() + 1);
            questionService.saveQuestion(question);
        } catch (ConstraintViolationException e) {
            e.printStackTrace();
            return ResponseEntity.ok().body(new Response(false, ConstraintViolationExceptionHandler.getMessage(e)));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.ok().body(new Response(true, "处理成功!", null));
    }


    /**
     * 对回答进行评论
     *
     * @param answerId
     * @param content
     * @return
     */
    @PostMapping("/answers/comment")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")  // 指定角色权限才能操作方法
    public ResponseEntity<Response> createCommentForAnswer(@RequestParam(value = "answerId") Long answerId,
                                                           @RequestParam(value = "commentId") Long commentId,
                                                           @RequestParam(value = "content") String content) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        user = userService.getUserByUsername(user.getUsername());

        Answer answer = answerService.getAnswerById(answerId);
        Answer comment = answerService.getAnswerById(commentId);
        if (answer == null) {
            return ResponseEntity.ok().body(new Response(false, "回答不存在!"));
        }
        if (comment == null) {
            return ResponseEntity.ok().body(new Response(false, "评论不存在!"));
        }
        try {
            //1、评论回答问题
            Answer commentAnswer = new Answer();
            commentAnswer.setQuestion(answer.getQuestion());
            commentAnswer.setContent(content);
            commentAnswer.setUser(user);
            commentAnswer.setReplyUser(comment.getUser());
            commentAnswer.setPid(answerId);
            answerService.saveAnswer(commentAnswer);

            //2、修改回答的评论数目
            answer.setCommentSize(answer.getCommentSize() + 1);
            answerService.updateAnswer(answer);

        } catch (ConstraintViolationException e) {
            e.printStackTrace();
            return ResponseEntity.ok().body(new Response(false, ConstraintViolationExceptionHandler.getMessage(e)));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.ok().body(new Response(true, "处理成功!", null));
    }

    //    用户中心
    @GetMapping("/manage/answers")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")  // 指定角色权限才能操作方法
    public ModelAndView answerManage(Model model,
                                     @RequestParam(value = "async", required = false, defaultValue = "false") Boolean async,
                                     @RequestParam(value = "status", required = false, defaultValue = "") String status,
                                     @RequestParam(value = "pageIndex", required = false, defaultValue = "1") int pageIndex,
                                     @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(pageIndex - 1, pageSize, sort);
        List<String> statusList = new ArrayList<>();
        statusList.add(PostStatusEnum.PUBLISH_POST.getCode());
        Page<Answer> page = null;
        //未采纳
        if (PostStatusEnum.PUBLISH_POST.getCode().equals(status)) {
            page = answerService.listAnswersByUserAndIsAcceptedAndStatusIn(user, IsAcceptedEnum.NOT_ACCEPT_STATUS.getCode(), statusList, pageable);
            model.addAttribute("status", "publish");
        }
        //已采纳
        else if (PostStatusEnum.ACCEPT_POST.getCode().equals(status)) {
            page = answerService.listAnswersByUserAndIsAcceptedAndStatusIn(user, IsAcceptedEnum.ACCEPT_STATUS.getCode(), statusList, pageable);
            model.addAttribute("status", "accepted");
        }
        //全部
        else {
            page = answerService.listAnswersByUserAndStatusIn(user, statusList, pageable);

            model.addAttribute("status", "all");
        }
        model.addAttribute("page", page);
        model.addAttribute("site_title", SiteTitleEnum.MY_ANSWER_MANAGE.getTitle());
        return new ModelAndView(async == true ? "home/userspace/question/answer-list :: #tab-pane" : "home/userspace/question/answer-list");
    }

    @PostMapping("/answers/accept")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")  // 指定角色权限才能操作方法
    public ResponseEntity<Response> acceptAnswer(Long answerId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();        user = userService.getUserByUsername(user.getUsername());
        Answer answer = answerService.getAnswerById(answerId);
        if (answer == null) {
            return ResponseEntity.ok().body(new Response(false, "该回答不存在"));
        }
        Question question = answer.getQuestion();
        if (question != null && Objects.equals(question.getUser().getUsername(), user.getUsername())) {
            //采纳答案
            if (PostStatusEnum.PUBLISH_POST.getCode().equals(question.getStatus())) {
                //保存问题
                question.setStatus(PostStatusEnum.RESOLVED_POST.getCode());
                questionService.saveQuestion(question);
                //保存回答
                answer.setIsAccepted(IsAcceptedEnum.ACCEPT_STATUS.getCode());
                answerService.saveAnswer(answer);
                //提醒
                if (!Objects.equals(answer.getUser().getId(), answer.getReplyUser().getId())) {
                    Notice notice = new Notice();
                    notice.setContent(answer.getQuestion().getTitle());
                    notice.setMore(answer.getContent());
                    notice.setGuid(answer.getQuestion().getGuid());
                    notice.setFromUser(question.getUser());
                    notice.setToUser(answer.getUser());
                    NoticeType noticeType = new NoticeType();
                    noticeType.setId(NoticeTypeEnum.ACCEPT_ANSWER.getCode());
                    notice.setNoticeType(noticeType);
                    noticeRepository.save(notice);

                    //添加redis
                    try {
                        redis.incr("noticeSize:" + answer.getUser().getId(), 1);
                    } catch (Exception e) {
                        logger.error("redis服务故障", e);
                    }
                }
            }
        } else {
            return ResponseEntity.ok().body(new Response(false, "没有权限！"));

        }
        return ResponseEntity.ok().body(new Response(true, "操作成功"));
    }

    //后台
    @GetMapping("/admin/answer")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")  // 指定角色权限才能操作方法
    public ModelAndView adminArticleList(
            @RequestParam(value = "status", required = false, defaultValue = "all") String status,
            @RequestParam(value = "async", required = false, defaultValue = "false") Boolean async,
            @RequestParam(value = "pageIndex", required = false, defaultValue = "1") Integer pageIndex,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize, Model model) {

        List<String> statusList = new ArrayList<>();
        Page<Answer> answerPage = null;
        PageRequest pageRequest = new PageRequest(pageIndex - 1, pageSize);
        if (PostStatusEnum.PUBLISH_POST.getCode().equals(status)) {
            statusList.add(PostStatusEnum.PUBLISH_POST.getCode());
            answerPage = answerService.listAnswerByStatusIn(statusList, pageRequest);
            model.addAttribute("status", status);
        } else if (PostStatusEnum.DELETED_POST.getCode().equals(status)) {
            statusList.add(PostStatusEnum.DELETED_POST.getCode());
            answerPage = answerService.listAnswerByStatusIn(statusList, pageRequest);
            model.addAttribute("status", status);
        } else {
            answerPage = answerService.listAllAnswer(pageRequest);
            model.addAttribute("status", PostStatusEnum.ALL_POST.getCode());
        }
        model.addAttribute("page", answerPage);
        return new ModelAndView(async == true ? "admin/answer/list :: #mainContainerReplace" : "admin/answer/list");
    }

    /**
     * 删除回答
     *
     * @return
     */
    @DeleteMapping("/admin/answer/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")  // 指定角色权限才能操作方法
    public ResponseEntity<Response> delete(@PathVariable("id") Long id) {
        try {
            answerService.removeAnswer(id);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.ok().body(new Response(true, "处理成功"));
    }


    /**
     * 恢复回答
     *
     * @param id
     * @return
     */
    @PutMapping("/admin/answer/restore/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")  // 指定角色权限才能操作方法
    public ResponseEntity<Response> restore(@PathVariable("id") Long id) {
        try {
            Answer answer = answerService.getAnswerById(id);
            answer.setStatus(PostStatusEnum.PUBLISH_POST.getCode());
            answerService.saveAnswer(answer);
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.ok().body(new Response(true, "处理成功"));
    }


}
