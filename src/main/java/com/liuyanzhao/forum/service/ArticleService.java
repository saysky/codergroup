package com.liuyanzhao.forum.service;

import com.liuyanzhao.forum.entity.Category;
import com.liuyanzhao.forum.entity.Article;
import com.liuyanzhao.forum.entity.User;
import com.liuyanzhao.forum.vo.HotArticleVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author 言曌
 * @date 2018/4/2 上午9:02
 */

public interface ArticleService {

    Page<Article> listArticle(Pageable pageable);


    /**
     * 用户
     *
     * @param status
     * @param pageable
     * @return
     */
    Page<Article> listArticleByStatusIn(List<String> status, Pageable pageable);


    /**
     * 状态、关键词
     *
     * @param statusList
     * @param keywords
     * @param pageable
     * @return
     */
    Page<Article> listArticlesByStatusInAndKeywords(List<String> statusList, String keywords, Pageable pageable);

    /**
     * 关键词
     *
     * @param keywords
     * @param pageable
     * @return
     */
    Page<Article> listArticlesByKeywords(String keywords, Pageable pageable);


    /**
     * 用户、关键词
     *
     * @param user
     * @param keywords
     * @param pageable
     * @return
     */
    Page<Article> listArticlesByUserAndKeywords(User user, String keywords, Pageable pageable);

    /**
     * 用户、状态、关键字查询
     *
     * @param user
     * @param statusList
     * @param keywords
     * @param pageable
     * @return
     */
    Page<Article> listArticlesByUserAndStatusInAndKeywords(User user, List<String> statusList, String keywords, Pageable pageable);


    /**
     * 分类、状态、关键词
     *
     * @param category
     * @param status
     * @param keywords
     * @param pageable
     * @return
     */
    Page<Article> listArticlesByCategoryAndStatusInAndKeywords(Category category, List<String> status, String keywords, Pageable pageable);

    /**
     * 是否置顶、状态
     *
     * @param user
     * @param sticky
     * @param status
     * @param pageable
     * @return
     */
    Page<Article> listArticlesByStickyAndStatusIn(User user, Integer sticky, List<String> status, Pageable pageable);


    /**
     * 保存Article
     *
     * @param article
     * @return
     */
    Article saveArticle(Article article);


    /**
     * 删除Article
     *
     * @param article
     * @return
     */
    boolean removeArticle(Article article);

    /**
     * 根据id获取Article
     *
     * @param id
     * @return
     */
    Article getArticleById(Long id);


    /**
     * 阅读量递增
     *
     * @param id
     * @return
     */
    Article viewIncrease(Long id);


    /**
     * 点赞
     *
     * @param articleId
     * @return
     */
    void createZan(Long articleId);


    /**
     * 获取热门文章前十
     *
     * @return
     */
    List<HotArticleVO> listTop10HotArticles();


    /**
     * 统计不同状态的文章数
     * @param status
     * @return
     */
    Long countArticleByStatus(String status);

    /**
     * 根据标签获得文章列表
     * @param tagName
     * @param pageable
     * @return
     */
    Page<Article> listArticlesByTag(String tagName,Pageable pageable);



    void deleteByStatus(String status);

    /**
     * 批量删除
     * @param ids
     */
    void batchRemove(List<Long> ids);
}
