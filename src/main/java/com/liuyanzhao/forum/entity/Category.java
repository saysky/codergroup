package com.liuyanzhao.forum.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.hibernate.validator.constraints.NotEmpty;


import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * @author 言曌
 * @date 2018/3/19 下午9:54
 */

@Entity(name = "category")
public class Category implements Serializable {


    private static final long serialVersionUID = -1302049482786492870L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自增长策略
    @Column(name = "id", nullable = false)
    private Long id;

    @NotEmpty(message = "分类名称不可为空")
    @Size(min = 1, max = 20, message = "分类名长度必须为1-20个字符")
    @Column(nullable = false)
    private String name;

    @Column(name = "is_hidden",length = 1)
    private String isHidden = "N";//前台是否隐藏

    @Column(length = 10)
    private Integer position;//排序

    //我级联他
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;//用户

    @OneToMany(mappedBy = "category", cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    private List<Article> articleList;//文章列表

    @Transient
    private Integer articleSize;

    private String guid;

    public Integer getArticleSize() {
        if(articleList == null) {
            return 0;
        }
        return articleList.size();
    }

    public void setArticleSize(Integer articleSize) {
        if(articleList == null) {
            this.articleSize = 0;
        }
        this.articleSize = articleList.size();
    }


    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", isHidden='" + isHidden + '\'' +
                ", position=" + position +
                ", guid='" + guid + '\'' +
                '}';
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIsHidden() {
        return isHidden;
    }

    public void setIsHidden(String isHidden) {
        this.isHidden = isHidden;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Article> getArticleList() {
        return articleList;
    }

    public void setArticleList(List<Article> articleList) {
        this.articleList = articleList;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }
}
