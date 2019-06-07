package com.liuyanzhao.forum.repository;

import com.liuyanzhao.forum.entity.Bulletin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author 言曌
 * @date 2018/6/10 上午11:14
 */

public interface BulletinRepository extends JpaRepository<Bulletin, Long> {

    Page<Bulletin> findByStatus(String status, Pageable pageable);

    Bulletin findByName(String name);
}
