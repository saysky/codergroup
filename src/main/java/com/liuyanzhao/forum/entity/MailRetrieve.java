package com.liuyanzhao.forum.entity;



import javax.persistence.*;
import java.io.Serializable;

/**
 * @author 言曌
 * @date 2018/2/23 上午10:24
 */

@Entity
@Table(name = "mail_retrieve")
public class MailRetrieve implements Serializable {
    private static final long serialVersionUID = -6320447428584162925L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "account", nullable = true, length = 100, unique = true)
    private String account;

    @Column(name = "sid", nullable = true, length = 255)
    private String sid;

    @Column(name = "create_time")
    private Long createTime = System.currentTimeMillis();

    @Column(name = "out_time", nullable = true, length = 100)
    private long outTime;

    public MailRetrieve() {
    }

    public MailRetrieve(String account, String sid, long outTime) {
        this.account = account;
        this.sid = sid;
        this.outTime = outTime;
    }


    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public long getOutTime() {
        return outTime;
    }

    public void setOutTime(long outTime) {
        this.outTime = outTime;
    }
}