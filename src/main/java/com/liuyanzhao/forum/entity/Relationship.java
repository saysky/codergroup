package com.liuyanzhao.forum.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author 言曌
 * @date 2018/4/24 下午9:38
 */

@Entity(name = "relationship")
@IdClass(RelationshipPK.class)
@Data
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
}
