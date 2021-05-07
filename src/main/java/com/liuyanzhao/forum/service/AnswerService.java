package com.liuyanzhao.forum.service;

import com.liuyanzhao.forum.entity.Question;
import com.liuyanzhao.forum.entity.Answer;
import com.liuyanzhao.forum.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author 言曌
 * @date 2018/6/4 下午2:48
 */

public interface AnswerService {

    /**
     * 根据id获取 Answer
     *
     * @param id
     * @return
     */
    Answer getAnswerById(Long id);

    /**
     * 发表回答
     *
     * @param answer
     * @return
     */
    void saveAnswer(Answer answer);


    /**
     * 修改回答
     *
     * @param answer
     */
    Answer updateAnswer(Answer answer);


    /**
     * 删除回答
     *
     * @param id
     * @return
     */
    void removeAnswer(Long id);


    /**
     * 统计某篇文章的回答数
     *
     * @param question
     * @return
     */
    Integer countAnswerSizeByQuestion(Question question);


    /**
     * 点赞
     *
     * @param answerId
     * @return
     */
    Answer createZan(Long answerId);



    /**
     * 点踩
     *
     * @param answerId
     * @return
     */
    Answer createCai(Long answerId);


    /**
     * 根据状态获得所有的回答
     *
     * @param statusList
     * @param pageable
     * @return
     */
    Page<Answer> listAnswerByStatusIn(List<String> statusList, Pageable pageable);

    /**
     * 状态，关键词
     *
     * @param statusList
     * @param keywords
     * @param pageable
     * @return
     */
    Page<Answer> listAnswerByStatusInAndKeyword(List<String> statusList, String keywords, Pageable pageable);

    /**
     * 根据文章获取回答列表，分页显示
     *
     * @param question
     * @return
     */
    Page<Answer> listAnswerByQuestionAndStatusIn(Question question, List<String> answerList, Pageable pageable);


    /**
     * 根据状态统计回答数
     *
     * @param status
     * @return
     */
    Long countAnswerByStatus(String status);

    /**
     * 获得某用户发送的回答列表
     *
     * @param user
     * @param statusList
     * @param pageable
     * @return
     */
    Page<Answer> listSendAnswers(User user, List<String> statusList, Pageable pageable);

    /**
     * 获得某用户接受的回答列表
     *
     * @param user
     * @param statusList
     * @param pageable
     * @return
     */
    Page<Answer> listRecieveAnswers(User user, List<String> statusList, Pageable pageable);

    /**
     * 统计
     *
     * @param user
     * @param statusList
     * @return
     */
    Long countByUserAndStatusIn(User user, List<String> statusList);

    /**
     * 统计收到的回答数
     *
     * @param user
     * @param statusList
     * @return
     */
    Long countByReplyUserAndStatusIn(User user, List<String> statusList);


    /**
     * 显示某个用户的所有回答记录
     *
     * @param user
     * @param statusList
     * @return
     */
    Page<Answer> listAllAnswersByUserAndStatusIn(User user, List<String> statusList, Pageable pageable);

    /**
     * 获得某个用户的一级评论
     *
     * @param user
     * @param statusList
     * @param pageable
     * @return
     */
    Page<Answer> listAnswersByUserAndStatusIn(User user, List<String> statusList, Pageable pageable);

    /**
     * 根据用户、是否采纳和状态来获取回答
     * @param user
     * @param isAccepted
     * @param statusList
     * @param pageable
     * @return
     */
    Page<Answer> listAnswersByUserAndIsAcceptedAndStatusIn(User user, Integer isAccepted, List<String> statusList, Pageable pageable);

    /**
     * 获得所有的回答
     * @param pageable
     * @return
     */
    Page<Answer> listAllAnswer(Pageable pageable);
}
