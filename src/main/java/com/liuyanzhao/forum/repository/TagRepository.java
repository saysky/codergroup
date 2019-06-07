package com.liuyanzhao.forum.repository;

import com.liuyanzhao.forum.entity.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * @author 言曌
 * @date 2018/5/24 下午10:42
 */

public interface TagRepository extends JpaRepository<Tag,Integer> {

    @Override
    Page<Tag> findAll(Pageable pageable);

    Tag findByName(String name);


}
