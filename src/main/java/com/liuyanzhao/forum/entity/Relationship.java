package com.liuyanzhao.forum.entity;



import javax.persistence.*;
import java.io.Serializable;

/**
 * @author 言曌
 * @date 2018/4/24 下午9:38
 */

@Entity(name = "relationship")
@IdClass(RelationshipPK.class)
public class Relationship implements Serializable {


    private static final long serialVersionUID = -2887492513363730444L;
    @Id
    @Column(name = "from_user_id")
    private Integer fromUserId;

    @Id
    @Column(name = "to_user_id")
    private Integer toUserId;

    public Relationship(Integer fromUserId, Integer toUserId) {
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
    }

    public Relationship() {
    }


    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(Integer fromUserId) {
        this.fromUserId = fromUserId;
    }

    public Integer getToUserId() {
        return toUserId;
    }

    public void setToUserId(Integer toUserId) {
        this.toUserId = toUserId;
    }
}
