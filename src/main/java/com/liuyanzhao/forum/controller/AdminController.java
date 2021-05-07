package com.liuyanzhao.forum.controller;

import com.liuyanzhao.forum.entity.Slide;
import com.liuyanzhao.forum.repository.SlideRepository;
import com.liuyanzhao.forum.util.ConstraintViolationExceptionHandler;
import com.liuyanzhao.forum.vo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolationException;
import java.util.List;


/**
 * @author 言曌
 * @date 2018/3/19 下午11:21
 */

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")  // 指定角色权限才能操作方法
public class AdminController {

    @Autowired
    private SlideRepository slideRepository;

    /**
     * 获取后台管理主页面
     *
     * @return
     */
    @GetMapping
    public String index() {
//        return "admin/index";
        return "forward:/admin/user";
    }


    @GetMapping("/settings")
    public String settings() {
        return "forward:/admin/settings/slide";
    }

    /**
     * 幻灯片设置页面
     *
     * @return
     */
    @GetMapping("/settings/slide")
    public String slidePage(Model model) {
        Sort sort = new Sort(Sort.Direction.DESC, "position");
        List<Slide> slideList = slideRepository.findAll(sort);
        model.addAttribute("slideList", slideList);
        return "admin/settings/slide";
    }

    @GetMapping("/settings/slide/{id}")
    public String slidePage(@PathVariable("id") Integer id, Model model) {
        Slide slide = slideRepository.findOne(id);
        model.addAttribute("slide", slide);
        return "admin/settings/slide-edit";
    }


    /**
     * 添加幻灯片提交
     *
     * @param slide
     * @return
     */
    @PostMapping("/settings/slide")
    public ResponseEntity<Response> submitSlide(@RequestBody Slide slide) {
        try {
            slideRepository.save(slide);
        } catch (ConstraintViolationException e) {
            e.printStackTrace();
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.ok().body(new Response(true, "操作成功"));
    }


    /**
     * @param id
     * @return
     */
    @DeleteMapping("/settings/slide/{id}")
    public ResponseEntity<Response> deleteTag(@PathVariable("id") Integer id) {
        try {
            slideRepository.delete(id);
        } catch (ConstraintViolationException e) {
            return ResponseEntity.ok().body(new Response(false, ConstraintViolationExceptionHandler.getMessage(e)));
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.ok().body(new Response(true, "删除成功"));
    }
}
