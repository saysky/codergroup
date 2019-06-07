package com.liuyanzhao.forum.entity;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @author 言曌
 * @date 2018/5/13 下午11:14
 */

@Entity(name = "bind")
@Data
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
}
