package com.liuyanzhao.forum.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 后台菜单
 * @author 言曌
 * @date 2018/3/20 下午2:57
 */

@Data
public class MenuDTO implements Serializable {

    private static final long serialVersionUID = 1913314222204056046L;
    private String icon;
    private String name;
    private String url;

    public MenuDTO() {
    }

    public MenuDTO(String icon, String name, String url) {
        this.icon = icon;
        this.name = name;
        this.url = url;
    }
}
