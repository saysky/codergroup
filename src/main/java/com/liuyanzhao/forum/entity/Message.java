package com.liuyanzhao.forum.entity;

import com.liuyanzhao.forum.util.DateUtil;

import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 私信
 *
 * @author 言曌
 * @date 2018/4/26 上午11:08
 */

@Entity(name = "message")
public class Message implements Serializable {

    private static final long serialVersionUID = -4579166384094798537L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自增长策略
    private Long id;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;//留言的主人

    @JoinColumn(name = "friend_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User friend;//对方

    @JoinColumn(name = "sender_id")
    @ManyToOne(fetch = FetchType.LAZY)//发送者用户id，真实的发送者id
    private User sender;

    @JoinColumn(name = "receiver_id")
    @ManyToOne(fetch = FetchType.LAZY)//接受者用户id，真实的接受者id
    private User receiver;

    @NotEmpty(message = "内容不能为空")
    @Size(max = 500, message = "内容不能超过500个字符")
    private String content;

    private Integer type = 1;//消息类型，1普通消息，2系统消息

    private Integer status = 1;//消息状态：1未读，2已读，3删除

    @org.hibernate.annotations.CreationTimestamp  // 由数据库自动创建时间
    @Column(name = "create_time")
    private Timestamp createTime;


    @Transient
    public String easyCreateTime;

    public String getEasyCreateTime() {
        if (getCreateTime() == null) {
            return null;
        }
        return DateUtil.getRelativeDate(getCreateTime());
    }

    public Message() {
    }


    public Message(User user, User friend, User sender, User receiver, String content, Integer status) {
        this.user = user;
        this.friend = friend;
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.status = status;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getFriend() {
        return friend;
    }

    public void setFriend(User friend) {
        this.friend = friend;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public void setEasyCreateTime(String easyCreateTime) {
        this.easyCreateTime = easyCreateTime;
    }
}
