package com.liuyanzhao.forum.vo;



/**
 * @author 言曌
 * @date 2018/5/24 上午11:34
 */


public class UserRankVO {

    private Integer id;

    private String nickname;

    private String username;

    private String avatar;

    private Long viewSize;

    private Long articleSize;

    private Long answerSize;

    private Long questionSize;

    private Integer reputation;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Long getViewSize() {
        return viewSize;
    }

    public void setViewSize(Long viewSize) {
        this.viewSize = viewSize;
    }

    public Long getArticleSize() {
        return articleSize;
    }

    public void setArticleSize(Long articleSize) {
        this.articleSize = articleSize;
    }

    public Long getAnswerSize() {
        return answerSize;
    }

    public void setAnswerSize(Long answerSize) {
        this.answerSize = answerSize;
    }

    public Long getQuestionSize() {
        return questionSize;
    }

    public void setQuestionSize(Long questionSize) {
        this.questionSize = questionSize;
    }

    public Integer getReputation() {
        return reputation;
    }

    public void setReputation(Integer reputation) {
        this.reputation = reputation;
    }
}
