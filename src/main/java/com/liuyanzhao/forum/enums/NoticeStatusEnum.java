package com.liuyanzhao.forum.enums;

/**
 * @author 言曌
 * @date 2018/5/31 下午11:21
 */

public enum NoticeStatusEnum {
    NOT_READ(0,"未读"),
    HAD_READ(1,"已读");

    private Integer code;

    private String msg;

    NoticeStatusEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
