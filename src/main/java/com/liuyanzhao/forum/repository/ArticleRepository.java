package com.liuyanzhao.forum.repository;

import com.liuyanzhao.forum.entity.Category;
import com.liuyanzhao.forum.entity.Article;
import com.liuyanzhao.forum.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigInteger;
import java.util.List;

/**
 * @author 言曌
 * @date 2018/3/21 上午11:26
 */

public interface ArticleRepository extends JpaRepository<Article, Long> {


    /**
     * 根据文章状态分页查询
     *
     * @param status
     * @param pageable
     * @return
     */
    Page<Article> findByStatusIn(List<String> status, Pageable pageable);

    /**
     * 获得某个用户的所有文章
     *
     * @param user
     * @param status
     * @param pageable
     * @return
     */
    Page<Article> findByUserAndStatusIn(User user, List<String> status, Pageable pageable);


    /**
     * 获得所有用户的文章，根据状态和标题关键字模糊查询
     *
     * @param statusList
     * @param title
     * @param statusList2
     * @param tags
     * @param pageable
     * @return
     */
    Page<Article> findByStatusInAndTitleLikeOrStatusInAndTagsLike(List<String> statusList, String title, List<String> statusList2, String tags, Pageable pageable);


    Page<Article> findByTitleLikeOrTagsLike(String title, String tags, Pageable pageable);


    /**
     * 获得某个用户的文章，根据状态和标题关键字模糊查询
     *
     * @param user
     * @param statusList
     * @param title
     * @param user2
     * @param statusList2
     * @param tags
     * @param pageable
     * @return
     */
    @Deprecated
    Page<Article> findByUserAndStatusInAndTitleLikeOrUserAndStatusInAndTagsLike(User user, List<String> statusList, String title, User user2, List<String> statusList2, String tags, Pageable pageable);


    /**
     * 根据用户和状态和文章内容模糊查找
     * @param user
     * @param statusList
     * @param content
     * @param pageable
     * @return
     */
    Page<Article> findByUserAndStatusInAndContentLike(User user, List<String> statusList, String content, Pageable pageable);


    /**
     * 根据分类和状态和文章内容模糊查找
     * @param category
     * @param statusList
     * @param content
     * @param pageable
     * @return
     */
    Page<Article> findByCategoryAndStatusInAndContentLike(Category category, List<String> statusList, String content, Pageable pageable);

    /**
     * 获得某个用户的所有文章（包括已删除的）
     *
     * @param user
     * @param title
     * @param user2
     * @param tags
     * @param pageable
     * @return
     */
    Page<Article> findByUserAndTitleLikeOrUserAndTagsLike(User user, String title, User user2, String tags, Pageable pageable);


    /**
     * 根据用户和置顶状态分页查询
     *
     * @param user
     * @param isSticky
     * @param pageable
     * @return
     */
    Page<Article> findByUserAndIsStickyAndStatusIn(User user, Integer isSticky, List<String> status, Pageable pageable);

    /**
     * 根据分类，状态，标题和关键字模糊查询
     *
     * @param category
     * @param status
     * @param title
     * @param category2
     * @param status2
     * @param tags
     * @param pageable
     * @return
     */
    @Deprecated
    Page<Article> findByCategoryAndStatusInAndTitleLikeOrCategoryAndStatusInAndTagsLike(Category category, List<String> status, String title, Category category2, List<String> status2, String tags, Pageable pageable);


    /**
     * 根据用户和关键字模糊查询
     *
     * @param user
     * @param keywords
     * @param pageable
     * @return
     */
    Page<Article> findByUserAndContentLike(User user, String keywords, Pageable pageable);

    /**
     * 热门文章
     *
     * @return
     */
    @Query(value = "SELECT * FROM article where status = 'publish' ORDER BY comment_size DESC,zan_size DESC LIMIT 10", nativeQuery = true)
    List<Article> findTop10HotArticle();

    /**
     * 统计不同状态的文章数
     *
     * @param status
     * @return
     */
    Long countByStatus(String status);

    /**
     * 根据标签模糊查询
     *
     * @param tagName
     * @param pageable
     * @return
     */
    Page<Article> findByTagsLike(String tagName, Pageable pageable);

    /**
     * 获得七日文章用户榜
     *
     * @return
     */
    @Query(value = "SELECT user_id FROM article WHERE status = 'publish' AND (DATE_SUB(CURDATE(),INTERVAL 30 DAY) <= date(create_time))  GROUP BY user_id ORDER BY count(id) DESC LIMIT 10", nativeQuery = true)
    List<Integer> getUserRankByArticleSizeInSevenDay();

    /**
     * 获得七日文章用户榜的数量
     *
     * @return
     */
    @Query(value = "SELECT count(id) as count FROM article  WHERE status = 'publish' AND (DATE_SUB(CURDATE(),INTERVAL 30 DAY) <= date(article.create_time))  GROUP BY user_id ORDER BY count DESC LIMIT 10", nativeQuery = true)
    List<BigInteger> getCountRankByArticleSizeInSevenDay();

    /**
     * 统计用户的文章数
     *
     * @return
     */
    @Query(value = "SELECT COUNT(*) FROM article WHERE user_id = ?1 AND status = 'publish'", nativeQuery = true)
    Long countArticleSizeByUserId(Integer id);

    /**
     * 统计用户的访问量
     *
     * @param id
     * @return
     */
    @Query(value = "SELECT SUM(view_size) FROM article WHERE user_id = ?1 AND status = 'publish'", nativeQuery = true)
    Long countViewSizeByUserId(Integer id);

    Integer deleteByUser(User user);

    Integer deleteByStatus(String status);
}
