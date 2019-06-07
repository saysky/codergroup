package com.liuyanzhao.forum.service;

import com.liuyanzhao.forum.entity.User;
import com.liuyanzhao.forum.vo.UserRankVO;

import java.util.List;

/**
 * @author 言曌
 * @date 2018/6/8 下午4:44
 */


public interface RankService {

    /**
     * 获得用户排名信息
     * @param user
     * @return
     */
    UserRankVO getUserRankVO(User user);

    /**
     * 用户回答榜
     * @return
     */
    List<UserRankVO> getUserAnswerRankTop10();

    /**
     * 用户文章榜
     * @return
     */
    List<UserRankVO> getUserArticleRankTop10();

    /**
     * 用户提问榜
     * @return
     */
    List<UserRankVO> getUserQuestionRankTop10();

    /**
     * 用户声望榜
     * @return
     */
    List<UserRankVO> getUserReputationRankTop10();
}
