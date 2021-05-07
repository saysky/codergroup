package com.liuyanzhao.forum.entity;


import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author 言曌
 * @date 2018/4/2 下午4:57
 */

@Entity(name = "job")
public class Job implements Serializable {

    private static final long serialVersionUID = -9005722255873746978L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotEmpty(message = "职位名称不能为空")
    @Column(length = 20, unique = true)
    private String name;//职位名称


    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
