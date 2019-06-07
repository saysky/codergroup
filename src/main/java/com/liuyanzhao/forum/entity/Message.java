package com.liuyanzhao.forum.entity;

import com.liuyanzhao.forum.util.DateUtil;
import lombok.Data;
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
@Data
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
}
