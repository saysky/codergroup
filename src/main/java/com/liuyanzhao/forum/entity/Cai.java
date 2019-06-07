package com.liuyanzhao.forum.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 踩
 * @author 言曌
 * @date 2018/5/3 下午2:09
 */

@Data
@Entity(name = "cai")
public class Cai implements Serializable {

    private static final long serialVersionUID = -6761434041669308614L;
    @Id // 主键
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自增长策略
    private Long id; // 用户的唯一标识

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;

    @org.hibernate.annotations.CreationTimestamp  // 由数据库自动创建时间
    @Column(name = "create_time")
    private Timestamp createTime;

    public Cai() {
    }

    public Cai(User user) {
        this.user = user;
    }
}
