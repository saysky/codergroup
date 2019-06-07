package com.liuyanzhao.forum.service;

import com.liuyanzhao.forum.entity.Relationship;
import com.liuyanzhao.forum.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author 言曌
 * @date 2018/4/24 下午10:10
 */

public interface RelationshipService {

    /**
     * 列出所有的关注者
     *
     * @return
     */
    Page<User> listFollows(Integer userId, Pageable pageable);


    /**
     * 列出所有的粉丝
     *
     * @return
     */
    Page<User> listFans(Integer userId, Pageable pageable);

    /**
     * 列出互相关注的id
     *
     * @param userId
     * @return
     */
    List<Integer> listFriends(Integer userId);

    /**
     * 添加关系
     *
     * @param relationship
     */
    void saveRelationship(Relationship relationship);

    /**
     * 去除关系
     *
     * @param relationship
     */
    void removeRelationship(Relationship relationship);

    /**
     * 更新关注数
     */
    void updateFollowSize(Integer userId);

    /**
     * 更新粉丝数
     */
    void updateFanSize(Integer userId);

    /**
     * 获得两个用户之间的关系，0未关注，1已关注，2互相关注
     * @param fromUserId
     * @param toUserId
     * @return
     */
    Integer getRelationshipBetweenUsers(Integer fromUserId, Integer toUserId);

}
