package com.liuyanzhao.forum.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 赞
 * @author 言曌
 * @date 2018/5/2 下午6:44
 */

@Getter
@Setter
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


}
