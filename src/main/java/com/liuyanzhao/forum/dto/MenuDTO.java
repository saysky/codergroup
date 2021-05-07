package com.liuyanzhao.forum.dto;



import java.io.Serializable;

/**
 * 后台菜单
 * @author 言曌
 * @date 2018/3/20 下午2:57
 */


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

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
