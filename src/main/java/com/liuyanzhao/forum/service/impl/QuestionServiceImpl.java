package com.liuyanzhao.forum.service.impl;

import com.liuyanzhao.forum.entity.Question;
import com.liuyanzhao.forum.entity.User;
import com.liuyanzhao.forum.enums.PostStatusEnum;
import com.liuyanzhao.forum.enums.ReputationEnum;
import com.liuyanzhao.forum.repository.AnswerRepository;
import com.liuyanzhao.forum.repository.QuestionRepository;
import com.liuyanzhao.forum.repository.UserRepository;
import com.liuyanzhao.forum.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * @author 言曌
 * @date 2018/6/3 下午3:02
 */
@Service
public class QuestionServiceImpl implements QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Question saveQuestion(Question question) {
        //发布问题
        if (question.getId() == null) {
            Question returnQuestion = questionRepository.save(question);
            returnQuestion.setGuid("/questions/" + returnQuestion.getId());

            //2、修改用户积分
            User originalUser = question.getUser();
            Integer reputation = originalUser.getReputation();
            originalUser.setReputation(reputation + ReputationEnum.PUBLISH_QUESTION.getCode());
            userRepository.save(originalUser);

            return returnQuestion;
        } else {
            //修改问题
            return questionRepository.save(question);
        }
    }

    @Override
    public void deleteQuestion(Long id) {
        questionRepository.delete(id);
    }

    @Override
    public Question getQuestionById(Long id) {
        return questionRepository.findOne(id);
    }

    @Override
    public Page<Question> listQuestionsByStatusIn(List<String> statusList, Pageable pageable) {
        return questionRepository.findByStatusIn(statusList, pageable);
    }

    @Override
    public Page<Question> listQuestionsByStatusInAndKeywords(List<String> statusList, String keywords, Pageable pageable) {
        return questionRepository.findByStatusInAndTitleLikeOrStatusInAndTagsLike(statusList, "%" + keywords + "%", statusList, "%" + keywords + "%", pageable);
    }

    @Override
    public Page<Question> listUnansweredQuestionByStatusIn(List<String> statusList, Pageable pageable) {
        return questionRepository.findByAnswerSizeAndStatusIn(0, statusList, pageable);
    }

    @Override
    public Page<Question> listQuestionsByUserAndStatusIn(User user, List<String> statusList, Pageable pageable) {
        return questionRepository.findByUserAndStatusIn(user, statusList, pageable);
    }

    @Override
    public void viewIncrease(Long id) {
        //添加当前文章的cookie
        Question question = questionRepository.findOne(id);
        if (question == null) {
            return;
        }
        question.setViewSize(question.getViewSize() + 1);
        this.saveQuestion(question);
    }

    @Override
    public Page<Question> listQuestionsByTag(String name, Pageable pageable) {
        return questionRepository.findByTagsLike("%" + name + "%", pageable);
    }

    @Override
    public Page<Question> listAllQuestions(Pageable pageable) {
        return questionRepository.findAll(pageable);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void removeQuestion(Long id) {
        Question originalQuestion = questionRepository.findOne(id);
        if (Objects.equals(originalQuestion.getStatus(), PostStatusEnum.PUBLISH_POST.getCode())) {
            originalQuestion.setStatus(PostStatusEnum.DELETED_POST.getCode());
            questionRepository.save(originalQuestion);

            //2、修改用户积分
            User originalUser = originalQuestion.getUser();
            Integer reputation = originalUser.getReputation();
            originalUser.setReputation(reputation - ReputationEnum.PUBLISH_QUESTION.getCode());
            userRepository.save(originalUser);

            //3.删除评论
            answerRepository.deleteByQuestion(originalQuestion);

        } else {
            questionRepository.delete(id);
        }
    }
}
