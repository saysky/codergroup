package com.liuyanzhao.forum.service;

import com.liuyanzhao.forum.entity.Comment;
import com.liuyanzhao.forum.entity.Article;
import com.liuyanzhao.forum.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author 言曌
 * @date 2018/4/18 下午3:33
 */

public interface CommentService {
    /**
     * 根据id获取 Comment
     *
     * @param id
     * @return
     */
    Comment getCommentById(Long id);

    /**
     * 发表评论
     *
     * @param comment
     * @return
     */
    void saveComment(Comment comment);

    /**
     * 修改评论
     *
     * @param comment
     */
    void updateComment(Comment comment);

    /**
     * 回复评论
     *
     * @param commentId
     * @param article
     * @param commentContent
     * @return
     */
    void replyComment(Article article, Long commentId, Long replyId, String commentContent);


    /**
     * 删除评论
     *
     * @param id
     * @return
     */
    void removeComment(Long id);


    /**
     * 统计某篇文章的评论数
     *
     * @param article
     * @return
     */
    Integer countCommentSizeByArticle(Article article);


    /**
     * 点赞
     *
     * @param commentId
     * @return
     */
    Comment createZan(Long commentId);

    /**
     * 点踩
     *
     * @param commentId
     * @return
     */
    Comment createCai(Long commentId);


    /**
     * 根据状态获得所有的评论
     *
     * @param statusList
     * @param pageable
     * @return
     */
    Page<Comment> listCommentByStatusIn(List<String> statusList, Pageable pageable);

    /**
     * 状态，关键词
     *
     * @param statusList
     * @param keywords
     * @param pageable
     * @return
     */
    Page<Comment> listCommentByStatusInAndKeyword(List<String> statusList, String keywords, Pageable pageable);

    /**
     * 根据文章获取评论列表，分页显示
     *
     * @param article
     * @return
     */
    Page<Comment> listCommentByArticleAndStatusIn(Article article, List<String> commentList, Pageable pageable);


    /**
     * 根据状态统计评论数
     *
     * @param status
     * @return
     */
    Long countCommentByStatus(String status);

    /**
     * 获得某用户发送的评论列表
     * @param user
     * @param statusList
     * @param pageable
     * @return
     */
    Page<Comment> listSendComments(User user, List<String> statusList, Pageable pageable);

    /**
     * 获得某用户接受的评论列表
     * @param user
     * @param statusList
     * @param pageable
     * @return
     */
    Page<Comment> listRecieveComments(User user, List<String> statusList, Pageable pageable);

    /**
     * 统计
     * @param user
     * @param statusList
     * @return
     */
    Long countByUserAndStatusIn(User user, List<String> statusList);

    /**
     * 统计收到的评论数
     * @param user
     * @param statusList
     * @return
     */
    Long countByReplyUserAndStatusIn(User user,List<String> statusList);

}
