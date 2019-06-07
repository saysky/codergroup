package com.liuyanzhao.forum.controller;

import com.liuyanzhao.forum.entity.*;
import com.liuyanzhao.forum.enums.CommentStickStatusEnum;
import com.liuyanzhao.forum.enums.PostStatusEnum;
import com.liuyanzhao.forum.enums.SiteTitleEnum;
import com.liuyanzhao.forum.repository.ArticleRepository;
import com.liuyanzhao.forum.service.CommentService;
import com.liuyanzhao.forum.service.ArticleService;
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
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author 言曌
 * @date 2018/4/18 下午3:29
 */

@Controller
public class CommentController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private UserService userService;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private CommentService commentService;

    @Autowired
    private RedisOperator redis;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    /**
     * 我收到的评论
     *
     * @param model
     * @return
     */
    @GetMapping("/manage/comments")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")  // 指定角色权限才能操作方法
    public ModelAndView mySendCommentList(
            @RequestParam(value = "async", required = false, defaultValue = "false") Boolean async,
            @RequestParam(value = "type", required = false, defaultValue = "in") String type,
            @RequestParam(value = "pageIndex", required = false, defaultValue = "1") Integer pageIndex,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
            Model model) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        PageRequest pageRequest = new PageRequest(pageIndex - 1, pageSize, sort);
        List<String> statusList = new ArrayList<>();
        statusList.add(PostStatusEnum.PUBLISH_POST.getCode());
        Page<Comment> commentPage = null;
        if ("in".equals(type)) {
            commentPage = commentService.listRecieveComments(user, statusList, pageRequest);
            model.addAttribute("type", "in");
        } else {
            commentPage = commentService.listSendComments(user, statusList, pageRequest);
            model.addAttribute("type", "out");
        }
        model.addAttribute("page", commentPage);
        if (!async) {
            Long sendCommentSize = commentService.countByUserAndStatusIn(user, statusList);
            Long recieveCommentSize = commentService.countByReplyUserAndStatusIn(user, statusList);
            model.addAttribute("sendCommentSize", sendCommentSize);
            model.addAttribute("recieveCommentSize", recieveCommentSize);
        }
        model.addAttribute("site_title", SiteTitleEnum.COMMENT_MANAGE.getTitle());
        return new ModelAndView(async == true ? "home/userspace/comment :: #tab-pane" : "home/userspace/comment");
    }


    /**
     * 发表评论
     *
     * @param articleId
     * @param commentId
     * @param commentContent
     * @return
     */
    @PostMapping("/comments")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")  // 指定角色权限才能操作方法
    public ResponseEntity<Response> createComment(Principal principal, Long articleId, Long replyId, Long commentId, String commentContent) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();        user = userService.getUserByUsername(user.getUsername());

        Article article = articleRepository.findOne(articleId);
        if (article == null) {
            return ResponseEntity.ok().body(new Response(false, "文章不存在!"));
        }
        try {
            //1、评论
            if (commentId == null) {
                Comment comment = new Comment();
                comment.setArticle(article);
                comment.setContent(commentContent);
                comment.setUser(user);
                comment.setReplyUser(article.getUser());
                commentService.saveComment(comment);
            } else {
                //回复
                commentService.replyComment(article, commentId, replyId, commentContent);
            }
            //2、修改文章的评论数目
            article.setCommentSize(commentService.countCommentSizeByArticle(article));
            articleRepository.save(article);
        } catch (ConstraintViolationException e) {
            return ResponseEntity.ok().body(new Response(false, ConstraintViolationExceptionHandler.getMessage(e)));
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.ok().body(new Response(true, "处理成功!", null));
    }

    /**
     * 删除评论
     *
     * @return
     */
    @DeleteMapping("/comments/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")  // 指定角色权限才能操作方法
    public ResponseEntity<Response> delete(@PathVariable("id") Long id) {
        boolean isOwner = false;
        boolean isAuthor = false;
        User user;

        Comment originalComment;
        Integer deleteCount = 1;//删除评论数


        try {
            originalComment = commentService.getCommentById(id);
            //如果已经到了回收站，用户不允许继续删除
            if (Objects.equals(PostStatusEnum.DELETED_POST.getCode(), originalComment.getStatus())) {
                return ResponseEntity.ok().body(new Response(false, "评论不存在！"));
            }

            user = originalComment.getUser();

            // 判断操作用户是否是评论的所有者
            User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal != null && user.getUsername().equals(principal.getUsername())) {
                isOwner = true;
            }
            //判断操作用户是否是文章作者
            if (Objects.equals(originalComment.getArticle().getUser().getUsername(), principal.getUsername())) {
                isAuthor = true;
            }

        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, "评论不存在！"));
        }

        if (!isOwner && !isAuthor) {
            return ResponseEntity.ok().body(new Response(false, "没有操作权限！"));
        }
        try {
            //1、先删除子评论
            List<Comment> commentList = originalComment.getCommentList();
            if (commentList != null && commentList.size() != 0) {
                for (int i = 0; i < commentList.size(); i++) {
                    Comment comment = commentList.get(i);
                    commentService.removeComment(comment.getId());
                    deleteCount++;
                }
            }
            //2、删除该评论
            commentService.removeComment(id);
            //3、修改文章的评论数目
            Article originalArticle = originalComment.getArticle();
            originalArticle.setCommentSize(commentService.countCommentSizeByArticle(originalArticle));
            articleRepository.save(originalArticle);
        } catch (ConstraintViolationException e) {
            return ResponseEntity.ok().body(new Response(false, ConstraintViolationExceptionHandler.getMessage(e)));
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.ok().body(new Response(true, "处理成功", deleteCount));
    }


    /**
     * 评论置顶
     *
     * @param commentId
     * @return
     */
    @PutMapping("/comments/stick")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")  // 指定角色权限才能操作方法
    public ResponseEntity<Response> stickComment(Long commentId) {
        boolean isArticleAuthor = false;
        Comment originalComment = null;
        Article article = null;
        try {
            originalComment = commentService.getCommentById(commentId);
            article = originalComment.getArticle();
            if (Objects.equals(CommentStickStatusEnum.STICKY_STATUS.getCode(), originalComment.getIsSticky())) {
                return ResponseEntity.ok().body(new Response(false, "当前状态已经是置顶！"));
            }
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, "评论不存在！"));
        }

        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal != null && article.getUser().getUsername().equals(principal.getUsername())) {
            isArticleAuthor = true;
        }

        //只有文章作者才能置顶评论
        try {
            if (isArticleAuthor) {
                originalComment.setIsSticky(CommentStickStatusEnum.STICKY_STATUS.getCode());
                commentService.updateComment(originalComment);
            } else {
                return ResponseEntity.ok().body(new Response(false, "没有权限操作！"));
            }
        } catch (ConstraintViolationException e) {
            return ResponseEntity.ok().body(new Response(false, ConstraintViolationExceptionHandler.getMessage(e)));
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.ok().body(new Response(true, "处理成功"));
    }

    /**
     * 取消评论置顶
     *
     * @param commentId
     * @return
     */
    @PutMapping("/comments/cancelStick")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")  // 指定角色权限才能操作方法
    public ResponseEntity<Response> cancelStickComment(Long commentId) {
        boolean isArticleAuthor = false;
        Comment originalComment = null;
        Article article = null;
        try {
            originalComment = commentService.getCommentById(commentId);
            article = originalComment.getArticle();
            if (Objects.equals(CommentStickStatusEnum.NOT_STICKY_STATUS.getCode(), originalComment.getIsSticky())) {
                return ResponseEntity.ok().body(new Response(false, "当前状态已经是未置顶！"));
            }
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, "评论不存在！"));
        }

        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal != null && article.getUser().getUsername().equals(principal.getUsername())) {
            isArticleAuthor = true;
        }

        //只有文章作者才能置顶评论
        try {
            if (isArticleAuthor) {
                originalComment.setIsSticky(CommentStickStatusEnum.NOT_STICKY_STATUS.getCode());
                commentService.updateComment(originalComment);
            } else {
                return ResponseEntity.ok().body(new Response(false, "没有权限操作！"));
            }
        } catch (ConstraintViolationException e) {
            return ResponseEntity.ok().body(new Response(false, ConstraintViolationExceptionHandler.getMessage(e)));
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.ok().body(new Response(true, "处理成功"));
    }

    /**
     * 后台获取评论列表
     *
     * @param model
     * @return
     */
    @GetMapping("/admin/comment")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")  // 指定角色权限才能操作方法
    public ModelAndView listComments(
            @RequestParam(value = "async", required = false, defaultValue = "false") Boolean async,
            @RequestParam(value = "status", required = false, defaultValue = "all") String status,
            @RequestParam(value = "keywords", required = false, defaultValue = "") String keywords,
            @RequestParam(value = "pageIndex", required = false, defaultValue = "1") Integer pageIndex,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
            Model model) {

        Sort sort = new Sort(Sort.Direction.DESC, "id");
        PageRequest pageRequest = new PageRequest(pageIndex - 1, pageSize, sort);
        Page<Comment> commentPage = null;
        List<String> statusList = new ArrayList<>();
        if (Objects.equals(PostStatusEnum.ALL_POST.getCode(), status)) {
            statusList.add(PostStatusEnum.PUBLISH_POST.getCode());
            statusList.add(PostStatusEnum.DELETED_POST.getCode());
        } else {
            statusList.add(status);
        }
        if (keywords == "" || keywords == null) {
            commentPage = commentService.listCommentByStatusIn(statusList, pageRequest);
        } else {
            commentPage = commentService.listCommentByStatusInAndKeyword(statusList, keywords, pageRequest);
        }

        if (!async) {
            Long publishSize = commentService.countCommentByStatus(PostStatusEnum.PUBLISH_POST.getCode());
            Long deletedSize = commentService.countCommentByStatus(PostStatusEnum.DELETED_POST.getCode());
            Long allSize = publishSize + deletedSize;
            model.addAttribute("publishSize", publishSize);
            model.addAttribute("deletedSize", deletedSize);
            model.addAttribute("allSize", allSize);
        }
        model.addAttribute("page", commentPage);
        model.addAttribute("status", status);
        return new ModelAndView(async == true ? "admin/comment/list :: #mainContainerReplace" : "admin/comment/list");
    }


    @DeleteMapping("/admin/comment/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")  // 指定角色权限才能操作方法
    public ResponseEntity<Response> deleteCommentByAdmin(@PathVariable("id") Long id) {
        try {
            commentService.removeComment(id);
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.ok().body(new Response(true, "处理成功"));
    }


    /**
     * 恢复评论
     *
     * @param id
     * @return
     */
    @PutMapping("/admin/comment/restore/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")  // 指定角色权限才能操作方法
    public ResponseEntity<Response> restoreComment(@PathVariable("id") Long id) {
        try {
            Comment comment = commentService.getCommentById(id);
            comment.setStatus(PostStatusEnum.PUBLISH_POST.getCode());
            commentService.saveComment(comment);
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.ok().body(new Response(true, "处理成功"));
    }
}
