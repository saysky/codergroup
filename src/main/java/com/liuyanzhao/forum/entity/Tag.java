package com.liuyanzhao.forum.entity;


import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * @author 言曌
 * @date 2018/5/24 下午10:42
 */
@Entity(name = "tag")
@Data
public class Tag implements Serializable{

    private static final long serialVersionUID = 8818653536688119316L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自增长策略
    private Integer id;

    @Column(unique = true)
    @Size(min = 1, max = 20,message = "标签名长度为1-20个字符")
    private String name;

    private String guid;

    @Column(name = "article_size")
    private Integer articleSize;

    private Integer position = 1;

}
