package com.liuyanzhao.forum.service.impl;

import com.liuyanzhao.forum.entity.*;
import com.liuyanzhao.forum.enums.PostStatusEnum;
import com.liuyanzhao.forum.enums.NoticeTypeEnum;
import com.liuyanzhao.forum.enums.ReputationEnum;
import com.liuyanzhao.forum.repository.CommentRepository;
import com.liuyanzhao.forum.repository.NoticeRepository;
import com.liuyanzhao.forum.repository.UserRepository;
import com.liuyanzhao.forum.service.CommentService;
import com.liuyanzhao.forum.service.UserService;
import com.liuyanzhao.forum.util.RedisOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author 言曌
 * @date 2018/4/18 下午3:33
 */

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private NoticeRepository noticeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private RedisOperator redis;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void removeComment(Long id) {
        Comment originalComment = commentRepository.findOne(id);
        if (Objects.equals(originalComment.getStatus(), PostStatusEnum.PUBLISH_POST.getCode())) {
            originalComment.setStatus(PostStatusEnum.DELETED_POST.getCode());
            commentRepository.save(originalComment);

            //2、修改用户积分
            User originalUser = originalComment.getUser();
            Integer reputation = originalUser.getReputation();
            originalUser.setReputation(reputation - ReputationEnum.PUBLISH_COMMENT.getCode());
            userRepository.save(originalUser);

        } else {
            commentRepository.delete(id);
        }
    }

    @Override
    public Integer countCommentSizeByArticle(Article article) {
        List<String> statusList = new ArrayList<>();
        statusList.add(PostStatusEnum.PUBLISH_POST.getCode());
        return commentRepository.countByArticleAndStatusIn(article, statusList);
    }

    @Override
    public Comment getCommentById(Long id) {
        return commentRepository.findOne(id);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveComment(Comment comment) {
        //1、添加评论
        //获取楼层号
        Integer lastFloor = commentRepository.getMaxCommentFloor(comment.getArticle().getId());
        if (lastFloor == null) {
            lastFloor = 0;
        }
        comment.setFloor(lastFloor + 1);
        Comment result = commentRepository.save(comment);
        //2、添加提醒
        //如果是评论的是自己的文章，不提醒
        if (!Objects.equals(comment.getUser().getId(), comment.getArticle().getUser().getId())) {
            Notice notice = new Notice();
            notice.setContent(comment.getArticle().getTitle());
            notice.setMore(comment.getContent());
            String guid = comment.getArticle().getGuid() + "#comment-" + result.getId();
            notice.setGuid(guid);
            notice.setFromUser(comment.getUser());
            notice.setToUser(comment.getArticle().getUser());
            NoticeType noticeType = new NoticeType();
            noticeType.setId(NoticeTypeEnum.COMMENT_ARTICLE.getCode());
            notice.setNoticeType(noticeType);
            noticeRepository.save(notice);

            //添加redis
            try {
                redis.incr("noticeSize:" + comment.getArticle().getUser().getId(), 1);
            } catch (Exception e) {
                logger.error("redis服务故障", e);
            }
        }
        //3、修改用户积分
        User originalUser = comment.getUser();
        Integer reputation = originalUser.getReputation();
        originalUser.setReputation(reputation + ReputationEnum.ARTICLE_GET_COMMENT.getCode());
        userRepository.save(originalUser);

    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateComment(Comment comment) {
        commentRepository.save(comment);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void replyComment(Article article, Long commentId, Long replyId, String commentContent) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();        user = userService.getUserByUsername(user.getUsername());
        Comment comment = new Comment(user, article, commentId, commentContent);
        //评论回复，需要加上@用户
        Comment replyComment = null;
        if (replyId != null) {
            replyComment = commentRepository.findOne(replyId);
            comment.setReplyUser(replyComment.getUser());
        } else {
            replyComment = commentRepository.findOne(commentId);
            comment.setReplyUser(replyComment.getUser());
        }
        //1、添加评论
        Comment result = commentRepository.save(comment);

        //2、添加提醒
        //如果回复的是自己的评论，不提醒
        if (!Objects.equals(replyComment.getUser().getId(), user.getId())) {
            Notice notice = new Notice();
            notice.setContent(replyComment.getContent());
            notice.setMore(commentContent);
            String guid = comment.getArticle().getGuid() + "#comment-" + result.getId();
            notice.setGuid(guid);
            notice.setFromUser(user);
            notice.setToUser(replyComment.getUser());
            NoticeType noticeType = new NoticeType();
            noticeType.setId(NoticeTypeEnum.REPLY_COMMENT.getCode());
            notice.setNoticeType(noticeType);
            noticeRepository.save(notice);

            //3、redis提醒数
            try {
                redis.incr("noticeSize:" + replyComment.getUser().getId(), 1);
            } catch (Exception e) {
                logger.error("redis服务故障", e);
            }
        }


    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Comment createZan(Long commentId) {
        Comment originalComment = commentRepository.findOne(commentId);
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();        user = userService.getUserByUsername(user.getUsername());
        Zan zan = new Zan(user);
        boolean isExist = originalComment.addZan(zan);
//        if (isExist) {
//            throw new IllegalArgumentException("您已经点过赞了！");
//        }
        return commentRepository.save(originalComment);
    }


    //    @Transactional(rollbackFor = Exception.class)
    @Override
    public Comment createCai(Long commentId) {
        Comment originalComment = commentRepository.findOne(commentId);
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();        user = userService.getUserByUsername(user.getUsername());
        Cai cai = new Cai(user);
        boolean isExist = originalComment.addCai(cai);
//        if (isExist) {
//            throw new IllegalArgumentException("您已经点踩过了！");
//        }
        return commentRepository.save(originalComment);

    }

    @Override
    public Page<Comment> listCommentByArticleAndStatusIn(Article article, List<String> statusList, Pageable pageable) {
        return commentRepository.findByArticleAndPidAndStatusIn(article, null, statusList, pageable);
    }


    @Override
    public Page<Comment> listCommentByStatusIn(List<String> statusList, Pageable pageable) {
        return commentRepository.findByStatusIn(statusList, pageable);
    }

    @Override
    public Page<Comment> listCommentByStatusInAndKeyword(List<String> statusList, String keywords, Pageable pageable) {
        return commentRepository.findByContentLikeAndStatusIn("%" + keywords + "%", statusList, pageable);
    }

    @Override
    public Long countCommentByStatus(String status) {
        return commentRepository.countByStatus(status);
    }

    @Override
    public Page<Comment> listSendComments(User user, List<String> statusList, Pageable pageable) {
        return commentRepository.findByUserAndStatusIn(user, statusList, pageable);
    }

    @Override
    public Page<Comment> listRecieveComments(User user, List<String> statusList, Pageable pageable) {
        return commentRepository.findByReplyUserAndStatusIn(user, statusList, pageable);

    }

    @Override
    public Long countByUserAndStatusIn(User user, List<String> statusList) {
        return commentRepository.countByUserAndStatusIn(user, statusList);
    }

    @Override
    public Long countByReplyUserAndStatusIn(User user, List<String> statusList) {
        return commentRepository.countByReplyUserAndStatusIn(user, statusList);
    }

}
