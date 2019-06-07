package com.liuyanzhao.forum.repository;

import com.liuyanzhao.forum.entity.NoticeType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author 言曌
 * @date 2018/6/2 上午7:47
 */

public interface NoticeTypeRepository extends JpaRepository<NoticeType,Integer> {

    List<NoticeType> findByIdInOrderById(List<Integer> ids);
}
