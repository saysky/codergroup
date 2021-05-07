package com.liuyanzhao.forum.enums;

/**
 * @author 言曌
 * @date 2018/5/12 下午3:00
 */

public enum AllowCommentEnum {

    ALLOW_COMMENT(0,"允许评论"),
    NOT_ALLOW_COMMENT(1,"不允许评论")
    ;

    private Integer code;

    private String msg;

    AllowCommentEnum(Integer code, String msg) {
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
