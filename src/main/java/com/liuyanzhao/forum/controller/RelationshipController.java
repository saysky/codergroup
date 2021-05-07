package com.liuyanzhao.forum.controller;

import com.liuyanzhao.forum.entity.Notice;
import com.liuyanzhao.forum.entity.NoticeType;
import com.liuyanzhao.forum.entity.Relationship;
import com.liuyanzhao.forum.entity.User;
import com.liuyanzhao.forum.enums.NoticeTypeEnum;
import com.liuyanzhao.forum.enums.SiteTitleEnum;
import com.liuyanzhao.forum.repository.NoticeRepository;
import com.liuyanzhao.forum.repository.UserRepository;
import com.liuyanzhao.forum.service.RelationshipService;
import com.liuyanzhao.forum.service.UserService;
import com.liuyanzhao.forum.util.RedisOperator;
import com.liuyanzhao.forum.vo.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.List;
import java.util.Objects;

/**
 * @author 言曌
 * @date 2018/5/2 下午3:54
 */

@Controller
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")  // 指定角色权限才能操作方法
public class RelationshipController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RelationshipService relationshipService;

    @Autowired
    private NoticeRepository noticeRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private RedisOperator redis;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    /**
     * 关注
     *
     * @param uid
     * @return
     */
    @PostMapping("/relationships/follow")
    public ResponseEntity<Response> follow(Integer uid) {
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findOne(uid);
        if (user == null) {
            return ResponseEntity.ok().body(new Response(false, "用户不存在！"));
        }
        if (Objects.equals(uid, principal.getId())) {
            return ResponseEntity.ok().body(new Response(false, "您时时刻刻都在关注自己哦！"));
        }
        //1、添加关系
        Relationship relationship = new Relationship(principal.getId(), uid);
        relationshipService.saveRelationship(relationship);
        //2、添加提醒
        Notice notice = new Notice();
        notice.setFromUser(principal);
        notice.setToUser(user);
        NoticeType noticeType = new NoticeType();
        noticeType.setId(NoticeTypeEnum.FOLLOW_YOU.getCode());
        notice.setNoticeType(noticeType);
        notice.setGuid("/manage/relationships/fans");
        try {
            noticeRepository.save(notice);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        try {
            redis.incr("noticeSize:" + user.getId(), 1);
        } catch (Exception e) {
            logger.error("redis服务故障",e);
        }
        return ResponseEntity.ok().body(new Response(true, "操作成功"));
    }

    /**
     * 取消关注
     *
     * @param uid
     * @return
     */
    @PostMapping("/relationships/notfollow")
    public ResponseEntity<Response> notfollow(Integer uid) {
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User user = userRepository.findOne(uid);
        if (user == null) {
            return ResponseEntity.ok().body(new Response(false, "用户不存在！"));

        }
        Relationship relationship = new Relationship(principal.getId(), uid);
        relationshipService.removeRelationship(relationship);

        return ResponseEntity.ok().body(new Response(true, "操作成功"));

    }


//    用户中心

    @GetMapping("/manage/relationships")
    public String relationships() {
        return "forward:/manage/relationships/follows";
    }

    //粉丝-关注 start

    /**
     * 查看所有的关注者
     *
     * @param async
     * @param page
     * @param size
     * @param model
     * @return
     */
    @GetMapping("/manage/relationships/follows")
    public ModelAndView follows(
            @RequestParam(value = "async", required = false) boolean async,
            @RequestParam(value = "pageIndex", defaultValue = "1", required = false) Integer page,
            @RequestParam(value = "pageSize", defaultValue = "12", required = false) Integer size,
            Model model) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        PageRequest pageRequest = new PageRequest(page - 1, size);
        Page<User> userPage = relationshipService.listFollows(user.getId(), pageRequest);

        List<Integer> friendIds = relationshipService.listFriends(user.getId());
        List<User> userList = userPage.getContent();
        for (int i = 0; i < userList.size(); i++) {
            if (friendIds.contains(userList.get(i).getId())) {
                userPage.getContent().get(i).setIsFriend(2);
            }
        }
        model.addAttribute("page", userPage);
        model.addAttribute("dataType", "follows");
        model.addAttribute("site_title", SiteTitleEnum.RELATIONSHIP_MANAGE.getTitle());
        return new ModelAndView(async == true ? "home/userspace/relationship :: #right-box-body-replace" : "home/userspace/relationship");

    }

    /**
     * 查看所有的粉丝
     *
     * @param principal
     * @param async
     * @param page
     * @param size
     * @param model
     * @return
     */
    @GetMapping("/manage/relationships/fans")
    public ModelAndView fans(
            Principal principal,
            @RequestParam(value = "async", required = false, defaultValue = "false") boolean async,
            @RequestParam(value = "pageIndex", defaultValue = "1", required = false) Integer page,
            @RequestParam(value = "pageSize", defaultValue = "12", required = false) Integer size,
            Model model) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        PageRequest pageRequest = new PageRequest(page - 1, size);
        Page<User> userPage = relationshipService.listFans(user.getId(), pageRequest);
        List<Integer> friendIds = relationshipService.listFriends(user.getId());
        List<User> userList = userPage.getContent();
        for (int i = 0; i < userList.size(); i++) {
            if (friendIds.contains(userList.get(i).getId())) {
                userPage.getContent().get(i).setIsFriend(2);
            }
        }
        model.addAttribute("page", userPage);
        model.addAttribute("dataType", "fans");
        model.addAttribute("site_title", SiteTitleEnum.RELATIONSHIP_MANAGE.getTitle());
        return new ModelAndView(async == true ? "home/userspace/relationship :: #right-box-body-replace" : "home/userspace/relationship");

    }


    /**
     * 关注或取消关注
     *
     * @param userId
     * @param optType
     * @return
     */
    @PostMapping("/manage/relationships")
    public ResponseEntity<Response> followUser(Integer userId, String optType) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        //1、判断用户是否存在
        User temp = null;
        temp = userRepository.findOne(userId);
        if (temp == null) {
            return ResponseEntity.ok().body(new Response(false, "用户不存在"));
        }
        //2、判断是关注还是取消关注
        //关注
        if ("follow".equals(optType)) {
            relationshipService.saveRelationship(new Relationship(user.getId(), userId));
        } else if ("notfollow".equals(optType)) {
            //取消关注
            relationshipService.removeRelationship(new Relationship(user.getId(), userId));
        } else {
            //非法操作
            return ResponseEntity.ok().body(new Response(false, "非法操作"));
        }
        Long fanSize = userRepository.findOne(userId).getFanSize();
        return ResponseEntity.ok().body(new Response(true, "操作成功", fanSize));
    }




}