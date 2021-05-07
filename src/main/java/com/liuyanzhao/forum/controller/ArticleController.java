package com.liuyanzhao.forum.controller;

import com.liuyanzhao.forum.controller.common.BaseController;
import com.liuyanzhao.forum.entity.*;
import com.liuyanzhao.forum.enums.*;
import com.liuyanzhao.forum.exception.CustomException;
import com.liuyanzhao.forum.exception.NotFoundException;
import com.liuyanzhao.forum.service.*;
import com.liuyanzhao.forum.util.BaiduPostUtil;
import com.liuyanzhao.forum.util.ConstraintViolationExceptionHandler;
import com.liuyanzhao.forum.util.HTMLUtil;
import com.liuyanzhao.forum.util.IPUtil;
import com.liuyanzhao.forum.vo.HotArticleVO;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author 言曌
 * @date 2018/4/13 下午5:08
 */

@Controller
public class ArticleController extends BaseController {


    @Autowired
    private ArticleService articleService;

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RelationshipService relationshipService;

    @Autowired
    private TagService tagService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private RankService rankService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final String hostName = "http://codergroup.cn";

    /**
     * 文章列表
     *
     * @param orderby
     * @param keywords
     * @param async
     * @param pageIndex
     * @param pageSize
     * @param model
     * @return
     */
    @GetMapping("/articles")
    public ModelAndView articleList(
            @RequestParam(value = "keywords", required = false, defaultValue = "") String keywords,
            @RequestParam(value = "orderby", required = false, defaultValue = "new") String orderby,
            @RequestParam(value = "async", required = false, defaultValue = "false") Boolean async,
            @RequestParam(value = "pageIndex", required = false, defaultValue = "1") Integer pageIndex,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize, Model model) {


        //1、文章列表
        Page<Article> articlePage = null;
        Sort sort = null;
        if (orderby.equals("hot")) {
            sort = new Sort(Sort.Direction.DESC, "commentSize", "zanSize", "viewSize", "id");
        }
        //按访问量排序
        else if (orderby.equals("view")) {
            sort = new Sort(Sort.Direction.DESC, "viewSize", "id");
        }
        //按评论数排序
        else if (orderby.equals("comment")) {
            sort = new Sort(Sort.Direction.DESC, "commentSize", "id");
        }
        //按点赞数排序
        else if (orderby.equals("like")) {
            sort = new Sort(Sort.Direction.DESC, "zanSize", "id");
        }
        //最新查询
        else {
            sort = new Sort(Sort.Direction.DESC, "id");
        }
        PageRequest pageRequest = new PageRequest(pageIndex - 1, pageSize, sort);
        if (keywords == null) {
            List<String> statusList = new ArrayList<>();
            statusList.add(PostStatusEnum.PUBLISH_POST.getCode());
            articlePage = articleService.listArticleByStatusIn(statusList, pageRequest);
        } else {
            List<String> statusList = new ArrayList<>();
            statusList.add(PostStatusEnum.PUBLISH_POST.getCode());
            articlePage = articleService.listArticlesByStatusInAndKeywords(statusList, keywords, pageRequest);
        }
        model.addAttribute("page", articlePage);
        model.addAttribute("keywords", keywords);
        model.addAttribute("site_title", SiteTitleEnum.HOME_INDEX.getTitle());
        model.addAttribute("orderby", orderby);

        if (!async) {

            //用户排行榜
            List<UserRankVO> userRankVOList = null;
            try {
                userRankVOList = rankService.getUserArticleRankTop10();
            } catch (Exception e) {
                logger.error("redis服务故障", e);
            }
            model.addAttribute("userArticleRankList", userRankVOList);

            //推荐关键词
            List<Tag> tagList = null;
            try {
                tagList = tagService.listTags();
            } catch (Exception e) {
                logger.error("redis服务故障", e);
            }
            model.addAttribute("tagList", tagList);

            //热门文章
            List<HotArticleVO> hotArticleList = null;
            try {
                hotArticleList = articleService.listTop10HotArticles();
            } catch (Exception e) {
                logger.error("redis服务故障", e);
            }
            model.addAttribute("hotArticleList", hotArticleList);

        }
        return new ModelAndView(async == true ? "home/article_list :: #tab-pane" : "home/article_list");
    }


    /**
     * 文章详情页
     *
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/articles/{id}")
    public ModelAndView getArticleById(@PathVariable("id") Long id,
                                       @RequestParam(value = "async", required = false, defaultValue = "false") Boolean async,
                                       @RequestParam(value = "orderby", required = false, defaultValue = "new") String orderby,
                                       @RequestParam(value = "pageIndex", required = false, defaultValue = "1") Integer pageIndex,
                                       @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                                       Model model) {
        User principal = null;

        //1、判断文章是否存在
        Article article = articleService.getArticleById(id);
        if (article == null) {
            throw new NotFoundException("文章不存在!");
        }


        // 2、判断操作用户是否是博客的所有者
        boolean isArticleOwner = false;
        String username = article.getUser().getUsername();
        if (SecurityContextHolder.getContext().getAuthentication() != null && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
                && !SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString().equals("anonymousUser")) {
            principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal != null && username.equals(principal.getUsername())) {
                isArticleOwner = true;
            }
        }

        //3、判断用户是否可以访问该文章
        //作者无法看到被删除的文章
        if (isArticleOwner) {
            if (Objects.equals(PostStatusEnum.DELETED_POST.getCode(), article.getStatus())) {
                throw new NotFoundException("文章已被删除!");
            }
        } else {
            //非作者无法看到除了已发布的文章
            if (!Objects.equals(PostStatusEnum.PUBLISH_POST.getCode(), article.getStatus())) {
                throw new NotFoundException("该文章可能被作者临时隐藏，暂时无法访问!");
            }
        }

//        //4、分类列表
        if (!async) {
            List<Category> categoryList = categoryService.listCategorys(article.getUser());
            model.addAttribute("categoryList", categoryList);


            //5、判断是否关注了该用户
            Integer isFollow = 0;
            if (principal != null) {
                isFollow = relationshipService.getRelationshipBetweenUsers(principal.getId(), article.getUser().getId());
            }
            model.addAttribute("isFollow", isFollow);

            //7、站点关键词和描述
            model.addAttribute("site_keywords", article.getTags());
            model.addAttribute("site_content", article.getSummary());

        }

        //6、初始化评论
        String commentOrder = "";
        List<String> statusList = new ArrayList<>();
        statusList.add(PostStatusEnum.PUBLISH_POST.getCode());
        Page<Comment> commentPage = null;
        try {
            if (orderby.equals("hot")) { // 最热查询
                Sort sort = new Sort(Sort.Direction.DESC, "isSticky", "zanSize", "id");
                Pageable pageable = new PageRequest(pageIndex - 1, pageSize, sort);
                commentPage = commentService.listCommentByArticleAndStatusIn(article, statusList, pageable);
                commentOrder = "hot";
            } else { // 最新查询
                Sort sort = new Sort(Sort.Direction.DESC, "isSticky", "id");
                Pageable pageable = new PageRequest(pageIndex - 1, pageSize, sort);
                commentPage = commentService.listCommentByArticleAndStatusIn(article, statusList, pageable);
                commentOrder = "new";

            }
        } catch (Exception e) {
            e.printStackTrace();
            Sort sort = new Sort(Sort.Direction.DESC, "isSticky", "zanSize", "id");
            Pageable pageable = new PageRequest(0, 10, sort);
            commentPage = commentService.listCommentByArticleAndStatusIn(article, statusList, pageable);
        }

        model.addAttribute("isArticleOwner", isArticleOwner);
        model.addAttribute("article", article);
        model.addAttribute("page", commentPage);
        model.addAttribute("commentOrder", commentOrder);
        model.addAttribute("commentSize", article.getCommentSize());

        model.addAttribute("site_title", article.getTitle() + SiteTitleEnum.BLOG_DETAIL_PAGE.getTitle());
        return new ModelAndView(async == true ? "home/article_detail :: #comment" : "home/article_detail");
    }


    //    用户中心
    @GetMapping("/manage/articles")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")  // 指定角色权限才能操作方法
    public ModelAndView articleList(Model model,
                                    @RequestParam(value = "status", required = false, defaultValue = "all") String status,
                                    @RequestParam(value = "keywords", required = false, defaultValue = "") String keywords,
                                    @RequestParam(value = "async", required = false, defaultValue = "false") Boolean async,
                                    @RequestParam(value = "pageIndex", required = false, defaultValue = "1") int pageIndex,
                                    @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Sort sort = new Sort(Sort.Direction.DESC, "isSticky", "id");

        Pageable pageable = new PageRequest(pageIndex - 1, pageSize, sort);
        String postStatus = "";
        //全部
        Page<Article> page = null;
        List<String> statusList = new ArrayList<>();
        if (PostStatusEnum.ALL_POST.getCode().equals(status)) {
            statusList.add(PostStatusEnum.PUBLISH_POST.getCode());
            statusList.add(PostStatusEnum.DRAFT_POST.getCode());
            statusList.add(PostStatusEnum.PRIVATE_POST.getCode());
            postStatus = "all";
        }
        //已发布
        else if (PostStatusEnum.PUBLISH_POST.getCode().equals(status)) {
            statusList.add(status);
            postStatus = "publish";
        }
        //草稿
        else if (PostStatusEnum.DRAFT_POST.getCode().equals(status)) {
            statusList.add(status);
            postStatus = "draft";
        }
        //私密
        else if (PostStatusEnum.PRIVATE_POST.getCode().equals(status)) {
            statusList.add(status);
            postStatus = "private";
        }
        //其他
        else {
            statusList.add(PostStatusEnum.PUBLISH_POST.getCode());
            postStatus = "publish";
        }
        page = articleService.listArticlesByUserAndStatusInAndKeywords(user, statusList, keywords, pageable);

        model.addAttribute("page", page);
        model.addAttribute("site_title", SiteTitleEnum.BLOG_MANAGE.getTitle());
        model.addAttribute("status", postStatus);
        model.addAttribute("keywords", keywords);
        return new ModelAndView(async == true ? "home/userspace/article/list :: #tab-pane" : "home/userspace/article/list");
    }

    /**
     * 获取新增博客的界面
     *
     * @param model
     * @return
     */
    @GetMapping("/post-new")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")  // 指定角色权限才能操作方法
    public String createArticle(Model model) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Category> categoryList = categoryService.listCategorys(user);

        model.addAttribute("categoryList", categoryList);

        model.addAttribute("site_title", SiteTitleEnum.BLOG_CREATE.getTitle());
        return "home/userspace/article/add";
    }


    /**
     * 获取编辑博客的界面
     *
     * @param model
     * @return
     */
    @GetMapping("/manage/article/edit/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")  // 指定角色权限才能操作方法
    public String editArticle(@PathVariable("id") Long id, Model model) {
        Article article = null;
        try {
            article = articleService.getArticleById(id);
        } catch (Exception e) {
            throw new NotFoundException("文章不存在！");
        }
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!Objects.equals(user.getUsername(), article.getUser().getUsername())) {
            throw new CustomException("没有权限操作！");
        }

        List<Category> categoryList = categoryService.listCategorys(user);

        model.addAttribute("article", article);
        model.addAttribute("categoryList", categoryList);

        model.addAttribute("site_title", SiteTitleEnum.BLOG_EDIT.getTitle());
        return "home/userspace/article/edit";
    }


    @PostMapping("/manage/article/stick")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")  // 指定角色权限才能操作方法
    public ResponseEntity<Response> stickArticle(Long id) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        //判断文章不存在
        Article article = articleService.getArticleById(id);
        if (article == null) {
            return ResponseEntity.ok().body(new Response(false, "文章不存在！"));
        }

        //判断文章是否属于主人
        if (article.getUser().getUsername().equals(user.getUsername())) {
            article.setIsSticky(PostStickyEnum.STICKY_POST.getCode());
            articleService.saveArticle(article);
        } else {
            return ResponseEntity.ok().body(new Response(false, "没有权限！"));
        }
        return ResponseEntity.ok().body(new Response(true, "操作成功！"));
    }

    @PostMapping("/manage/article/cancelStick")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")  // 指定角色权限才能操作方法
    public ResponseEntity<Response> cancelStickArticle(Long id) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        //判断文章不存在
        Article article = articleService.getArticleById(id);
        if (article == null) {
            return ResponseEntity.ok().body(new Response(false, "文章不存在！"));
        }

        //判断文章是否属于主人
        if (article.getUser().getUsername().equals(user.getUsername())) {
            article.setIsSticky(PostStickyEnum.NOT_STICKY_POST.getCode());
            articleService.saveArticle(article);
        } else {
            return ResponseEntity.ok().body(new Response(false, "没有权限！"));
        }
        return ResponseEntity.ok().body(new Response(true, "操作成功！"));
    }

    /**
     * 发布文章提交
     *
     * @param article
     * @return
     */
    @PostMapping("/article/edit")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")  // 指定角色权限才能操作方法
    public ResponseEntity<Response> saveArticle(@RequestBody Article article) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Category category = null;
        //1、判断分类
        if (article.getCategory().getId() == null) {
            return ResponseEntity.ok().body(new Response(false, "未选择分类"));
        } else {
            //处理分类
            category = categoryService.getCategoryByUserAndId(user, article.getCategory().getId());
            if (category == null) {
                return ResponseEntity.ok().body(new Response(false, "分类不存在！"));
            }
        }


        String guid = null;


        try {
            //3、 判断是修改还是新增
            if (article.getId() != null) {
                //修改

                Article originalArticle = articleService.getArticleById(article.getId());
                originalArticle.setTitle(article.getTitle());
                originalArticle.setContent(article.getContent());
                originalArticle.setCategory(category);
                originalArticle.setTags(article.getTags());
                originalArticle.setStatus(article.getStatus());
                originalArticle.setIsAllowComment(article.getIsAllowComment());
                originalArticle.setIsSticky(article.getIsSticky());
                int articleContentLength = article.getContent().length();
                //summary
                String summary = null;
                if (articleContentLength > 150) {
                    summary = HTMLUtil.html2text(article.getContent().substring(0, 150));
                } else {
                    summary = HTMLUtil.html2text(article.getContent().substring(0, articleContentLength));
                }
                originalArticle.setSummary(summary);
                articleService.saveArticle(originalArticle);
                guid = originalArticle.getGuid();
                //百度推送POST提交
                String url = hostName + guid;
                String[] arr = {url};
                BaiduPostUtil.Post(arr);

            } else {

                //用户
                user = userService.getUserByUsername(user.getUsername());
                article.setUser(user);
                int articleContentLength = article.getContent().length();
                //summary
                String summary = "";
                if (articleContentLength > 150) {
                    summary = HTMLUtil.html2text(article.getContent().substring(0, 150));
                } else {
                    summary = HTMLUtil.html2text(article.getContent().substring(0, articleContentLength));
                }
                article.setSummary(summary);
                article.setIsSticky(PostStickyEnum.NOT_STICKY_POST.getCode());
                article = articleService.saveArticle(article);

                guid = article.getGuid();

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

        logger.info("IP" + IPUtil.getIpAddr(request) + "发布了文章:" + article.getTitle());
        return ResponseEntity.ok().body(new Response(true, "处理成功", guid));
    }


    /**
     * 删除文章
     *
     * @param id
     * @return
     */
    @DeleteMapping("/article/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")  // 指定角色权限才能操作方法
    public ResponseEntity<Response> deleteArticle(@PathVariable("id") Long id) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Article article = null;
        //1、判断文章是否存在
        try {
            article = articleService.getArticleById(id);
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, "文章不存在!"));

        }

        //2、判断文章是否属于该用户
        if (!user.getUsername().equals(article.getUser().getUsername())) {
            return ResponseEntity.ok().body(new Response(false, "没有权限!"));
        }

        //3、开始删除
        try {
            articleService.removeArticle(article);
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }

        return ResponseEntity.ok().body(new Response(true, "处理成功"));
    }

    /**
     * 开启评论
     *
     * @param articleId
     * @return
     */
    @PostMapping("/article/openComment")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")  // 指定角色权限才能操作方法
    public ResponseEntity<Response> openComment(Long articleId) {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Article article = null;
        //1、判断文章是否存在
        try {
            article = articleService.getArticleById(articleId);
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, "文章不存在!"));

        }

        //2、判断文章是否属于该用户
        if (!user.getUsername().equals(article.getUser().getUsername())) {
            return ResponseEntity.ok().body(new Response(false, "没有权限!"));
        }

        //3、开始更新
        try {
            article.setIsAllowComment(AllowCommentEnum.ALLOW_COMMENT.getCode());
            articleService.saveArticle(article);
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }

        return ResponseEntity.ok().body(new Response(true, "处理成功"));
    }

    /**
     * 关闭评论
     *
     * @param articleId
     * @return
     */
    @PostMapping("/article/closeComment")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")  // 指定角色权限才能操作方法
    public ResponseEntity<Response> closeComment(Long articleId) {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Article article = null;
        //1、判断文章是否存在
        try {
            article = articleService.getArticleById(articleId);
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, "文章不存在!"));

        }

        //2、判断文章是否属于该用户
        if (!user.getUsername().equals(article.getUser().getUsername())) {
            return ResponseEntity.ok().body(new Response(false, "没有权限!"));
        }

        //3、开始更新
        try {
            article.setIsAllowComment(AllowCommentEnum.NOT_ALLOW_COMMENT.getCode());
            articleService.saveArticle(article);
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }

        return ResponseEntity.ok().body(new Response(true, "处理成功"));
    }

    /**
     * 增加访问量
     *
     * @param articleId
     * @return
     */
    @PutMapping("/article/increaseView")
    @ResponseBody
    public ResponseEntity<Response> increaseView(Long articleId) {
        Article article = null;
        try {
            article = articleService.viewIncrease(articleId);
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(true, "文章不存在！"));
        }
        return ResponseEntity.ok().body(new Response(true, "处理成功", article.getViewSize()));
    }

    //    后台
    @GetMapping("/admin/article")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")  // 指定角色权限才能操作方法
    public ModelAndView adminArticleList(
            @RequestParam(value = "keywords", required = false, defaultValue = "") String keywords,
            @RequestParam(value = "status", required = false, defaultValue = "all") String status,
            @RequestParam(value = "orderby", required = false, defaultValue = "new") String orderby,
            @RequestParam(value = "async", required = false, defaultValue = "false") Boolean async,
            @RequestParam(value = "pageIndex", required = false, defaultValue = "1") Integer pageIndex,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize, Model model) {

        //1、文章列表
        Page<Article> articlePage = null;
        Sort sort = null;
        if (orderby.equals("hot")) {
            sort = new Sort(Sort.Direction.DESC, "commentSize", "zanSize", "viewSize", "id");
        }
        //按访问量排序
        else if (orderby.equals("view")) {
            sort = new Sort(Sort.Direction.DESC, "viewSize", "id");
        }
        //按评论数排序
        else if (orderby.equals("comment")) {
            sort = new Sort(Sort.Direction.DESC, "commentSize", "id");
        }
        //按点赞数排序
        else if (orderby.equals("like")) {
            sort = new Sort(Sort.Direction.DESC, "zanSize", "id");
        } else if (orderby.equals("bookmark")) {
            sort = new Sort(Sort.Direction.DESC, "bookmarkSize", "id");
        }
        //最新查询
        else {
            sort = new Sort(Sort.Direction.DESC, "id");
        }
        PageRequest pageRequest = new PageRequest(pageIndex - 1, pageSize, sort);
        if (keywords == null) {
            articlePage = articleService.listArticle(pageRequest);
        } else {
            List<String> statusList = new ArrayList<>();
            if (Objects.equals(PostStatusEnum.ALL_POST.getCode(), status)) {
                articlePage = articleService.listArticlesByKeywords(keywords, pageRequest);
            } else {
                statusList.add(status);
                articlePage = articleService.listArticlesByStatusInAndKeywords(statusList, keywords, pageRequest);
            }
        }

        if (!async) {
            Long publishSize = articleService.countArticleByStatus(PostStatusEnum.PUBLISH_POST.getCode());
            Long draftSize = articleService.countArticleByStatus(PostStatusEnum.DRAFT_POST.getCode());
            Long privateSize = articleService.countArticleByStatus(PostStatusEnum.PRIVATE_POST.getCode());
            Long deleteSize = articleService.countArticleByStatus(PostStatusEnum.DELETED_POST.getCode());
            Long allSize = publishSize + draftSize + privateSize + deleteSize;

            model.addAttribute("allSize", allSize);
            model.addAttribute("publishSize", publishSize);
            model.addAttribute("draftSize", draftSize);
            model.addAttribute("privateSize", privateSize);
            model.addAttribute("deleteSize", deleteSize);
        }

        model.addAttribute("page", articlePage);
        model.addAttribute("keywords", keywords);
        model.addAttribute("site_title", SiteTitleEnum.HOME_INDEX.getTitle());
        model.addAttribute("orderby", orderby);
        model.addAttribute("status", status);
        return new ModelAndView(async == true ? "admin/article/list :: #mainContainerReplace" : "admin/article/list");
    }


    @DeleteMapping("/admin/article/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")  // 指定角色权限才能操作方法
    public ResponseEntity<Response> deleteCommentByAdmin(@PathVariable("id") Long id) {
        try {
            Article article = articleService.getArticleById(id);
            articleService.removeArticle(article);
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.ok().body(new Response(true, "处理成功"));
    }


    /**
     * 还原文章
     *
     * @param id
     * @return
     */
    @PutMapping("/admin/article/restore/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")  // 指定角色权限才能操作方法
    public ResponseEntity<Response> restoreComment(@PathVariable("id") Long id) {
        try {
            Article article = articleService.getArticleById(id);
            article.setStatus(PostStatusEnum.PUBLISH_POST.getCode());
            articleService.saveArticle(article);
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.ok().body(new Response(true, "处理成功"));
    }


    /**
     * 批量删除
     *
     * @param ids 库Id列表
     * @return JsonResult
     */
    @PostMapping(value = "/admin/article/batchRemove")
    @ResponseBody
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<Response> batchRemove(@RequestParam("ids") String ids) {
        String[] arr = ids.split(",");
        List<Long> list = new ArrayList<>();
        for (int i = 0; i < arr.length; i++) {
            list.add(Long.valueOf(arr[i]));
        }
        try {
            articleService.batchRemove(list);
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.ok().body(new Response(true, "处理成功"));
    }

    /**
     * 清空库
     *
     * @return JsonResult
     */
    @PostMapping(value = "/admin/article/clear")
    @ResponseBody
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<Response> clearAll() {
        try {
            articleService.deleteByStatus(PostStatusEnum.DELETED_POST.getCode());
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.ok().body(new Response(true, "清空成功！"));
    }

}
