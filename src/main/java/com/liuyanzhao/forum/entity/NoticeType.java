package com.liuyanzhao.forum.entity;



import javax.persistence.*;

/**
 * @author 言曌
 * @date 2018/5/29 下午7:17
 */
@Entity(name = "notice_type")
public class NoticeType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String content;

    private String style;

    @Transient
    private Integer count;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
