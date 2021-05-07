package com.liuyanzhao.forum.controller;

import com.alibaba.fastjson.JSON;
import com.liuyanzhao.forum.controller.common.BaseController;
import com.liuyanzhao.forum.entity.*;
import com.liuyanzhao.forum.enums.PostStatusEnum;
import com.liuyanzhao.forum.repository.SlideRepository;
import com.liuyanzhao.forum.service.*;
import com.liuyanzhao.forum.vo.UserRankVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * 所有用户都可以访问
 *
 * @author 言曌
 * @date 2018/3/19 下午3:14
 */

@Controller
public class MainController extends BaseController {


    @Autowired
    private UserService userService;


    @Autowired
    private ArticleService articleService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private BulletinService bulletinService;

    @Autowired
    private SlideRepository slideRepository;

    @Autowired
    private TagService tagService;

    @Autowired
    private RankService rankService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @GetMapping("/testsession")
    @ResponseBody
    public String test() {
        HttpSession session = request.getSession();
        System.out.println(session);
        String getMaxInactiveInterval = String.valueOf(request.getSession().getMaxInactiveInterval());
        String getCreationTime = String.valueOf(request.getSession().getCreationTime());
        String getServletContext = String.valueOf(request.getSession().getServletContext());
        return "getMaxInactiveInterval" + getMaxInactiveInterval + ",getCreationTime" + getCreationTime + ",getServletContext" + getServletContext;
    }

    @GetMapping("/")
    public ModelAndView index(@RequestParam(value = "async", required = false, defaultValue = "false") Boolean async,
                              @RequestParam(value = "pageIndex", required = false, defaultValue = "1") Integer pageIndex,
                              @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize, Model model) {

        //文章
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        PageRequest pageRequest = new PageRequest(pageIndex - 1, pageSize, sort);
        List<String> statusList = new ArrayList<>();
        statusList.add(PostStatusEnum.PUBLISH_POST.getCode());
        Page<Article> articlePage = articleService.listArticleByStatusIn(statusList, pageRequest);

        model.addAttribute("page", articlePage);

        if (!async) {
            //幻灯片
            Sort positionSort = new Sort(Sort.Direction.DESC, "position");
            List<Slide> slideList = slideRepository.findByStatus(PostStatusEnum.PUBLISH_POST.getCode(), positionSort);
            model.addAttribute("slideList", slideList);

            //公告
            PageRequest bulletinPageRequest = new PageRequest(0, 5, positionSort);
            List<Bulletin> bulletinList = bulletinService.listBulletinByStatus(PostStatusEnum.PUBLISH_POST.getCode(), bulletinPageRequest).getContent();
            model.addAttribute("bulletinList", bulletinList);
            //问答
            PageRequest questionPageRequest = new PageRequest(0, 5, sort);
            List<Question> questionList = questionService.listQuestionsByStatusIn(statusList, questionPageRequest).getContent();
            model.addAttribute("questionList", questionList);
            //用户排行榜
            List<UserRankVO> userRankVOList = null;
            try {
                userRankVOList = rankService.getUserReputationRankTop10();
            } catch (Exception e) {
                logger.error("redis服务故障", e);
            }

            List<Tag> tags = tagService.listTags();
            model.addAttribute("tags", tags);
            model.addAttribute("userReputationRankList", userRankVOList);
        }
        return new ModelAndView(async == true ? "index :: #article-box-replace" : "index");
    }

    @PostMapping("/ajax/checkUsernameOrEmail")
    @ResponseBody
    public String checkUsernameOrEmail(Integer id, String username, String email) {
        HashMap<String, Boolean> hashMap = new HashMap();
        User user = null;
        if (username != null && email == null) {
            user = userService.getUserByUsername(username);
        } else {
            user = userService.getUserByEmail(email);
        }
        //新增用户
        if (id == null) {
            if (user == null) {
                hashMap.put("valid", true);
            } else {
                hashMap.put("valid", false);
            }
        }
        //编辑用户
        else {
            //编辑的是当前的用户
            if (user == null || Objects.equals(user.getId(), id)) {
                hashMap.put("valid", true);
            } else {
                hashMap.put("valid", false);
            }
        }
        //可用

        return JSON.toJSONString(hashMap);
    }

}
