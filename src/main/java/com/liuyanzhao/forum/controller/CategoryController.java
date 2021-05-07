package com.liuyanzhao.forum.controller;

import com.liuyanzhao.forum.entity.Category;
import com.liuyanzhao.forum.entity.User;
import com.liuyanzhao.forum.enums.SiteTitleEnum;
import com.liuyanzhao.forum.service.CategoryService;
import com.liuyanzhao.forum.service.UserService;
import com.liuyanzhao.forum.util.ConstraintViolationExceptionHandler;
import com.liuyanzhao.forum.vo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.ConstraintViolationException;
import java.security.Principal;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * @author 言曌
 * @date 2018/5/7 下午3:52
 */

@Controller
public class CategoryController {


    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;


//用户中心

    /**
     * @param async
     * @param pageIndex
     * @param pageSize
     * @param principal
     * @param model
     * @return
     */
    @GetMapping("/manage/categorys")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")  // 指定角色权限才能操作方法
    public ModelAndView listCategorys(@RequestParam(value = "async", required = false, defaultValue = "false") boolean async,
                                      @RequestParam(value = "pageIndex", required = false, defaultValue = "1") Integer pageIndex,
                                      @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                                      Principal principal, Model model) {
        User user = userService.getUserByUsername(principal.getName());
        PageRequest pageRequest = new PageRequest(pageIndex - 1, pageSize);
        Page<Category> categoryPage = categoryService.listCategorys(user, pageRequest);
        model.addAttribute("page", categoryPage);
        model.addAttribute("site_title", SiteTitleEnum.CATEGORY_MANAGE.getTitle());
        return new ModelAndView(async == true ? "home/userspace/category :: #right-box-body-replace" : "home/userspace/category");
    }

    /**
     * 创建
     * 限制创建100个
     *
     * @param categoryName
     * @return
     */
    @PostMapping("/category")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")  // 指定角色权限才能操作方法
    public ResponseEntity<Response> create(Principal principal, String categoryName) {
        User user = userService.getUserByUsername(principal.getName());

        String name = categoryName.trim();
        //1、判断长度和排除空格
        if (name.length() < 1 || name.length() > 20) {
            return ResponseEntity.ok().body(new Response(false, "分类名长度不合法"));
        }
        //2、如果该分类名已存在，禁止创建
        List<Category> categoryList = categoryService.listCategorysByUserAndName(user, name);
        if (categoryList.size() != 0) {
            return ResponseEntity.ok().body(new Response(false, "分类名已存在"));
        }
        //3、每个人只能创建100个分类
        if (categoryService.listCategorys(user).size() >= 100) {
            return ResponseEntity.ok().body(new Response(false, "分类已经够多啦，不能再创建啦！"));
        }
        Category result = null;
        Category category = new Category();
        category.setName(name);
        category.setUser(user);
        try {
            result = categoryService.saveCategory(category);
        } catch (ConstraintViolationException e) {
            return ResponseEntity.ok().body(new Response(false, ConstraintViolationExceptionHandler.getMessage(e)));
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.ok().body(new Response(true, "添加成功", result));
    }

    /**
     * 删除分类
     *
     * @param id
     * @return
     */
    @DeleteMapping("/category/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")  // 指定角色权限才能操作方法
    public ResponseEntity<Response> deleteCategory(Principal principal, @PathVariable("id") Long id) {
        User user = userService.getUserByUsername(principal.getName());
        Category category = null;
        //1、判断分类不为空
        try {
            category = categoryService.getCategoryById(id);
        } catch (NoSuchElementException e) {
            return ResponseEntity.ok().body(new Response(false, "分类不存在"));

        }

        //2、判断分类是否属于该用户
        if (!category.getUser().equals(user)) {
            return ResponseEntity.ok().body(new Response(false, "没有操作权限！"));
        }

        //3、删除分类
        categoryService.removeCategory(category.getId());

        return ResponseEntity.ok().body(new Response(true, "删除成功"));
    }


    /**
     * 更新
     * 限制创建100个
     *
     * @param categoryName
     * @return
     */
    @PutMapping("/category/{id}/name")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")  // 指定角色权限才能操作方法
    public ResponseEntity<Response> updateCategoryName(Principal principal, @PathVariable("id") Long categoryId, String categoryName) {
        User user = userService.getUserByUsername(principal.getName());
        Category category = null;
        String name = categoryName.trim();
        //1、判断长度和排除空格
        if (name.length() < 1 || name.length() > 20) {
            return ResponseEntity.ok().body(new Response(false, "分类名长度不合法"));
        }
        //2、判断分类不为空
        try {
            category = categoryService.getCategoryById(categoryId);
        } catch (NoSuchElementException e) {
            return ResponseEntity.ok().body(new Response(false, "分类不存在"));
        }
        //3、判断分类是否属于该用户
        if (!category.getUser().equals(user)) {
            return ResponseEntity.ok().body(new Response(false, "没有操作权限！"));
        }
        //4、如果该分类名已存在，禁止创建
        List<Category> categoryList = categoryService.listCategorysByUserAndName(user, name);
        if (categoryList.size() != 0) {
            return ResponseEntity.ok().body(new Response(false, "分类名已存在"));
        }
        //5、每个人只能创建100个分类
        if (categoryService.listCategorys(user).size() >= 100) {
            return ResponseEntity.ok().body(new Response(false, "分类已经够多啦，不能再创建啦！"));
        }

        //6、修改分类
        category.setName(name);
        categoryService.saveCategory(category);
        return ResponseEntity.ok().body(new Response(true, "修改成功"));
    }


    /**
     * 更新是否隐藏
     * 限制创建100个
     *
     * @return
     */
    @PutMapping("/category/{id}/isHidden")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")  // 指定角色权限才能操作方法
    public ResponseEntity<Response> updateCategoryIsHidden(Principal principal, @PathVariable("id") Long categoryId, String isHidden) {
        User user = userService.getUserByUsername(principal.getName());
        Category category = null;
        //1、判断分类不为空
        try {
            category = categoryService.getCategoryById(categoryId);
        } catch (NoSuchElementException e) {
            return ResponseEntity.ok().body(new Response(false, "分类不存在"));
        }
        //2、判断分类是否属于该用户
        if (!category.getUser().equals(user)) {
            return ResponseEntity.ok().body(new Response(false, "没有操作权限！"));
        }

        //3、修改分类
        category.setIsHidden(isHidden);
        categoryService.saveCategory(category);
        return ResponseEntity.ok().body(new Response(true, "修改成功"));
    }

    /**
     * 更新位置
     * 限制创建100个
     *
     * @return
     */
    @PutMapping("/category/position")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")  // 指定角色权限才能操作方法
    public ResponseEntity<Response> updateCategoryPosition(Principal principal, Long upId, Long currentId, Long downId) {
        User user = userService.getUserByUsername(principal.getName());

        Category upCategory = null;
        Category currentCategory = null;
        Category downCategory = null;

        //向上移动
        if (upId != null && currentId != null) {
            try {
                upCategory = categoryService.getCategoryById(upId);
                currentCategory = categoryService.getCategoryById(currentId);
            } catch (NoSuchElementException e) {
                return ResponseEntity.ok().body(new Response(false, "分类不存在"));
            }
            if (!upCategory.getUser().equals(user) || !currentCategory.getUser().equals(user)) {
                return ResponseEntity.ok().body(new Response(false, "没有操作权限！"));
            }
            categoryService.changePriority(user, currentId, upId);
            return ResponseEntity.ok().body(new Response(true, "操作成功"));
        }

        //向下移动
        if (downId != null && currentId != null) {
            try {
                currentCategory = categoryService.getCategoryById(currentId);
                downCategory = categoryService.getCategoryById(downId);

            } catch (NoSuchElementException e) {
                return ResponseEntity.ok().body(new Response(false, "分类不存在"));
            }
            if (!downCategory.getUser().equals(user) || !currentCategory.getUser().equals(user)) {
                return ResponseEntity.ok().body(new Response(false, "没有操作权限！"));
            }
            categoryService.changePriority(user, currentId, downId);
        }

        return ResponseEntity.ok().body(new Response(true, "操作成功"));

    }
}
