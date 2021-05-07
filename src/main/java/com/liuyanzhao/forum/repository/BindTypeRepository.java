package com.liuyanzhao.forum.repository;

import com.liuyanzhao.forum.entity.BindType;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author 言曌
 * @date 2018/5/15 上午9:13
 */

public interface BindTypeRepository extends JpaRepository<BindType, Integer> {

    BindType findByName(String name);
}
