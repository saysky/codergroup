package com.liuyanzhao.forum.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * @author 言曌
 * @date 2018/5/29 下午7:17
 */
@Entity(name = "notice_type")
@Data
public class NoticeType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String content;

    private String style;

    @Transient
    private Integer count;
}
