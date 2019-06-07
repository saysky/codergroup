package com.liuyanzhao.forum.repository;

import com.liuyanzhao.forum.entity.Slide;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author 言曌
 * @date 2018/6/11 下午4:18
 */

public interface SlideRepository extends JpaRepository<Slide, Integer> {

    List<Slide> findByStatus(String status, Sort sort);

}
