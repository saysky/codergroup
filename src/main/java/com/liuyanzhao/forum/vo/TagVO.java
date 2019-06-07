package com.liuyanzhao.forum.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 言曌
 * @date 2018/4/2 上午10:42
 */

@Data
public class TagVO implements Serializable {

    private static final long serialVersionUID = -9192192840980858203L;

    private String name;

    public TagVO() {
    }

    public TagVO(String name) {
        this.name = name;
    }
}
