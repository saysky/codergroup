package com.liuyanzhao.forum.enums;

/**
 * @author 言曌
 * @date 2018/5/11 下午4:54
 */

public enum PostStickyEnum {

    STICKY_POST(1,"置顶"),
    NOT_STICKY_POST(0,"不置顶");


    private Integer code;

    private String msg;

    PostStickyEnum(Integer code, String msg) {
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
