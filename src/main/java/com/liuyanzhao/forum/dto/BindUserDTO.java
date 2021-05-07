package com.liuyanzhao.forum.dto;

import com.liuyanzhao.forum.entity.BindType;


/**
 * @author 言曌
 * @date 2018/5/14 上午6:56
 */

public class BindUserDTO {

    private String openId;

    private String nickname;

    private String gender;

    private String avatar;

    private BindType bindType;


    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public BindType getBindType() {
        return bindType;
    }

    public void setBindType(BindType bindType) {
        this.bindType = bindType;
    }
}
