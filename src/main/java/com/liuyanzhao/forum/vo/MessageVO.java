package com.liuyanzhao.forum.vo;



/**
 * @author 言曌
 * @date 2018/5/1 上午11:22
 */


public class MessageVO {

    private Long id;

    private String content;//内容

    private Integer uid;//私信人的id

    private String name;//私信人的昵称

    private String avatar;//私信人的头像

    private String createTime;//发布时间

    private Integer count;//该好友发的私信数量

    public MessageVO() {
    }

    public MessageVO(Long id, String content, Integer uid, String name,String avatar, String createTime) {
        this.id = id;
        this.content = content;
        this.uid = uid;
        this.name = name;
        this.avatar = avatar;
        this.createTime = createTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
