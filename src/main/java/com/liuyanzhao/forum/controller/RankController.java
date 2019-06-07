package com.liuyanzhao.forum.controller;

import com.liuyanzhao.forum.service.RankService;
import com.liuyanzhao.forum.vo.UserRankVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author 言曌
 * @date 2018/6/8 下午7:59
 */
@RestController
public class RankController {

    @Autowired
    private RankService rankService;

    @GetMapping("/userAnswerRank")
    public ResponseEntity userAnswerRank() {
        List<UserRankVO> userList = rankService.getUserAnswerRankTop10();
        return ResponseEntity.ok().body(userList);
    }


    @GetMapping("/userQuestionRank")
    public ResponseEntity userQuestionRank() {
        List<UserRankVO> userList = rankService.getUserQuestionRankTop10();
        return ResponseEntity.ok().body(userList);
    }


    @GetMapping("/userArticleRank")
    public ResponseEntity userArticleRank() {
        List<UserRankVO> userList = rankService.getUserArticleRankTop10();
        return ResponseEntity.ok().body(userList);
    }


    @GetMapping("/userReputationRank")
    public ResponseEntity userReputationRank() {
        List<UserRankVO> userList = rankService.getUserReputationRankTop10();
        return ResponseEntity.ok().body(userList);
    }

}
