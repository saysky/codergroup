package com.liuyanzhao.forum.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author 言曌
 * @date 2018/2/23 上午10:24
 */

@Entity
@Table(name = "mail_retrieve")
@Data
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


}