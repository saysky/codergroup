package com.liuyanzhao.forum.entity;



import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.sql.Timestamp;

/**
 * 话题
 *
 * @author 言曌
 * @date 2018/6/10 上午10:52
 */
@Entity(name = "bulletin")
public class Bulletin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自增长策略
    private Long id;

    @Column(name = "name", unique = true)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @NotEmpty(message = "标题不能为空")
    @Size(max = 200, message = "标题过长，请删减")
    @Column(nullable = false, length = 200)
    private String title;

    @Lob  // 大对象，映射 MySQL 的 Long Text 类型
    @Basic(fetch = FetchType.LAZY) // 懒加载
    @NotEmpty(message = "内容不能为空")
    @Size(max = 10000, message = "内容过长，请删减")
    @Column(nullable = false, length = 10000)
    private String content;

    private String guid;

    private Integer position = 1;//优先级排序

    @Column(name = "is_sticky")
    private Integer isSticky = 0;//0不置顶，1置顶

    @Pattern(regexp = "publish|draft|deleted")
    private String status = "publish";

    @org.hibernate.annotations.CreationTimestamp  // 由数据库自动创建时间
    @Column(name = "create_time")
    private Timestamp createTime;

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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Integer getIsSticky() {
        return isSticky;
    }

    public void setIsSticky(Integer isSticky) {
        this.isSticky = isSticky;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }
}
