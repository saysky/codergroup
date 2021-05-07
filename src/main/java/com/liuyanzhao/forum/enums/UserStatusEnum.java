package com.liuyanzhao.forum.enums;

/**
 * @author 言曌
 * @date 2018/5/13 下午11:26
 */

public enum UserStatusEnum {


    NORMAL("normal", "正常"),
    BANNED("banned", "禁止登录"),
    DELETED("deleted", "已删除");

    private String code;

    private String msg;

    UserStatusEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
