package com.liuyanzhao.forum.enums;

/**
 * @author 言曌
 * @date 2018/6/10 上午1:54
 */

public enum IsAcceptedEnum {
    ACCEPT_STATUS(1,"已采纳"),
    NOT_ACCEPT_STATUS(0,"未采纳")
    ;

    private Integer code;

    private String message;

    IsAcceptedEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
