package com.liuyanzhao.forum.entity;




import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 赞
 * @author 言曌
 * @date 2018/5/2 下午6:44
 */



@Entity(name = "zan")
public class Zan implements Serializable {


    private static final long serialVersionUID = -1739308574227182144L;

    @Id // 主键
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自增长策略
    private Long id; // 用户的唯一标识

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;

    @org.hibernate.annotations.CreationTimestamp  // 由数据库自动创建时间
    @Column(name = "create_time")
    private Timestamp createTime;

    public Zan() {
    }

    public Zan(User user) {
        this.user = user;
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

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }
}
