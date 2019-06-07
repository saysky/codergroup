package com.liuyanzhao.forum.entity;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author 言曌
 * @date 2018/4/2 下午4:57
 */

@Entity(name = "job")
@Data
public class Job implements Serializable {

    private static final long serialVersionUID = -9005722255873746978L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotEmpty(message = "职位名称不能为空")
    @Column(length = 20, unique = true)
    private String name;//职位名称



}
