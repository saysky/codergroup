package com.liuyanzhao.forum.service.impl;

import com.liuyanzhao.forum.entity.Category;
import com.liuyanzhao.forum.entity.User;
import com.liuyanzhao.forum.repository.CategoryRepository;
import com.liuyanzhao.forum.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author 言曌
 * @date 2018/3/29 上午9:52
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;


    @Override
    public Category getCategoryById(Long id) {
        return categoryRepository.findOne(id);
    }

    @Override
    public List<Category> listCategorys(User user) {
        return categoryRepository.findByUserOrderByPositionAsc(user);
    }

    @Override
    public Page<Category> listCategorys(User user, Pageable pageable) {
        return categoryRepository.findByUserOrderByPositionAsc(user, pageable);
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public Category saveCategory(Category category) {
        //创建分类
        if (category.getId() == null) {
            Integer maxPosition = categoryRepository.getMaxPosition(category.getUser().getId());
            if (maxPosition == null) {
                maxPosition = 1;
            } else {
                maxPosition = categoryRepository.getMaxPosition(category.getUser().getId()) + 1;
            }
            category.setPosition(maxPosition);
            Category returnCategory = categoryRepository.save(category);
            returnCategory.setGuid("/" + category.getUser().getUsername() + "/articles?category=" + returnCategory.getId());
            return returnCategory;
        } else {
            //更新分类
            return categoryRepository.save(category);
        }
    }

    @Override
    public List<Category> listCategorysByUserAndName(User user, String name) {
        return categoryRepository.findByUserAndName(user, name);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void removeCategory(Long id) {
        categoryRepository.delete(id);
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void changePriority(User user, Long currentId, Long otherId) {
        Category currentCategory = categoryRepository.findByUserAndId(user, currentId);//当前分类
        Category otherCategory = categoryRepository.findByUserAndId(user, otherId);//另一个分类
        Integer currentPriority = currentCategory.getPosition();//1
        Integer otherPriority = otherCategory.getPosition();//2
        //交换
        currentCategory.setPosition(otherPriority);
        otherCategory.setPosition(currentPriority);
        //写入数据库
        categoryRepository.save(currentCategory);
        categoryRepository.save(otherCategory);
    }

    @Override
    public Category getCategoryByUserAndId(User user, Long id) {
        return categoryRepository.findByUserAndId(user, id);
    }


}
