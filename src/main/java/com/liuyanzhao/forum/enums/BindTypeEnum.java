package com.liuyanzhao.forum.enums;

/**
 * @author 言曌
 * @date 2018/5/13 下午11:26
 */

public enum BindTypeEnum {


    QQ(1,"腾讯QQ"),
    GITHUB(2,"GitHub"),
    WEIBO(3,"新浪微博");

    private Integer code;

    private String msg;

    BindTypeEnum(Integer code, String msg) {
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
