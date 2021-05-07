package com.liuyanzhao.forum.service.impl;

import com.liuyanzhao.forum.entity.User;
import com.liuyanzhao.forum.repository.AnswerRepository;
import com.liuyanzhao.forum.repository.ArticleRepository;
import com.liuyanzhao.forum.repository.QuestionRepository;
import com.liuyanzhao.forum.repository.UserRepository;
import com.liuyanzhao.forum.service.RankService;
import com.liuyanzhao.forum.vo.UserRankVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 言曌
 * @date 2018/6/8 下午4:47
 */
@Service
public class RankServiceImpl implements RankService {

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private QuestionRepository questionRepository;


    @Override
    @Cacheable(value = "24h", key = "'userRankVO:'+#p0.id", unless = "#user eq null")
    public UserRankVO getUserRankVO(User user) {
        UserRankVO userRankVO = new UserRankVO();
        Long viewSize = articleRepository.countViewSizeByUserId(user.getId());
        Long articleSize = articleRepository.countArticleSizeByUserId(user.getId());
        Long questionSize = questionRepository.countQuestionSizeByUserId(user.getId());
        Long answerSize = answerRepository.countAnswerSizeByUserId(user.getId());
        Integer reputation = user.getReputation();
        userRankVO.setId(user.getId());
        userRankVO.setUsername(user.getUsername());
        userRankVO.setNickname(user.getNickname());
        userRankVO.setAvatar(user.getAvatar());
        userRankVO.setViewSize(viewSize);
        userRankVO.setArticleSize(articleSize);
        userRankVO.setQuestionSize(questionSize);
        userRankVO.setAnswerSize(answerSize);
        userRankVO.setReputation(reputation);
        //修改数据库
        user.setViewSize(viewSize);
        user.setArticleSize(articleSize);
        user.setQuestionSize(questionSize);
        user.setAnswerSize(answerSize);
        userRepository.save(user);
        return userRankVO;
    }

    //用户回答榜
    @Override
    @Cacheable(value = "24h", key = "'rank:userAnswerRankTop10'")
    public List<UserRankVO> getUserAnswerRankTop10() {
        List<Integer> uidList = answerRepository.getUserRankByAnswerSize();
        List<BigInteger> countList = answerRepository.getCountRankByAnswerSize();
        List<UserRankVO> userRankVOList = new ArrayList<>();
        for (int i = 0; i < uidList.size(); i++) {
            User user = userRepository.findOne(uidList.get(i));
            UserRankVO userRankVO = new UserRankVO();
            userRankVO.setId(user.getId());
            userRankVO.setUsername(user.getUsername());
            userRankVO.setNickname(user.getNickname());
            userRankVO.setAvatar(user.getAvatar());
            userRankVO.setAnswerSize(Long.valueOf(countList.get(i) + ""));
            userRankVOList.add(userRankVO);
        }
        return userRankVOList;
    }

    //用户文章榜(七日)
    @Override
    public List<UserRankVO> getUserArticleRankTop10() {
        List<Integer> uidList = articleRepository.getUserRankByArticleSizeInSevenDay();
        List<BigInteger> countList = articleRepository.getCountRankByArticleSizeInSevenDay();
        List<UserRankVO> userRankVOList = new ArrayList<>();
        for (int i = 0; i < uidList.size(); i++) {
            User user = userRepository.findOne(uidList.get(i));
            UserRankVO userRankVO = new UserRankVO();
            userRankVO.setId(user.getId());
            userRankVO.setUsername(user.getUsername());
            userRankVO.setNickname(user.getNickname());
            userRankVO.setAvatar(user.getAvatar());
            userRankVO.setArticleSize(Long.valueOf(countList.get(i) + ""));
            userRankVOList.add(userRankVO);
        }
        return userRankVOList;
    }


    //用户提问榜
    @Override
    public List<UserRankVO> getUserQuestionRankTop10() {
        List<Integer> uidList = questionRepository.getUserRankByQuestionSize();
        List<BigInteger> countList = questionRepository.getCountRankByQuestionSize();
        List<UserRankVO> userRankVOList = new ArrayList<>();
        for (int i = 0; i < uidList.size(); i++) {
            User user = userRepository.findOne(uidList.get(i));
            UserRankVO userRankVO = new UserRankVO();
            userRankVO.setId(user.getId());
            userRankVO.setUsername(user.getUsername());
            userRankVO.setNickname(user.getNickname());
            userRankVO.setAvatar(user.getAvatar());
            userRankVO.setQuestionSize(Long.valueOf(countList.get(i) + ""));
            userRankVOList.add(userRankVO);
        }
        return userRankVOList;
    }

    //用户声望榜
    @Override
    public List<UserRankVO> getUserReputationRankTop10() {
        List<User> userList = userRepository.getUserRankByReputation();
        List<UserRankVO> userRankVOList = new ArrayList<>();
        for (int i = 0; i < userList.size(); i++) {
            User user = userList.get(i);
            UserRankVO userRankVO = new UserRankVO();
            userRankVO.setId(user.getId());
            userRankVO.setUsername(user.getUsername());
            userRankVO.setNickname(user.getNickname());
            userRankVO.setAvatar(user.getAvatar());
            userRankVO.setReputation(user.getReputation());
            userRankVOList.add(userRankVO);
        }
        return userRankVOList;
    }

}
