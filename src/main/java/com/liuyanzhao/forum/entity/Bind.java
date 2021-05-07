package com.liuyanzhao.forum.entity;


import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @author 言曌
 * @date 2018/5/13 下午11:14
 */

@Entity(name = "bind")
public class Bind implements Serializable {


    private static final long serialVersionUID = 1746031292139389937L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bind_type_id")
    private BindType bindType;

    @Column(unique = true)
    @NotEmpty
    private String identifier;//识别符

    private String credential;//密码

    @org.hibernate.annotations.CreationTimestamp
    @Column(name = "create_time")
    private Timestamp createTime;

    private Integer status = 1;


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

    public BindType getBindType() {
        return bindType;
    }

    public void setBindType(BindType bindType) {
        this.bindType = bindType;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getCredential() {
        return credential;
    }

    public void setCredential(String credential) {
        this.credential = credential;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
