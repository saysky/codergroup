package com.liuyanzhao.forum.service.impl;

import com.liuyanzhao.forum.entity.Bookmark;
import com.liuyanzhao.forum.entity.Article;
import com.liuyanzhao.forum.entity.User;
import com.liuyanzhao.forum.enums.ReputationEnum;
import com.liuyanzhao.forum.repository.BookmarkRepository;
import com.liuyanzhao.forum.repository.ArticleRepository;
import com.liuyanzhao.forum.repository.UserRepository;
import com.liuyanzhao.forum.service.BookmarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * @author 言曌
 * @date 2018/5/6 下午9:39
 */

@Service
public class BookmarkServiceImpl implements BookmarkService {

    @Autowired
    private BookmarkRepository bookmarkRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Page<Bookmark> findBookmarkByUser(User user, Pageable pageable) {
        return bookmarkRepository.findDistinctByUser(user, pageable);
    }

    @Override
    public Bookmark findById(Long id) {
        return bookmarkRepository.findOne(id);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveBookmark(Bookmark bookmark) {
        //添加书签
        bookmarkRepository.save(bookmark);
        //给文章增加收藏数
        Article originalArticle = bookmark.getArticle();
        originalArticle.setBookmarkSize(originalArticle.getBookmarkSize() + 1);
        articleRepository.save(originalArticle);

        //2、修改用户积分
        User originalUser = originalArticle.getUser();
        Integer reputation = originalUser.getReputation();
        originalUser.setReputation(reputation + ReputationEnum.ARTICLE_GET_BOOKMARK.getCode());
        userRepository.save(originalUser);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteBookmark(Bookmark bookmark) {
        //删除书签
        bookmarkRepository.delete(bookmark);
        //给文章减少收藏数
        Article originalArticle = bookmark.getArticle();
        Integer count = originalArticle.getBookmarkSize();
        if (count > 0) {
            originalArticle.setBookmarkSize(count - 1);
            articleRepository.save(originalArticle);
        }
    }

    @Override
    public Integer countByArticle(Article article) {
        return bookmarkRepository.countByArticle(article);
    }

    @Override
    public Boolean isMarkArticle(User user, Article article) {
        List<Bookmark> bookmarkList = bookmarkRepository.findByUserAndArticle(user, article);
        if (bookmarkList == null || bookmarkList.size() == 0) {
            return false;
        } else {
            return true;
        }
    }
}
