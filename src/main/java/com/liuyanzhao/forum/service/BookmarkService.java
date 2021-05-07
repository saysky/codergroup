package com.liuyanzhao.forum.service;

import com.liuyanzhao.forum.entity.Bookmark;
import com.liuyanzhao.forum.entity.Article;
import com.liuyanzhao.forum.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


/**
 * @author 言曌
 * @date 2018/5/6 下午9:30
 */

public interface BookmarkService {


    /**
     * 获得用户的所有书签
     *
     * @return
     */
    Page<Bookmark> findBookmarkByUser(User user, Pageable pageable);

    /**
     * 根据id获得书签
     *
     * @param id
     * @return
     */
    Bookmark findById(Long id);

    /**
     * 添加书签
     * bookmark
     */
    void saveBookmark(Bookmark bookmark);

    /**
     * 删除某个书签
     *
     */
    void deleteBookmark(Bookmark bookmark);

    /**
     * 获得某篇文章的收藏数
     *
     * @param article
     * @return
     */
    Integer countByArticle(Article article);

    /**
     * 是否收藏了某篇文章
     * @param user
     * @param article
     * @return
     */
    Boolean isMarkArticle(User user, Article article);
}
