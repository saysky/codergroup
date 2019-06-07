package com.liuyanzhao.forum.repository;

import com.liuyanzhao.forum.entity.Category;
import com.liuyanzhao.forum.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author 言曌
 * @date 2018/3/21 上午11:27
 */

public interface CategoryRepository extends JpaRepository<Category, Long> {


    /**
     * 根据用户查找分类,分页
     *
     * @param user
     * @return
     */
    Page<Category> findByUserOrderByPositionAsc(User user, Pageable pageable);

    /**
     * 根据用户查找用户，不分页
     *
     * @param user
     * @return
     */
    List<Category> findByUserOrderByPositionAsc(User user);


    /**
     * 根据用户和分类名来查找
     *
     * @param user
     * @param name
     * @return
     */
    List<Category> findByUserAndName(User user, String name);

    /**
     * 根据用户名和分类id查找
     *
     * @param user
     * @param id
     * @return
     */
    Category findByUserAndId(User user, Long id);

    @Query(value = "select max(position) from category where user_id = ?1", nativeQuery = true)
    Integer getMaxPosition(Integer userId);


    Integer deleteByUser(User user);
}
