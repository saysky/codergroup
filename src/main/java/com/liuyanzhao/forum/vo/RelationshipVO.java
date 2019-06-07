package com.liuyanzhao.forum.vo;

/**
 * @author 言曌
 * @date 2018/4/24 下午5:22
 */

public class RelationshipVO {

    private boolean isFollow;

    private boolean isFan;

    public RelationshipVO() {
    }

    public RelationshipVO(boolean isFollow, boolean isFan) {
        this.isFollow = isFollow;
        this.isFan = isFan;
    }

    public boolean isFollow() {
        return isFollow;
    }

    public void setFollow(boolean follow) {
        isFollow = follow;
    }

    public boolean isFan() {
        return isFan;
    }

    public void setFan(boolean fan) {
        isFan = fan;
    }
}
