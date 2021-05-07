package com.liuyanzhao.forum.vo;



/**
 * @author 言曌
 * @date 2018/5/28 下午11:38
 */


public class HotArticleVO {

    private Long id;

    private String title;

    private String guid;

    private Integer commentSize;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public Integer getCommentSize() {
        return commentSize;
    }

    public void setCommentSize(Integer commentSize) {
        this.commentSize = commentSize;
    }
}
