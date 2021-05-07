package com.liuyanzhao.forum.enums;

/**
 * @author 言曌
 * @date 2018/5/31 下午9:07
 */

public enum NoticeTypeEnum {
    COMMENT_ARTICLE(1, "评论文章"),
    REPLY_COMMENT(2, "回复评论"),
    FOLLOW_YOU(3, "关注了你"),
    ANSWER_QUESTION(4, "回答问题"),
    COMMENT_ANSWER(5, "评论回答"),
    ACCEPT_ANSWER(6, "采纳答案");

    private Integer code;

    private String msg;

    NoticeTypeEnum(Integer code, String msg) {
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
