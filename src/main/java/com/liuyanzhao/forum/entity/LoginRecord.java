package com.liuyanzhao.forum.entity;



import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @author 言曌
 * @date 2018/3/26 上午9:11
 */

@Entity(name = "login_record")
public class LoginRecord implements Serializable {


    private static final long serialVersionUID = 3819695643032970580L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自增长策略
    private Long id;

    @Column(name = "login_type")
    private String loginType;//登录方式

    @org.hibernate.annotations.CreationTimestamp  // 由数据库自动创建时间
    @Column(name = "create_time")
    private Timestamp createTime;

    @Column(length = 40)
    private String ip;//IP

    @Column(length = 100)
    private String area;//归属地

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;


    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
