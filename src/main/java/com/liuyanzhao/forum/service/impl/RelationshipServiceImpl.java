package com.liuyanzhao.forum.service.impl;

import com.liuyanzhao.forum.entity.Relationship;
import com.liuyanzhao.forum.entity.User;
import com.liuyanzhao.forum.repository.RelationshipRepository;
import com.liuyanzhao.forum.repository.NoticeRepository;
import com.liuyanzhao.forum.repository.UserRepository;
import com.liuyanzhao.forum.service.RelationshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author 言曌
 * @date 2018/4/24 下午10:23
 */

@Service
public class RelationshipServiceImpl implements RelationshipService {

    @Autowired
    private RelationshipRepository relationshipRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NoticeRepository noticeRepository;

    @Override
    public Page<User> listFollows(Integer userId, Pageable pageable) {
        List<Integer> relationshipList = relationshipRepository.findByFromUserId(userId);
        Page<User> userPage = userRepository.findByIdIn(relationshipList, pageable);
        return userPage;
    }

    @Override
    public Page<User> listFans(Integer userId, Pageable pageable) {
        List<Integer> relationshipList = relationshipRepository.findByToUserId(userId);
        Page<User> userPage = userRepository.findByIdIn(relationshipList, pageable);
        return userPage;
    }

    @Override
    public List<Integer> listFriends(Integer userId) {
        List<Integer> relationshipList = relationshipRepository.findFriendsByUserId(userId);
//        List<User> userList = userRepository.findByIdIn(relationshipList);
        return relationshipList;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveRelationship(Relationship relationship) {
        //1、添加关注
        relationshipRepository.save(relationship);
        //2、更新双方关注数和粉丝数
        updateFollowSize(relationship.getFromUserId());
        updateFanSize(relationship.getToUserId());

    }

    @Override
    public void removeRelationship(Relationship relationship) {
        //删除关系
        relationshipRepository.delete(relationship);
        //更新双方关注数和粉丝数
        updateFollowSize(relationship.getFromUserId());
        updateFanSize(relationship.getToUserId());
    }

    @Override
    public void updateFollowSize(Integer userId) {
        User user = userRepository.findOne(userId);
        user.setFollowSize(relationshipRepository.countByFromUserId(userId));
        userRepository.save(user);
    }

    @Override
    public void updateFanSize(Integer userId) {
        User user = userRepository.findOne(userId);
        user.setFanSize(relationshipRepository.countByToUserId(userId));
        userRepository.save(user);

    }

    @Override
    public Integer getRelationshipBetweenUsers(Integer fromUserId, Integer toUserId) {
        if (relationshipRepository.findByFromUserIdAndToUserId(fromUserId, toUserId) != null) {
            //已关注
            if (relationshipRepository.findByToUserIdAndFromUserId(fromUserId, toUserId) == null) {
                return 1;
            } else {
                //互相关注
                return 2;
            }
        } else {
            //未关注
            return 0;
        }
    }


}
