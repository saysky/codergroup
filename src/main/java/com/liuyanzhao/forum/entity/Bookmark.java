package com.liuyanzhao.forum.entity;


import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 收藏夹，书签
 *
 * @author 言曌
 * @date 2018/5/6 下午9:26
 */

@Entity(name = "bookmark")
@Data
public class Bookmark implements Serializable {


    private static final long serialVersionUID = 803818372566619398L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Article article;

    @org.hibernate.annotations.CreationTimestamp  // 由数据库自动创建时间
    @Column(name = "create_time")
    private Timestamp createTime;

    public Bookmark() {
    }

    public Bookmark(User user, Article article) {
        this.user = user;
        this.article = article;
    }
}
