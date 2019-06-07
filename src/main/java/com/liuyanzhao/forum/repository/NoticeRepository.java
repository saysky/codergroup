package com.liuyanzhao.forum.repository;

import com.liuyanzhao.forum.entity.Notice;
import com.liuyanzhao.forum.entity.NoticeType;
import com.liuyanzhao.forum.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author 言曌
 * @date 2018/5/29 下午7:27
 */

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    /**
     * 获得一个用户的未阅读的提醒，用于批量标记已阅读
     *
     * @param toUser
     * @param status
     * @return
     */
    List<Notice> findByToUserAndStatus(User toUser, Integer status);


    /**
     * 获得一个用户的所有提醒，用于删除
     *
     * @param user
     * @return
     */
    List<Notice> findByToUser(User user);

    /**
     * 获得用户的所有提醒，用于前台分页显示
     *
     * @param user
     * @param pageable
     * @return
     */
    Page<Notice> findByToUser(User user, Pageable pageable);


    /**
     * 按提醒分类统计没有阅读的提醒的数量，返回列表
     *
     * @return
     */
    @Query(value = "select count(id) from notice where to_user_id = ?1 and status = 0 group by notice_type_id order by notice_type_id", nativeQuery = true)
    List<Integer> getCountListByNotRead(Integer userId);

    /**
     * 按提醒分类统计没有阅读的提醒的数量，返回列表
     *
     * @return
     */
    @Query(value = "select notice_type_id from notice where to_user_id = ?1 and status = 0 group by notice_type_id order by notice_type_id", nativeQuery = true)
    List<Integer> getTypeIdsByNotRead(Integer userId);

    /**
     * 统计未读数量
     * @param user
     * @param status
     * @return
     */
    Integer countByToUserAndStatus(User user, Integer status);

    Integer deleteByFromUser(User user);

    Integer deleteByToUser(User user);


}
