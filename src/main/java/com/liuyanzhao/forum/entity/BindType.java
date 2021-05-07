package com.liuyanzhao.forum.entity;




import javax.persistence.*;
import java.io.Serializable;

/**
 * @author 言曌
 * @date 2018/5/13 下午11:11
 */

@Entity(name = "bind_type")
public class BindType implements Serializable {

    private static final long serialVersionUID = 6168385705667918117L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, length = 20)
    private String name;

    @Column(length = 100)
    private String style;

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

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }
}
