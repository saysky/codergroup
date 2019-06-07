package com.liuyanzhao.forum.controller;

import com.liuyanzhao.forum.controller.common.BaseController;
import com.liuyanzhao.forum.entity.Answer;
import com.liuyanzhao.forum.entity.Question;
import com.liuyanzhao.forum.entity.User;
import com.liuyanzhao.forum.enums.PostStatusEnum;
import com.liuyanzhao.forum.enums.SiteTitleEnum;
import com.liuyanzhao.forum.exception.CustomException;
import com.liuyanzhao.forum.exception.NotFoundException;
import com.liuyanzhao.forum.service.AnswerService;
import com.liuyanzhao.forum.service.QuestionService;
import com.liuyanzhao.forum.service.RankService;
import com.liuyanzhao.forum.service.UserService;
import com.liuyanzhao.forum.util.BaiduPostUtil;
import com.liuyanzhao.forum.util.ConstraintViolationExceptionHandler;
import com.liuyanzhao.forum.util.HTMLUtil;
import com.liuyanzhao.forum.vo.Response;
import com.liuyanzhao.forum.vo.UserRankVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.ConstraintViolationException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author 言曌
 * @date 2018/6/3 下午3:21
 */
@Controller
public class QuestionController extends BaseController {


    @Autowired
    private QuestionService questionService;

    @Autowired
    private AnswerService answerService;

    @Autowired
    private UserService userService;

    @Autowired
    private RankService rankService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final String hostName = "http://codergroup.cn";


    /**
     * 问题列表显示
     *
     * @param keywords
     * @param async
     * @param pageIndex
     * @param pageSize
     * @param model
     * @return
     */
    @GetMapping("/questions")
    public ModelAndView index(@RequestParam(value = "async", required = false, defaultValue = "false") Boolean async,
                              @RequestParam(value = "type", required = false, defaultValue = "") String type,
                              @RequestParam(value = "pageIndex", required = false, defaultValue = "1") Integer pageIndex,
                              @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize, Model model) {
        List<String> statusList = new ArrayList<>();
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        PageRequest pageRequest = new PageRequest(pageIndex - 1, pageSize, sort);
        Page<Question> questionPage = null;
        if (PostStatusEnum.UNANSWERED_POST.getCode().equals(type)) {
            //零回答的问题
            statusList.add(PostStatusEnum.PUBLISH_POST.getCode());
            statusList.add(PostStatusEnum.RESOLVED_POST.getCode());
            questionPage = questionService.listUnansweredQuestionByStatusIn(statusList, pageRequest);
            model.addAttribute("url", "/questions/unanswered");
            model.addAttribute("type", "unanswered");
        } else if (PostStatusEnum.RESOLVED_POST.getCode().equals(type)) {
            //已解决的问题
            statusList.add(PostStatusEnum.RESOLVED_POST.getCode());
            questionPage = questionService.listQuestionsByStatusIn(statusList, pageRequest);
            model.addAttribute("url", "/questions/resolved");
            model.addAttribute("type", "resolved");
        } else {
            //所有的问题
            statusList.add(PostStatusEnum.PUBLISH_POST.getCode());
            statusList.add(PostStatusEnum.RESOLVED_POST.getCode());
            questionPage = questionService.listQuestionsByStatusIn(statusList, pageRequest);
            model.addAttribute("url", "/questions");
            model.addAttribute("type", "all");

        }
        model.addAttribute("page", questionPage);

        if (!async) {
            List<UserRankVO> userRankVOList = null;
            try {
                userRankVOList  = rankService.getUserAnswerRankTop10();
            } catch (Exception e) {
                logger.error("redis服务异常",e);
            }
            model.addAttribute("userAnswerRankList", userRankVOList);

            model.addAttribute("site_title", SiteTitleEnum.QUESTION_HOME.getTitle());

        }
        return new ModelAndView(async == true ? "home/question_list :: #tab-pane" : "home/question_list");
    }


    /**
     * 获取新增博客的界面
     *
     * @param model
     * @return
     */
    @GetMapping("/ask")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")  // 指定角色权限才能操作方法
    public String createQuestion(Model model) {
        model.addAttribute("site_title", SiteTitleEnum.QUESTION_CREATE.getTitle());
        return "home/userspace/question/add";
    }


    /**
     * 获得问题详情页面
     *
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/questions/edit/{id}")
    public String questionEdit(@PathVariable(value = "id") Long id, Model model) {
        Question question = questionService.getQuestionById(id);
        //1、判断问题是否存在
        if (question == null) {
            throw new NotFoundException("问题不存在!");
        }

        //2、判断是否可以访问
        if (Objects.equals(PostStatusEnum.DELETED_POST.getCode(), question.getStatus())) {
            throw new NotFoundException("该问题已被管理员删除!");
        }

        // 3、判断操作用户是否是的问题所有者
        User principal = null;
        if (SecurityContextHolder.getContext().getAuthentication() != null && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
                && !SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString().equals("anonymousUser")) {
            principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal != null && question.getUser().getUsername().equals(principal.getUsername())) {
            } else {
                throw new CustomException("没有权限!");
            }
        }

        model.addAttribute("question", question);
        model.addAttribute("site_title", SiteTitleEnum.QUESTION_EDIT.getTitle());
        return "home/userspace/question/edit";
    }

    /**
     * 获得问题详情页面
     *
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/questions/{id}")
    public ModelAndView questionDetail(@PathVariable(value = "id") Long id,
                                       @RequestParam(value = "async", required = false, defaultValue = "false") Boolean async,
                                       @RequestParam(value = "orderby", required = false, defaultValue = "zanSize") String orderby,
                                       @RequestParam(value = "pageIndex", required = false, defaultValue = "1") Integer pageIndex,
                                       @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                                       Model model) {
        Question question = questionService.getQuestionById(id);
        //1、判断问题是否存在
        if (question == null) {
            throw new NotFoundException("问题不存在!");
        }

        //2、判断是否可以访问
        if (Objects.equals(PostStatusEnum.DELETED_POST.getCode(), question.getStatus())) {
            throw new NotFoundException("该问题已被管理员删除!");
        }

        // 3、判断操作用户是否是的问题所有者
        User principal = null;
        boolean isOwner = false;
        if (SecurityContextHolder.getContext().getAuthentication() != null && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
                && !SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString().equals("anonymousUser")) {
            principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal != null && question.getUser().getUsername().equals(principal.getUsername())) {
                isOwner = true;
            }
        }

        //4、回答列表
        String answerOrder = "";
        List<String> statusList = new ArrayList<>();
        statusList.add(PostStatusEnum.PUBLISH_POST.getCode());
        statusList.add(PostStatusEnum.ACCEPT_POST.getCode());
        Page<Answer> answerPage = null;
        try {
            if (orderby.equals("zanSize")) {
                Sort sort = new Sort(Sort.Direction.DESC, "isAccepted", "zanSize");
                Pageable pageable = new PageRequest(pageIndex - 1, pageSize, sort);
                answerPage = answerService.listAnswerByQuestionAndStatusIn(question, statusList, pageable);
                answerOrder = "zanSize";
            } else if (orderby.equals("commentSize")) {
                Sort sort = new Sort(Sort.Direction.DESC, "isAccepted", "commentSize");
                Pageable pageable = new PageRequest(pageIndex - 1, pageSize, sort);
                answerPage = answerService.listAnswerByQuestionAndStatusIn(question, statusList, pageable);
                answerOrder = "commentSize";
            } else {
                Sort sort = new Sort(Sort.Direction.DESC, "isAccepted", "createTime");
                Pageable pageable = new PageRequest(pageIndex - 1, pageSize, sort);
                answerPage = answerService.listAnswerByQuestionAndStatusIn(question, statusList, pageable);
                answerOrder = "createTime";
            }
        } catch (Exception e) {
            e.printStackTrace();
            Sort sort = new Sort(Sort.Direction.DESC, "isAccepted", "zanSize");
            Pageable pageable = new PageRequest(0, 10, sort);
            answerPage = answerService.listAnswerByQuestionAndStatusIn(question, statusList, pageable);
        }


        model.addAttribute("question", question);
        model.addAttribute("orderby", answerOrder);
        model.addAttribute("page", answerPage);
        model.addAttribute("isOwner", isOwner);
        model.addAttribute("site_title", question.getTitle() + SiteTitleEnum.QUESTION_DETAIL_PAGE.getTitle());

        if (!async) {
            //用户排行榜
            List<UserRankVO> userRankVOList = null;
            try {
                userRankVOList = rankService.getUserAnswerRankTop10();
            } catch (Exception e) {
                logger.error("redis服务故障", e);
            }
            model.addAttribute("userAnswerRankList", userRankVOList);

            //站点关键词和描述
            model.addAttribute("site_keywords", question.getTags());
            model.addAttribute("site_content", question.getSummary());
        }
        return new ModelAndView(async == true ? "home/question_detail :: #answer-list-replace" : "home/question_detail");
    }


    /**
     * 发布问题提交
     *
     * @param question
     * @return
     */
    @PostMapping("/question")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")  // 指定角色权限才能操作方法
    public ResponseEntity<Response> saveQuestion(@RequestBody Question question) {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();        user = userService.getUserByUsername(user.getUsername());
        String guid = null;

        try {
            //1、 判断是修改还是新增
            if (question.getId() != null) {
                //修改
                Question originalQuestion = questionService.getQuestionById(question.getId());
                originalQuestion.setTitle(question.getTitle());
                originalQuestion.setContent(question.getContent());
                originalQuestion.setTags(question.getTags());
                originalQuestion.setStatus(question.getStatus());
                Timestamp nowTime = new Timestamp(System.currentTimeMillis());
                originalQuestion.setUpdateTime(nowTime);
                //summary
                int contentLength = question.getContent().length();
                String summary = null;
                if (contentLength > 150) {
                    summary = HTMLUtil.html2text(question.getContent().substring(0, 150));
                } else {
                    summary = HTMLUtil.html2text(question.getContent().substring(0, contentLength));
                }
                originalQuestion.setSummary(summary);
                questionService.saveQuestion(originalQuestion);
                guid = originalQuestion.getGuid();
            } else {
                //新增
                question.setUser(user);
                //summary
                int contentLength = question.getContent().length();
                String summary = null;
                if (contentLength > 150) {
                    summary = HTMLUtil.html2text(question.getContent().substring(0, 150));
                } else {
                    summary = HTMLUtil.html2text(question.getContent().substring(0, contentLength));
                }
                question.setSummary(summary);
                Question result = questionService.saveQuestion(question);
                guid = result.getGuid();
                //百度推送POST提交
                String url = hostName + guid;
                String[] arr = {url};
                BaiduPostUtil.Post(arr);
            }

        } catch (ConstraintViolationException e) {
            e.printStackTrace();
            return ResponseEntity.ok().body(new Response(false, ConstraintViolationExceptionHandler.getMessage(e)));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }

        return ResponseEntity.ok().body(new Response(true, "处理成功", guid));
    }

    /**
     * 浏览量+1
     *
     * @param questionId
     * @return
     */
    @PutMapping("/questions/increaseView")
    public ResponseEntity<Response> increaseView(Long questionId) {
        try {
            questionService.viewIncrease(questionId);
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(true, "问题不存在！"));
        }
        return ResponseEntity.ok().body(new Response(true, "处理成功"));
    }


    //    用户中心
    @GetMapping("/manage/questions")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")  // 指定角色权限才能操作方法
    public ModelAndView questionManage(Model model,
                                       @RequestParam(value = "async", required = false, defaultValue = "false") Boolean async,
                                       @RequestParam(value = "status", required = false, defaultValue = "false") String status,
                                       @RequestParam(value = "pageIndex", required = false, defaultValue = "1") int pageIndex,
                                       @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();        user = userService.getUserByUsername(user.getUsername());
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(pageIndex - 1, pageSize, sort);
        List<String> statusList = new ArrayList<>();
        //未解决
        if (PostStatusEnum.PUBLISH_POST.getCode().equals(status)) {
            statusList.add(PostStatusEnum.PUBLISH_POST.getCode());
            model.addAttribute("status", "publish");
        }
        //已解决
        else if (PostStatusEnum.RESOLVED_POST.getCode().equals(status)) {
            statusList.add(PostStatusEnum.RESOLVED_POST.getCode());
            model.addAttribute("status", "resolved");
        }
        //全部
        else {
            statusList.add(PostStatusEnum.PUBLISH_POST.getCode());
            statusList.add(PostStatusEnum.RESOLVED_POST.getCode());
            model.addAttribute("status", "all");
        }
        Page<Question> page = questionService.listQuestionsByUserAndStatusIn(user, statusList, pageable);

        model.addAttribute("page", page);
        model.addAttribute("site_title", SiteTitleEnum.MY_QUESTION_MANAGE.getTitle());
        return new ModelAndView(async == true ? "home/userspace/question/question-list :: #tab-pane" : "home/userspace/question/question-list");
    }

    //后台
    @GetMapping("/admin/question")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")  // 指定角色权限才能操作方法
    public ModelAndView adminArticleList(
            @RequestParam(value = "status", required = false, defaultValue = "all") String status,
            @RequestParam(value = "async", required = false, defaultValue = "false") Boolean async,
            @RequestParam(value = "pageIndex", required = false, defaultValue = "1") Integer pageIndex,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize, Model model) {

        List<String> statusList = new ArrayList<>();
        Page<Question> questionPage = null;
        PageRequest pageRequest = new PageRequest(pageIndex - 1, pageSize);
        //未解决
        if (PostStatusEnum.PUBLISH_POST.getCode().equals(status)) {
            statusList.add(PostStatusEnum.PUBLISH_POST.getCode());
            questionPage = questionService.listQuestionsByStatusIn(statusList, pageRequest);
            model.addAttribute("status", status);
        }
        //已解决
        else if (PostStatusEnum.RESOLVED_POST.getCode().equals(status)) {
            statusList.add(PostStatusEnum.DELETED_POST.getCode());
            questionPage = questionService.listQuestionsByStatusIn(statusList, pageRequest);
            model.addAttribute("status", status);
        }
        //已删除
        else if (PostStatusEnum.DELETED_POST.getCode().equals(status)) {
            statusList.add(PostStatusEnum.DELETED_POST.getCode());
            questionPage = questionService.listQuestionsByStatusIn(statusList, pageRequest);
            model.addAttribute("status", status);
        } else {
            questionPage = questionService.listAllQuestions(pageRequest);
            model.addAttribute("status", PostStatusEnum.ALL_POST.getCode());
        }
        model.addAttribute("page", questionPage);
        return new ModelAndView(async == true ? "admin/question/list :: #mainContainerReplace" : "admin/question/list");
    }

    /**
     * 删除回答
     *
     * @return
     */
    @DeleteMapping("/admin/question/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")  // 指定角色权限才能操作方法
    public ResponseEntity<Response> delete(@PathVariable("id") Long id) {
        try {
            questionService.removeQuestion(id);
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
    @PutMapping("/admin/question/restore/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")  // 指定角色权限才能操作方法
    public ResponseEntity<Response> restore(@PathVariable("id") Long id) {
        try {
            Question question = questionService.getQuestionById(id);
            question.setStatus(PostStatusEnum.PUBLISH_POST.getCode());
            questionService.saveQuestion(question);
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.ok().body(new Response(true, "处理成功"));
    }


}
