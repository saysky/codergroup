package com.liuyanzhao.forum.entity;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author 言曌
 * @date 2018/3/21 下午7:48
 */

@Entity(name = "authority")
@Data
public class Authority implements GrantedAuthority, Serializable {

    private static final long serialVersionUID = 2627785840265805178L;


    @Id // 主键
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自增长策略
    private Integer id; // 用户的唯一标识

    @Column(nullable = false)
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String getAuthority() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
