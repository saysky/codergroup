package com.liuyanzhao.forum.controller;

import com.liuyanzhao.forum.entity.Tag;
import com.liuyanzhao.forum.exception.NotFoundException;
import com.liuyanzhao.forum.service.ArticleService;
import com.liuyanzhao.forum.service.QuestionService;
import com.liuyanzhao.forum.service.TagService;
import com.liuyanzhao.forum.util.ConstraintViolationExceptionHandler;
import com.liuyanzhao.forum.vo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.ConstraintViolationException;
import java.util.Objects;

/**
 * @author 言曌
 * @date 2018/5/24 下午11:08
 */

@Controller
public class TagController {

    @Autowired
    private TagService tagService;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private QuestionService questionService;

    @GetMapping("/admin/tag")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")  // 指定角色权限才能操作方法
    public ModelAndView index(Model model,
                              @RequestParam(value = "async", required = false, defaultValue = "false") Boolean async,
                              @RequestParam(value = "pageIndex", required = false, defaultValue = "1") Integer pageIndex,
                              @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        Sort sort = new Sort(Sort.Direction.DESC, "position", "id");
        PageRequest pageRequest = new PageRequest(pageIndex - 1, pageSize, sort);
        Page<Tag> tagList = tagService.listTags(pageRequest);
        model.addAttribute("page", tagList);
        return new ModelAndView(async == true ? "admin/tag/list :: #mainContainerReplace" : "admin/tag/list");
    }


    @GetMapping("/admin/tag/edit/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")  // 指定角色权限才能操作方法
    public ModelAndView editTag(Model model,
                                @PathVariable(value = "id") Integer id,
                                @RequestParam(value = "async", required = false, defaultValue = "false") Boolean async,
                                @RequestParam(value = "pageIndex", required = false, defaultValue = "1") Integer pageIndex,
                                @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {

        Tag tag = tagService.getTagById(id);
        if (tag == null) {
            throw new NotFoundException("标签不存在");
        }
        if (async == false) {
            PageRequest pageRequest = new PageRequest(pageIndex - 1, pageSize);
            Page<Tag> tagList = tagService.listTags(pageRequest);
            model.addAttribute("page", tagList);
        }
        model.addAttribute("tag", tag);
        return new ModelAndView(async == true ? "admin/tag/edit :: #left-box-body-replace" : "admin/tag/edit");
    }


    /**
     * 创建
     *
     * @param tag
     * @return
     */
    @PostMapping("/admin/tag")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")  // 指定角色权限才能操作方法
    public ResponseEntity<Response> create(Tag tag) {
        Tag temp = tagService.getTagByName(tag.getName());
        //查找tag是否存在
        if (temp != null) {
            if (tag.getId() == null || !Objects.equals(tag.getId(), temp.getId())) {
                return ResponseEntity.ok().body(new Response(false, "标签已存在"));
            }
        }
        try {
            if (tag.getGuid() == null || "".equals(tag.getGuid())) {
                tag.setGuid("/article?keywords=" + tag.getName());
            }
            tagService.saveTag(tag);
        } catch (ConstraintViolationException e) {
            e.printStackTrace();
            return ResponseEntity.ok().body(new Response(false, ConstraintViolationExceptionHandler.getMessage(e)));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.ok().body(new Response(true, "添加成功"));
    }


    /**
     * @param id
     * @return
     */
    @DeleteMapping("/admin/tag/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")  // 指定角色权限才能操作方法
    public ResponseEntity<Response> deleteTag(@PathVariable("id") Integer id) {
        try {
            tagService.removeTag(id);
        } catch (ConstraintViolationException e) {
            return ResponseEntity.ok().body(new Response(false, ConstraintViolationExceptionHandler.getMessage(e)));
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.ok().body(new Response(true, "删除成功"));
    }


}
