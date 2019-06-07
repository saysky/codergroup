package com.liuyanzhao.forum.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Pattern;

/**
 * @author 言曌
 * @date 2018/6/11 下午4:14
 */
@Entity(name = "slide")
@Setter
@Getter
public class Slide {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String picture;

    private String title;

    private String guid;

    private Integer position;

    @Pattern(regexp = "publish|deleted")
    private String status;//状态：publish/deleted
}
