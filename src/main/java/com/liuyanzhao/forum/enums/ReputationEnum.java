package com.liuyanzhao.forum.enums;

/**
 * @author 言曌
 * @date 2018/6/10 下午11:13
 */

public enum ReputationEnum {
    //文章
    PUBLISH_ARTICLE(10, "发布文章"),
    PUBLISH_COMMENT(2, "提交评论"),
    ARTICLE_GET_COMMENT(2, "文章被评论"),
    ARTICLE_GET_ZAN(5, "文章被赞"),
    ARTICLE_GET_BOOKMARK(5, "文章被收藏"),

    //问答
    PUBLISH_QUESTION(5, "发布问题"),
    PUBLISH_ANSWER(3, "发布回答"),
    ANSWER_GET_ACCEPT(5, "回答被采纳"),
    ACCEPT_ANSWER(2, "采纳答案"),

    ANSWER_GET_ZAN(1, "回答被赞"),
    ANSWER_GET_CAI(-1, "回答被踩"),;

    private Integer code;

    private String message;

    ReputationEnum(Integer code, String message) {
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
