package com.liuyanzhao.forum.service.impl;

import com.liuyanzhao.forum.entity.*;
import com.liuyanzhao.forum.enums.PostStatusEnum;
import com.liuyanzhao.forum.enums.ReputationEnum;
import com.liuyanzhao.forum.repository.CommentRepository;
import com.liuyanzhao.forum.repository.ArticleRepository;
import com.liuyanzhao.forum.repository.UserRepository;
import com.liuyanzhao.forum.service.ArticleService;
import com.liuyanzhao.forum.service.UserService;
import com.liuyanzhao.forum.service.ZanService;
import com.liuyanzhao.forum.vo.HotArticleVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author 言曌
 * @date 2018/4/2 上午9:02
 */

@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Override
    public Page<Article> listArticle(Pageable pageable) {
        return articleRepository.findAll(pageable);
    }

    @Override
    public Page<Article> listArticleByStatusIn(List<String> statusList, Pageable pageable) {
        return articleRepository.findByStatusIn(statusList, pageable);
    }

    @Override
    public Page<Article> listArticlesByStatusInAndKeywords(List<String> statusList, String keywords, Pageable pageable) {
        return articleRepository.findByStatusInAndTitleLikeOrStatusInAndTagsLike(statusList, "%" + keywords + "%", statusList, "%" + keywords + "%", pageable);
    }

    @Override
    public Page<Article> listArticlesByKeywords(String keywords, Pageable pageable) {
        return articleRepository.findByTitleLikeOrTagsLike("%" + keywords + "%", "%" + keywords + "%", pageable);
    }

    @Override
    public Page<Article> listArticlesByUserAndStatusInAndKeywords(User user, List<String> statusList, String keywords, Pageable pageable) {
        //用户主页搜索，根据内容查找
        return articleRepository.findByUserAndStatusInAndContentLike(user, statusList, "%" + keywords + "%", pageable);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Article saveArticle(Article article) {
        //发布文章
        if (article.getId() == null) {
            //1、添加文章
            Article returnArticle = articleRepository.save(article);
            returnArticle.setGuid("/articles/" + returnArticle.getId());
            //2、修改文章数和积分
            User originalUser = userRepository.findOne(article.getUser().getId());
            Long count = articleRepository.countArticleSizeByUserId(article.getUser().getId());
            Integer reputation = originalUser.getReputation();
            originalUser.setReputation(reputation + ReputationEnum.PUBLISH_ARTICLE.getCode());
            originalUser.setArticleSize(count);
            userRepository.save(originalUser);
            return returnArticle;
        } else {
            //修改文章
            return articleRepository.save(article);
        }

    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removeArticle(Article article) {

        //如果文章状态不是已删除，则先放到回收站
        if (Objects.equals(PostStatusEnum.DELETED_POST.getCode(), article.getStatus())) {
            //1、删除文章对应的评论,再删除一级评论
            commentRepository.deleteByArticle(article);
            articleRepository.delete(article);
        } else {
            //1、标记删除
            article.setStatus(PostStatusEnum.DELETED_POST.getCode());
            articleRepository.save(article);
            //2、修改文章数和积分
            Long count = articleRepository.countArticleSizeByUserId(article.getUser().getId());
            User originalUser = article.getUser();
            Integer reputation = originalUser.getReputation();
            originalUser.setReputation(reputation - ReputationEnum.PUBLISH_ARTICLE.getCode());
            originalUser.setArticleSize(count);
            userRepository.save(originalUser);
        }
        return true;
    }

    @Override

    public Article getArticleById(Long id) {
        return articleRepository.findOne(id);
    }


    @Override
    public Page<Article> listArticlesByUserAndKeywords(User user, String keywords, Pageable pageable) {
        return articleRepository.findByUserAndTitleLikeOrUserAndTagsLike(user, "%" + keywords + "%", user, "%" + keywords + "%", pageable);
    }


    @Override
    public Page<Article> listArticlesByCategoryAndStatusInAndKeywords(Category category, List<String> status, String keywords, Pageable pageable) {
        return articleRepository.findByCategoryAndStatusInAndContentLike(category, status, "%" + keywords + "%", pageable);
    }


    @Override
    public Page<Article> listArticlesByStickyAndStatusIn(User user, Integer sticky, List<String> status, Pageable pageable) {
        return articleRepository.findByUserAndIsStickyAndStatusIn(user, sticky, status, pageable);
    }


    @Override
    public Article viewIncrease(Long id) {
        //添加当前文章的cookie
        Article article = articleRepository.findOne(id);
        if (article == null) {
            return null;
        }
        article.setViewSize(article.getViewSize() + 1);
        this.saveArticle(article);
        return article;
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void createZan(Long articleId) {
        //1、添加赞
        Article originalArticle = articleRepository.findOne(articleId);
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        user = userService.getUserByUsername(user.getUsername());
        Zan zan = new Zan(user);
        boolean isExist = originalArticle.addZan(zan);
        if (isExist) {
            throw new IllegalArgumentException("您已经点过赞了！");
        }
        saveArticle(originalArticle);

        //2、修改用户积分
        User originalUser = originalArticle.getUser();
        Integer reputation = originalUser.getReputation();
        originalUser.setReputation(reputation + ReputationEnum.ARTICLE_GET_ZAN.getCode());
        userRepository.save(originalUser);

    }

    @Override
    @Cacheable(value = "1h", key = "'articles:top10hot'")
    public List<HotArticleVO> listTop10HotArticles() {
        List<HotArticleVO> hotArticleVOList = new ArrayList<>();
        List<Article> articleList = articleRepository.findTop10HotArticle();
        for (int i = 0; i < articleList.size(); i++) {
            Article article = articleList.get(i);
            HotArticleVO hotArticleVO = new HotArticleVO();
            hotArticleVO.setId(article.getId());
            hotArticleVO.setTitle(article.getTitle());
            hotArticleVO.setGuid(article.getGuid());
            hotArticleVO.setCommentSize(article.getCommentSize());
            hotArticleVOList.add(hotArticleVO);
        }
        return hotArticleVOList;
    }

    @Override
    public Long countArticleByStatus(String status) {
        return articleRepository.countByStatus(status);
    }

    @Override
    public Page<Article> listArticlesByTag(String tagName, Pageable pageable) {
        return articleRepository.findByTagsLike("%" + tagName + "%", pageable);
    }

    @Override
    @Async
    @Transactional(rollbackFor = Exception.class)
    public void deleteByStatus(String status) {
        Integer result = articleRepository.deleteByStatus(status);
        System.out.println(result);
    }

    @Override
    @Async
    @Transactional(rollbackFor = Exception.class)
    public void batchRemove(List<Long> ids) {
        for (int i = 0; i < ids.size(); i++) {
            Article article = articleRepository.findOne(ids.get(i));
            if(article != null) {
                //删除文章
                articleRepository.delete(ids.get(i));
                //删除对应的评论
                commentRepository.deleteByArticle(article);
            }
        }
    }


}
