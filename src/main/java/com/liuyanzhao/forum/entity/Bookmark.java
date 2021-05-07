package com.liuyanzhao.forum.entity;




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

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }
}
