package com.liuyanzhao.forum.controller;

import com.liuyanzhao.forum.controller.common.BaseController;
import com.liuyanzhao.forum.entity.*;
import com.liuyanzhao.forum.enums.PostStatusEnum;
import com.liuyanzhao.forum.enums.SiteTitleEnum;
import com.liuyanzhao.forum.exception.NotFoundException;
import com.liuyanzhao.forum.service.*;
import com.liuyanzhao.forum.vo.UserSessionVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;


/**
 * 个人主页
 *
 * @author 言曌
 * @date 2018/4/24 下午1:15
 */


@Controller
public class HomePageController extends BaseController {


    @Autowired
    private UserService userService;

    @Autowired
    private RelationshipService relationshipService;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private AnswerService answerService;

    @GetMapping(value = "/main")
    public String homePage(HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        } else {
            UserSessionVO user = (UserSessionVO) session.getAttribute("user");
            String username = user.getUsername();
            return "redirect:/" + username +"/articles";
        }
    }


//    /**
//     * 修复获得完整的，比如文件名：ad.png,sitemap.txt
//     *
//     * @param username
//     * @return
//     */
//    @GetMapping(value = "/{username:.*}")
//    public String homePage(@PathVariable("username") String username) {
//        if (username.indexOf('.') == -1) {
//            return "forward:/" + username + "/articles";
//        } else {
//            return "forward:/static/" + username;
//        }
//    }


    /**
     * 用户主页
     *
     * @param username
     * @param model
     * @return
     */
    @GetMapping(value = "/{username}/articles")
    public ModelAndView articleList(@PathVariable("username") String username,
                                    @RequestParam(value = "orderby", required = false, defaultValue = "new") String orderby,
                                    @RequestParam(value = "category", required = false) Long categoryId,
                                    @RequestParam(value = "keywords", required = false, defaultValue = "") String keywords,
                                    @RequestParam(value = "async", required = false, defaultValue = "false") Boolean async,
                                    @RequestParam(value = "pageIndex", required = false, defaultValue = "1") Integer pageIndex,
                                    @RequestParam(value = "pageSize", required = false, defaultValue = "15") Integer pageSize, Model model) {

        //1、判断用户是否存在
        User user = userService.getUserByUsername(username);
        if (user == null) {
            throw new NotFoundException("用户或页面不存在哦！");
        }

        //2、文章列表
        Page<Article> articlePage = null;
        Sort sort = null;
        //按访问量排序
        if (orderby.equals("viewSize")) {
            sort = new Sort(Sort.Direction.DESC, "isSticky", "viewSize");
            model.addAttribute("orderby", orderby);

        }
        //按评论数排序
        else if (orderby.equals("commentSize")) {
            sort = new Sort(Sort.Direction.DESC, "isSticky", "commentSize");
            model.addAttribute("orderby", orderby);
        }
        //按点赞数排序
        else if (orderby.equals("zanSize")) {
            sort = new Sort(Sort.Direction.DESC, "isSticky", "zanSize");
            model.addAttribute("orderby", orderby);
        }
        //最新查询
        else {
            sort = new Sort(Sort.Direction.DESC, "isSticky", "id");
            model.addAttribute("orderby", "createTime");

        }
        PageRequest pageRequest = new PageRequest(pageIndex - 1, pageSize, sort);

        //分类查询
        List<String> statusList = new ArrayList<>();
        statusList.add(PostStatusEnum.PUBLISH_POST.getCode());
        if (categoryId != null && categoryId > 0) {
            Category category = categoryService.getCategoryByUserAndId(user, categoryId);
            if (category == null) {
                throw new NotFoundException("分类不存在！");
            }
            articlePage = articleService.listArticlesByCategoryAndStatusInAndKeywords(category, statusList, keywords, pageRequest);
        }
        //没有分类查询
        else {
            articlePage = articleService.listArticlesByUserAndStatusInAndKeywords(user, statusList, keywords, pageRequest);
        }

        if (!async) {
            //2、判断当前用户是否为该博客主人
            User principal = null;
            Boolean isOwner = false;
            if (SecurityContextHolder.getContext().getAuthentication() != null && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
                    && !SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString().equals("anonymousUser")) {
                principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                if (principal != null && username.equals(principal.getUsername())) {
                    isOwner = true;
                }
            }
            model.addAttribute("isOwner", isOwner);

            //3、判断是否关注了他
            Integer isFollow = 0;
            if (principal != null) {
                isFollow = relationshipService.getRelationshipBetweenUsers(principal.getId(), user.getId());
            }
            model.addAttribute("isFollow", isFollow);

        }

        model.addAttribute("page", articlePage);
        model.addAttribute("keywords", keywords);
        model.addAttribute("category", categoryId);
        model.addAttribute("user", user);
        model.addAttribute("site_title", user.getUsername() + SiteTitleEnum.USER_HOME_PAGE.getTitle());
        return new ModelAndView(async == true ? "home/homepage/article :: #tab-pane" : "home/homepage/article");
    }


    /**
     * 该用户提出的问题
     *
     * @param username
     * @param async
     * @param pageIndex
     * @param pageSize
     * @param model
     * @return
     */
    @GetMapping(value = "/{username}/questions")
    public ModelAndView questionList(@PathVariable("username") String username,
                                     @RequestParam(value = "async", required = false, defaultValue = "false") Boolean async,
                                     @RequestParam(value = "pageIndex", required = false, defaultValue = "1") Integer pageIndex,
                                     @RequestParam(value = "pageSize", required = false, defaultValue = "15") Integer pageSize, Model model) {
        //1、判断用户是否存在
        User user = userService.getUserByUsername(username);
        if (user == null) {
            throw new NotFoundException("用户或页面不存在哦！");
        }

        if (!async) {
            //2、判断当前用户是否为该博客主人
            User principal = null;
            Boolean isOwner = false;
            if (SecurityContextHolder.getContext().getAuthentication() != null && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
                    && !SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString().equals("anonymousUser")) {
                principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                if (principal != null && username.equals(principal.getUsername())) {
                    isOwner = true;
                }
            }
            model.addAttribute("isOwner", isOwner);

            //3、判断是否关注了他
            Integer isFollow = 0;
            if (principal != null) {
                isFollow = relationshipService.getRelationshipBetweenUsers(principal.getId(), user.getId());
            }
            model.addAttribute("isFollow", isFollow);

        }
        //4、回答列表
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        PageRequest pageRequest = new PageRequest(pageIndex - 1, pageSize, sort);
        List<String> statusList = new ArrayList<>();
        statusList.add(PostStatusEnum.PUBLISH_POST.getCode());
        statusList.add(PostStatusEnum.RESOLVED_POST.getCode());
        Page<Question> questionPage = questionService.listQuestionsByUserAndStatusIn(user, statusList, pageRequest);


        model.addAttribute("page", questionPage);
        model.addAttribute("user", user);
        model.addAttribute("site_title", user.getUsername() + SiteTitleEnum.USER_HOME_PAGE.getTitle());
        return new ModelAndView(async == true ? "home/homepage/question :: #tab-pane" : "home/homepage/question");
    }

    /**
     * 该用户提出的问题
     *
     * @param username
     * @param async
     * @param pageIndex
     * @param pageSize
     * @param model
     * @return
     */
    @GetMapping(value = "/{username}/answers")
    public ModelAndView answerList(@PathVariable("username") String username,
                                   @RequestParam(value = "async", required = false, defaultValue = "false") Boolean async,
                                   @RequestParam(value = "pageIndex", required = false, defaultValue = "1") Integer pageIndex,
                                   @RequestParam(value = "pageSize", required = false, defaultValue = "15") Integer pageSize, Model model) {
        //1、判断用户是否存在
        User user = userService.getUserByUsername(username);
        if (user == null) {
            throw new NotFoundException("用户或页面不存在哦！");
        }


        if (!async) {
            //2、判断当前用户是否为该博客主人
            User principal = null;
            Boolean isOwner = false;
            if (SecurityContextHolder.getContext().getAuthentication() != null && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
                    && !SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString().equals("anonymousUser")) {
                principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                if (principal != null && username.equals(principal.getUsername())) {
                    isOwner = true;
                }
            }
            model.addAttribute("isOwner", isOwner);

            //3、判断是否关注了他
            Integer isFollow = 0;
            if (principal != null) {
                isFollow = relationshipService.getRelationshipBetweenUsers(principal.getId(), user.getId());
            }
            model.addAttribute("isFollow", isFollow);

        }
        //4、回答列表
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        PageRequest pageRequest = new PageRequest(pageIndex - 1, pageSize, sort);
        List<String> statusList = new ArrayList<>();
        statusList.add(PostStatusEnum.PUBLISH_POST.getCode());
        Page<Answer> answerPage = answerService.listAnswersByUserAndStatusIn(user, statusList, pageRequest);
        model.addAttribute("page", answerPage);
        model.addAttribute("user", user);
        model.addAttribute("site_title", user.getUsername() + SiteTitleEnum.USER_HOME_PAGE.getTitle());
        return new ModelAndView(async == true ? "home/homepage/answer :: #tab-pane" : "home/homepage/answer");
    }


    //关系页面
    @GetMapping(value = "/{username}/follows")
    public ModelAndView followList(@PathVariable("username") String username,
                                   @RequestParam(value = "async", required = false, defaultValue = "false") Boolean async,
                                   @RequestParam(value = "pageIndex", required = false, defaultValue = "1") Integer pageIndex,
                                   @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize, Model model) {
        //1、判断用户是否存在
        User user = userService.getUserByUsername(username);
        if (user == null) {
            throw new NotFoundException("用户或页面不存在哦！");
        }

        if (!async) {
            //2、判断当前用户是否为该博客主人
            User principal = null;
            Boolean isOwner = false;
            if (SecurityContextHolder.getContext().getAuthentication() != null && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
                    && !SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString().equals("anonymousUser")) {
                principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                if (principal != null && username.equals(principal.getUsername())) {
                    isOwner = true;
                }
            }
            model.addAttribute("isOwner", isOwner);

            //3、判断是否关注了他
            Integer isFollow = 0;
            if (principal != null) {
                isFollow = relationshipService.getRelationshipBetweenUsers(principal.getId(), user.getId());
            }
            model.addAttribute("isFollow", isFollow);

        }
        //4、分页显示
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        PageRequest pageRequest = new PageRequest(pageIndex - 1, pageSize, sort);
        Page<User> userPage = relationshipService.listFollows(user.getId(), pageRequest);
        model.addAttribute("page", userPage);
        model.addAttribute("user", user);
        model.addAttribute("site_title", user.getUsername() + SiteTitleEnum.USER_HOME_PAGE.getTitle());
        return new ModelAndView(async == true ? "home/homepage/follow :: #tab-pane" : "home/homepage/follow");
    }

    //关系页面
    @GetMapping(value = "/{username}/fans")
    public ModelAndView fanList(@PathVariable("username") String username,
                                @RequestParam(value = "async", required = false, defaultValue = "false") Boolean async,
                                @RequestParam(value = "pageIndex", required = false, defaultValue = "1") Integer pageIndex,
                                @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize, Model model) {
        //1、判断用户是否存在
        User user = userService.getUserByUsername(username);
        if (user == null) {
            throw new NotFoundException("用户或页面不存在哦！");
        }

        if (!async) {
            //2、判断当前用户是否为该博客主人
            User principal = null;
            Boolean isOwner = false;
            if (SecurityContextHolder.getContext().getAuthentication() != null && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
                    && !SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString().equals("anonymousUser")) {
                principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                if (principal != null && username.equals(principal.getUsername())) {
                    isOwner = true;
                }
            }
            model.addAttribute("isOwner", isOwner);

            //3、判断是否关注了他
            Integer isFollow = 0;
            if (principal != null) {
                isFollow = relationshipService.getRelationshipBetweenUsers(principal.getId(), user.getId());
            }
            model.addAttribute("isFollow", isFollow);

        }
        //4、分页显示
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        PageRequest pageRequest = new PageRequest(pageIndex - 1, pageSize, sort);
        Page<User> userPage = relationshipService.listFans(user.getId(), pageRequest);
        model.addAttribute("page", userPage);
        model.addAttribute("user", user);
        model.addAttribute("site_title", user.getUsername() + SiteTitleEnum.USER_HOME_PAGE.getTitle());
        return new ModelAndView(async == true ? "home/homepage/fan :: #tab-pane" : "home/homepage/fan");
    }


}
