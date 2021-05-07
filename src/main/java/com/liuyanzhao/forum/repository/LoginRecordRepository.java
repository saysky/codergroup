package com.liuyanzhao.forum.repository;

import com.liuyanzhao.forum.entity.LoginRecord;
import com.liuyanzhao.forum.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author 言曌
 * @date 2018/3/26 上午8:58
 */

public interface LoginRecordRepository extends JpaRepository<LoginRecord, Long> {

    List<LoginRecord> findTop50ByUserOrderByIdDesc(User user);

    Page<LoginRecord> findByUser(User user, Pageable pageable);
}
