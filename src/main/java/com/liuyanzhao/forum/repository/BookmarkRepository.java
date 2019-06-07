package com.liuyanzhao.forum.repository;

import com.liuyanzhao.forum.entity.Bookmark;
import com.liuyanzhao.forum.entity.Article;
import com.liuyanzhao.forum.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author 言曌
 * @date 2018/5/6 下午9:29
 */

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    /**
     * 获得某个用户的所有收藏
     * @param user
     * @return
     */
    Page<Bookmark> findDistinctByUser(User user, Pageable pageable);

    /**
     * 统计一篇文章收藏数
     * @param article
     * @return
     */
    Integer countByArticle(Article article);

    /**
     * 查询一个用户是否收藏过某篇文章
     * @param user
     * @param article
     * @return
     */
    List<Bookmark> findByUserAndArticle(User user, Article article);
}
