package com.liuyanzhao.forum.controller;

import com.liuyanzhao.forum.controller.common.BaseController;
import com.liuyanzhao.forum.entity.Bulletin;
import com.liuyanzhao.forum.entity.User;
import com.liuyanzhao.forum.enums.PostStatusEnum;
import com.liuyanzhao.forum.enums.SiteTitleEnum;
import com.liuyanzhao.forum.exception.NotFoundException;
import com.liuyanzhao.forum.service.AnswerService;
import com.liuyanzhao.forum.service.BulletinService;
import com.liuyanzhao.forum.service.RankService;
import com.liuyanzhao.forum.service.UserService;
import com.liuyanzhao.forum.util.ConstraintViolationExceptionHandler;
import com.liuyanzhao.forum.vo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.ConstraintViolationException;
import java.util.Objects;

/**
 * @author 言曌
 * @date 2018/6/10 上午11:25
 */
@Controller
public class BulletinController extends BaseController {

    @Autowired
    private BulletinService bulletinService;

    @Autowired
    private UserService userService;


    /**
     * 公告列表显示
     *
     * @param async
     * @param pageIndex
     * @param pageSize
     * @param model
     * @return
     */
    @GetMapping("/bulletins")
    public ModelAndView frontbulletinList(@RequestParam(value = "async", required = false, defaultValue = "false") Boolean async,
                                          @RequestParam(value = "pageIndex", required = false, defaultValue = "1") Integer pageIndex,
                                          @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize, Model model) {
        Sort sort = new Sort(Sort.Direction.DESC, "position", "id");
        PageRequest pageRequest = new PageRequest(pageIndex - 1, pageSize, sort);
        Page<Bulletin> bulletinPage = bulletinService.listPublishbulletin(pageRequest);
        model.addAttribute("page", bulletinPage);
        model.addAttribute("site_title", SiteTitleEnum.bulletin_HOME.getTitle());

        return new ModelAndView(async == true ? "home/bulletin_list :: #bulletin-box-replace" : "home/bulletin_list");
    }


    /**
     * 获得公告创建页面
     *
     * @return
     */
    @GetMapping("/admin/bulletin/new")
    public String bulletinNew() {
        return "admin/bulletin/add";
    }


    /**
     * 获得公告修改页面
     *
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/admin/bulletin/edit/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")  // 指定角色权限才能操作方法
    public String bulletinEdit(@PathVariable(value = "id") Long id, Model model) {
        Bulletin bulletin = bulletinService.getbulletinById(id);
        //1、判断公告是否存在
        if (bulletin == null) {
            throw new NotFoundException("公告不存在!");
        }

        //2、判断是否可以访问
        if (Objects.equals(PostStatusEnum.DELETED_POST.getCode(), bulletin.getStatus())) {
            throw new NotFoundException("该公告已被管理员删除!");
        }

        model.addAttribute("bulletin", bulletin);
        return "admin/bulletin/edit";
    }

    /**
     * 获得公告详情页面
     *
     * @param name
     * @param model
     * @return
     */
    @GetMapping("/bulletins/{name}")
    public ModelAndView bulletinDetailByKey(@PathVariable(value = "name") String name,
                                       Model model) {
        Bulletin bulletin = bulletinService.getBulletinByName(name);
        //1、判断公告是否存在
        if (bulletin == null) {
            throw new NotFoundException("公告不存在!");
        }

        //2、判断是否可以访问
        if (Objects.equals(PostStatusEnum.DELETED_POST.getCode(), bulletin.getStatus())) {
            throw new NotFoundException("该公告已被管理员删除!");
        }

        // 3、判断操作用户是否是公告的所有者
        boolean isOwner = false;
        User principal = null;
        String username = bulletin.getUser().getUsername();
        if (SecurityContextHolder.getContext().getAuthentication() != null && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
                && !SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString().equals("anonymousUser")) {
            principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal != null && username.equals(principal.getUsername())) {
                isOwner = true;
            }
        }
        model.addAttribute("isOwner", isOwner);
        model.addAttribute("bulletin", bulletin);
        return new ModelAndView("home/bulletin_detail");
    }


    /**
     * 发布公告提交
     *
     * @param bulletin
     * @return
     */
    @PostMapping("/admin/bulletin")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")  // 指定角色权限才能操作方法
    public ResponseEntity<Response> savebulletin(@RequestBody Bulletin bulletin) {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();        user = userService.getUserByUsername(user.getUsername());
        String guid = null;

        try {
            //1、 判断是修改还是新增
            if (bulletin.getId() != null) {
                //修改
                Bulletin originalbulletin = bulletinService.getbulletinById(bulletin.getId());
                originalbulletin.setTitle(bulletin.getTitle());
                originalbulletin.setContent(bulletin.getContent());
                originalbulletin.setPosition(bulletin.getPosition());
                originalbulletin.setStatus(bulletin.getStatus());
                originalbulletin.setIsSticky(bulletin.getIsSticky());
                originalbulletin.setName(bulletin.getName());
                bulletinService.savebulletin(originalbulletin);
                guid = originalbulletin.getGuid();
            } else {
                //新增
                bulletin.setUser(user);
                Bulletin result = bulletinService.savebulletin(bulletin);
                guid = result.getGuid();
            }

        } catch (ConstraintViolationException e) {
            e.printStackTrace();
            return ResponseEntity.ok().body(new Response(false, ConstraintViolationExceptionHandler.getMessage(e)));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }

        return ResponseEntity.ok().body(new Response(true, "处理成功", guid));
    }

    /**
     * 后台公告列表显示
     *
     * @param async
     * @param pageIndex
     * @param pageSize
     * @param model
     * @return
     */
    @GetMapping("/admin/bulletin")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")  // 指定角色权限才能操作方法
    public ModelAndView adminbulletinList(@RequestParam(value = "async", required = false, defaultValue = "false") Boolean async,
                                          @RequestParam(value = "status", required = false, defaultValue = "publish") String status,
                                          @RequestParam(value = "pageIndex", required = false, defaultValue = "1") Integer pageIndex,
                                          @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize, Model model) {
        Sort sort = new Sort(Sort.Direction.DESC, "position", "id");
        PageRequest pageRequest = new PageRequest(pageIndex - 1, pageSize, sort);
        Page<Bulletin> bulletinPage = null;
        if (PostStatusEnum.PUBLISH_POST.getCode().equals(status)) {
            bulletinPage = bulletinService.listPublishbulletin(pageRequest);
            model.addAttribute("status", PostStatusEnum.PUBLISH_POST.getCode());
        } else if (PostStatusEnum.DRAFT_POST.getCode().equals(status)) {
            bulletinPage = bulletinService.listBulletinByStatus(PostStatusEnum.DRAFT_POST.getCode(), pageRequest);
            model.addAttribute("status", PostStatusEnum.DRAFT_POST.getCode());
        } else if (PostStatusEnum.DELETED_POST.getCode().equals(status)) {
            bulletinPage = bulletinService.listBulletinByStatus(PostStatusEnum.DELETED_POST.getCode(), pageRequest);
            model.addAttribute("status", PostStatusEnum.DELETED_POST.getCode());
        } else {
            bulletinPage = bulletinService.listAllbulletin(pageRequest);
            model.addAttribute("status", PostStatusEnum.ALL_POST.getCode());
        }
        model.addAttribute("page", bulletinPage);
        model.addAttribute("site_title", SiteTitleEnum.bulletin_ADMIN.getTitle());
        return new ModelAndView(async == true ? "admin/bulletin/list :: #tab-pane" : "admin/bulletin/list");
    }



    /**
     * 删除公告
     * @param id
     * @return
     */
    @DeleteMapping("/admin/bulletin/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")  // 指定角色权限才能操作方法
    public ResponseEntity<Response> deleteTag(@PathVariable("id") Long id) {
        try {
            bulletinService.removebulletin(id);
        } catch (ConstraintViolationException e) {
            return ResponseEntity.ok().body(new Response(false, ConstraintViolationExceptionHandler.getMessage(e)));
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.ok().body(new Response(true, "删除成功"));
    }
}
