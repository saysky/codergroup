package com.liuyanzhao.forum.entity;


import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author 言曌
 * @date 2018/5/13 下午11:11
 */

@Entity(name = "bind_type")
@Data
public class BindType implements Serializable {

    private static final long serialVersionUID = 6168385705667918117L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, length = 20)
    private String name;

    @Column(length = 100)
    private String style;
}
