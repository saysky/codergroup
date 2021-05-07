package com.liuyanzhao.forum.repository;

import com.liuyanzhao.forum.entity.Message;
import com.liuyanzhao.forum.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author 言曌
 * @date 2018/4/27 下午9:15
 */

public interface MessageRepository extends JpaRepository<Message, Long> {


    /**
     * 获得聊天列表
     *
     * @param userId
     * @return
     */
    @Query(value = "SELECT * FROM  message WHERE id IN (SELECT MAX(id) AS id  FROM message WHERE user_id= ?1 AND status != 3 GROUP BY friend_id) ORDER BY id DESC", nativeQuery = true)
    List<Message> getChatList(Integer userId);

    /**
     * 聊天详情页
     *
     * @param user
     * @param Friend
     * @param statusList
     * @return
     */
    List<Message> findByUserAndFriendAndStatusIn(User user, User Friend, List<Integer> statusList);


    /**
     * 查找未读消息的最后一条
     *
     * @param user
     * @param Friend
     * @param statusList
     * @return
     */
    Message findTopByUserAndFriendAndStatusInOrderByIdDesc(User user, User Friend, List<Integer> statusList);


    /**
     * 获得未读私信的总数
     *
     * @param user
     * @param status
     * @return
     */
    Integer countByUserAndStatus(User user, Integer status);



    /**
     * 查询未读的列表(每个好友只显示一个)
     * @param userId
     * @param status
     * @return
     */
    @Query(value = "SELECT * FROM message WHERE id IN(SELECT Max(id) AS id FROM message WHERE user_id = ?1 AND status = ?2 GROUP BY friend_id) ORDER BY id DESC", nativeQuery = true)
    List<Message> findByUserAndStatus(Integer userId, Integer status);


    /**
     *
     * @param userId
     * @param status
     * @return
     */
    @Query(value = "SELECT count(id) FROM message WHERE user_id = ?1 AND status = ?2 GROUP BY friend_id ORDER BY Max(id) DESC", nativeQuery = true)
    List<Integer> listCountNotReadMessageSize(Integer userId, Integer status);


    /**
     * 获得未阅读的私信列表
     *
     * @param user
     * @param status
     * @return
     */
    List<Message> findByUserAndStatus(User user, Integer status);

    Integer deleteByFriend(User user);

    Integer deleteByUser(User user);

}

