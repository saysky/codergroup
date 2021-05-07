package com.liuyanzhao.forum.controller;

import com.liuyanzhao.forum.entity.*;
import com.liuyanzhao.forum.enums.SiteTitleEnum;
import com.liuyanzhao.forum.service.BookmarkService;
import com.liuyanzhao.forum.service.ArticleService;
import com.liuyanzhao.forum.service.UserService;
import com.liuyanzhao.forum.util.ConstraintViolationExceptionHandler;
import com.liuyanzhao.forum.vo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.ConstraintViolationException;
import java.util.Objects;


/**
 * @author 言曌
 * @date 2018/5/6 下午9:44
 */

@Controller
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")  // 指定角色权限才能操作方法
public class BookmarkController {

    @Autowired
    private BookmarkService bookmarkService;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private UserService userService;


    /**
     * @param async
     * @param pageIndex
     * @param pageSize
     * @param model
     * @return
     */
    @GetMapping("/manage/bookmarks")
    public ModelAndView list(@RequestParam(value = "async", required = false, defaultValue = "false") Boolean async,
                             @RequestParam(value = "pageIndex", required = false, defaultValue = "1") Integer pageIndex,
                             @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                             Model model) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        PageRequest pageRequest = new PageRequest(pageIndex - 1, pageSize);
        Page<Bookmark> bookmarkPage = bookmarkService.findBookmarkByUser(user, pageRequest);
        model.addAttribute("page", bookmarkPage);
        model.addAttribute("site_title", SiteTitleEnum.BOOKMARK_MANAGE.getTitle());
        return new ModelAndView(async == true ? "home/userspace/bookmark :: #right-box-body-replace" : "home/userspace/bookmark");
    }

    /**
     * 添加书签
     *
     * @param articleId
     * @return
     */
    @PostMapping("/bookmarks")
    public ResponseEntity<Response> createBookmark(Long articleId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();        user = userService.getUserByUsername(user.getUsername());
        Article article = null;
        try {
            //1、判断文章是否存在
            article = articleService.getArticleById(articleId);
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, "文章不存在！"));
        }
        //2、判断是否收藏过了
        if (bookmarkService.isMarkArticle(user, article)) {
            return ResponseEntity.ok().body(new Response(false, "您已经收藏过了，请在用户中心查看！"));
        }
        try {
            //3、添加收藏
            Bookmark bookmark = new Bookmark(user, article);
            bookmarkService.saveBookmark(bookmark);

        } catch (ConstraintViolationException e) {
            return ResponseEntity.ok().body(new Response(false, ConstraintViolationExceptionHandler.getMessage(e)));
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.ok().body(new Response(true, "收藏成功，请在用户中心查看！"));
    }

    /**
     * 删除书签
     *
     * @return
     */
    @DeleteMapping("/bookmarks/{id}")
    public ResponseEntity<Response> delete(@PathVariable("id") Long id) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();        user = userService.getUserByUsername(user.getUsername());

        Bookmark bookmark = null;
        //1、验证书签是否存在
        try {
            bookmark = bookmarkService.findById(id);
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, "书签不存在！"));
        }

        //2、验证是否是该用户的
        if (Objects.equals(bookmark.getUser().getUsername(), user.getUsername())) {
            bookmarkService.deleteBookmark(bookmark);
        } else {
            return ResponseEntity.ok().body(new Response(false, "没有权限操作！"));
        }

        return ResponseEntity.ok().body(new Response(true, "处理成功"));
    }
}
