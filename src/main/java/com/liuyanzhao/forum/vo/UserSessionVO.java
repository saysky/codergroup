package com.liuyanzhao.forum.vo;



/**
 * User session
 *
 * @author 言曌
 * @date 2018/4/30 下午8:55
 */


public class UserSessionVO {

    private Integer id;

    private String nickname;

    private String username;

    private String email;

    private String avatar;

    private Integer messageSize = 0;

    private Integer noticeSize = 0;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Integer getMessageSize() {
        return messageSize;
    }

    public void setMessageSize(Integer messageSize) {
        this.messageSize = messageSize;
    }

    public Integer getNoticeSize() {
        return noticeSize;
    }

    public void setNoticeSize(Integer noticeSize) {
        this.noticeSize = noticeSize;
    }
}
