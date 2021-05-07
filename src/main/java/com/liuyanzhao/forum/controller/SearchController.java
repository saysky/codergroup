package com.liuyanzhao.forum.controller;

import com.liuyanzhao.forum.entity.Article;
import com.liuyanzhao.forum.entity.Question;
import com.liuyanzhao.forum.enums.PostStatusEnum;
import com.liuyanzhao.forum.service.ArticleService;
import com.liuyanzhao.forum.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 言曌
 * @date 2018/5/23 上午11:14
 */

@Controller
@RequestMapping("/search")
public class SearchController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private QuestionService questionService;

    @GetMapping("/articles")
    public ModelAndView articleList(
            @RequestParam(value = "keywords", required = false, defaultValue = "") String keywords,
            @RequestParam(value = "orderby", required = false, defaultValue = "new") String orderby,
            @RequestParam(value = "async", required = false, defaultValue = "false") Boolean async,
            @RequestParam(value = "pageIndex", required = false, defaultValue = "1") Integer pageIndex,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize, Model model) {

        Sort sort = null;
        if ("new".equals(orderby)) {
            sort = new Sort(Sort.Direction.DESC, "id");
            model.addAttribute("orderby", "new");
        } else if ("like".equals(orderby)) {
            sort = new Sort(Sort.Direction.DESC, "zanSize");
            model.addAttribute("orderby", "like");
        } else if ("comment".equals(orderby)) {
            sort = new Sort(Sort.Direction.DESC, "commentSize");
            model.addAttribute("orderby", "comment");
        } else if ("view".equals(orderby)) {
            sort = new Sort(Sort.Direction.DESC, "viewSize");
            model.addAttribute("orderby", "view");
        } else {
            sort = new Sort(Sort.Direction.DESC, "id");
            model.addAttribute("orderby", "new");
        }


        PageRequest pageRequest = new PageRequest(pageIndex - 1, pageSize, sort);
        List<String> statusList = new ArrayList<>();
        statusList.add(PostStatusEnum.PUBLISH_POST.getCode());
        Page<Article> articlePage = null;
        if ("".equals(keywords)) {
            articlePage = articleService.listArticleByStatusIn(statusList, pageRequest);
        } else {
            articlePage = articleService.listArticlesByStatusInAndKeywords(statusList, keywords, pageRequest);
        }
        model.addAttribute("page", articlePage);
        model.addAttribute("url", "/search/articles");
        return new ModelAndView(async == true ? "home/search/article :: #tab-pane" : "home/search/article");
    }


    @GetMapping("/questions")
    public ModelAndView questionList(
            @RequestParam(value = "keywords", required = false, defaultValue = "") String keywords,
            @RequestParam(value = "orderby", required = false, defaultValue = "new") String orderby,
            @RequestParam(value = "async", required = false, defaultValue = "false") Boolean async,
            @RequestParam(value = "pageIndex", required = false, defaultValue = "1") Integer pageIndex,
            @RequestParam(value = "pageSize", required = false, defaultValue = "15") Integer pageSize, Model model) {

        Sort sort = null;
        if ("new".equals(orderby)) {
            sort = new Sort(Sort.Direction.DESC, "id");
            model.addAttribute("orderby", "new");
        } else if ("answer".equals(orderby)) {
            sort = new Sort(Sort.Direction.DESC, "answerSize");
            model.addAttribute("orderby", "answer");
        } else if ("view".equals(orderby)) {
            sort = new Sort(Sort.Direction.DESC, "viewSize");
            model.addAttribute("orderby", "view");
        } else {
            sort = new Sort(Sort.Direction.DESC, "id");
            model.addAttribute("orderby", "new");
        }

        PageRequest pageRequest = new PageRequest(pageIndex - 1, pageSize, sort);
        List<String> statusList = new ArrayList<>();
        statusList.add(PostStatusEnum.PUBLISH_POST.getCode());
        statusList.add(PostStatusEnum.RESOLVED_POST.getCode());
        Page<Question> questionPage = null;
        if ("".equals(keywords)) {
            questionPage = questionService.listQuestionsByStatusIn(statusList, pageRequest);
        } else {
            questionPage = questionService.listQuestionsByStatusInAndKeywords(statusList, keywords, pageRequest);
        }
        model.addAttribute("page", questionPage);
        model.addAttribute("orderby", orderby);
        model.addAttribute("url", "/search/questions");
        return new ModelAndView(async == true ? "home/search/question :: #tab-pane" : "home/search/question");
    }
}
