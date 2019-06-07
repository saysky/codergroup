package com.liuyanzhao.forum.enums;


/**
 * @author 言曌
 * @date 2018/5/6 下午5:38
 */


public enum CommentStickStatusEnum {

    STICKY_STATUS(1, "置顶"),
    NOT_STICKY_STATUS(0, "未置顶");

    private Integer code;

    private String msg;

    CommentStickStatusEnum(Integer code, String msg) {
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
