package com.liuyanzhao.forum.dto;



/**
 * @author 言曌
 * @date 2018/5/24 下午3:03
 */


public class UserArticleRankDTO {

    private Integer id;

    private String username;

    private String nickname;

    private String avatar;

    private String articleSize;

    public UserArticleRankDTO(Integer id, String username, String nickname, String avatar, String articleSize) {
        this.id = id;
        this.username = username;
        this.nickname = nickname;
        this.avatar = avatar;
        this.articleSize = articleSize;
    }

    public UserArticleRankDTO() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getArticleSize() {
        return articleSize;
    }

    public void setArticleSize(String articleSize) {
        this.articleSize = articleSize;
    }
}
