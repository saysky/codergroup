package com.liuyanzhao.forum.service;

import com.liuyanzhao.forum.entity.Category;
import com.liuyanzhao.forum.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author 言曌
 * @date 2018/3/28 下午3:13
 */

public interface CategoryService {


    Category getCategoryById(Long id);

    /**
     * 获得某个用户的分类列表
     *
     * @param user
     * @return
     */
    List<Category> listCategorys(User user);

    /**
     * 获得某个用户的分类列表，分页显示
     * @param user
     * @param pageable
     * @return
     */
    Page<Category> listCategorys(User user, Pageable pageable);


    /**
     * 添加/更新分类
     *
     * @param category
     */
    Category saveCategory(Category category);

    /**
     * 根据用户和分类名查询分类列表
     *
     * @param user
     * @param name
     * @return
     */
    List<Category> listCategorysByUserAndName(User user, String name);

    /**
     * 删除分类
     *
     * @param id
     */
    void removeCategory(Long id);


    /**
     * 向上移动
     *
     * @param currentId 当前分类的id
     * @param otherId   另一个要交换的分类的id
     */
    void changePriority(User user, Long currentId, Long otherId);


    /**
     * 根据分类的id和用户查找
     *
     * @param user
     * @param id
     * @return
     */
    Category getCategoryByUserAndId(User user, Long id);
}
