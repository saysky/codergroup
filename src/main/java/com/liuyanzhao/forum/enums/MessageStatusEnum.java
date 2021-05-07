package com.liuyanzhao.forum.enums;

/**
 * @author 言曌
 * @date 2018/4/28 上午10:14
 */

public enum MessageStatusEnum {


    NOT_READ_MESSAGE(1,"未读"),
    HAD_READ_MESSAGE(2,"已读"),
    DELETE_MESSAGE(3,"已删除")
    ;


    private Integer code;

    private String msg;

    MessageStatusEnum(Integer code, String msg) {
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
