package com.liuyanzhao.forum.controller;

import com.liuyanzhao.forum.controller.common.BaseController;
import com.liuyanzhao.forum.converter.Message2MessageVOConverter;
import com.liuyanzhao.forum.entity.Message;
import com.liuyanzhao.forum.entity.User;
import com.liuyanzhao.forum.enums.MessageStatusEnum;
import com.liuyanzhao.forum.enums.SiteTitleEnum;
import com.liuyanzhao.forum.exception.CustomException;
import com.liuyanzhao.forum.exception.NotFoundException;
import com.liuyanzhao.forum.service.MessageService;
import com.liuyanzhao.forum.service.UserService;
import com.liuyanzhao.forum.util.ConstraintViolationExceptionHandler;
import com.liuyanzhao.forum.util.RedisOperator;
import com.liuyanzhao.forum.vo.MessageVO;
import com.liuyanzhao.forum.vo.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author 言曌
 * @date 2018/5/1 下午6:10
 */

@Controller
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")  // 指定角色权限才能操作方法
public class MessageController extends BaseController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @Autowired
    private RedisOperator redis;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    /**
     * 返回未阅读的私信
     *
     * @return
     */
    @GetMapping("/messages/recentNotRead")
    public ResponseEntity<Response> getNotReadMessageList() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<MessageVO> newMessageList = new ArrayList<>();
        try {
            List<Message> messageList = messageService.listNotReadMessage(user.getId(), MessageStatusEnum.NOT_READ_MESSAGE.getCode());
            List<Integer> countList = messageService.listCountNotReadMessageSize(user.getId(), MessageStatusEnum.NOT_READ_MESSAGE.getCode());
            for (int i = 0; i < messageList.size(); i++) {
                MessageVO messageVO = Message2MessageVOConverter.convert(messageList.get(i));
                String count = String.valueOf(countList.get(i));
                messageVO.setCount(Integer.valueOf(count));
                newMessageList.add(messageVO);
            }
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.ok().body(new Response(true, "操作成功", newMessageList));

    }


//    用户中心

    /**
     * 打开私信首页
     *
     * @param uid
     * @param model
     * @return
     */
    @GetMapping("/manage/messages")
    public ModelAndView messages(
            @RequestParam(value = "async", defaultValue = "false", required = false) boolean async,
            @RequestParam(value = "uid", required = false) Integer uid,
            Model model) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        //1、左侧的聊天列表
        List<Message> chatList = messageService.getChatList(user.getId());

        List<Integer> statusList = new ArrayList<>();
        statusList.add(MessageStatusEnum.HAD_READ_MESSAGE.getCode());
        statusList.add(MessageStatusEnum.NOT_READ_MESSAGE.getCode());

        //有指定的用户，则默认打开与指定的用户的聊天框
        if (uid != null) {
            //1、判断用户是否存在
            User friend = null;
            try {
                friend = userService.getUserById(uid);
            } catch (Exception e) {
                throw new NotFoundException("用户不存在！");
            }
            if (Objects.equals(friend.getId(), user.getId())) {
                throw new CustomException("不可以和自己聊天哦！");
            }

            //2、判断是否首次聊天
            Message message = messageService.getTopMessage(user, friend, statusList);
            if (message == null) {
                message = new Message(user, friend, null, null, "  ", MessageStatusEnum.HAD_READ_MESSAGE.getCode());
                //将聊天移到顶部
                chatList.add(0, message);
            }


            List<Message> messageList = messageService.getMessageList(user, friend, statusList);
            Integer notReadSize = 0;
            //3、将所有未读状态改为已读
            if (messageList.size() > 0) {
                for (int i = 0; i < messageList.size(); i++) {
                    //未读标记为已读
                    if (messageList.get(i).getStatus().equals(MessageStatusEnum.NOT_READ_MESSAGE.getCode())) {
                        Message temp = messageList.get(i);
                        temp.setStatus(MessageStatusEnum.HAD_READ_MESSAGE.getCode());
                        messageService.saveMessage(temp);
                        notReadSize++;
                    }
                }
            }

            //4、更新缓存redis
            try {
                redis.decr("messageSize:" + user.getId(), notReadSize);
            } catch (Exception e) {
                logger.error("Redis服务故障",e);
            }

            model.addAttribute("messageList", messageList);
            model.addAttribute("friend", friend);

            //5、显示好友列表
            Page<User> userPage = null;
            model.addAttribute("page", userPage);
        }
        model.addAttribute("chatList", chatList);
        model.addAttribute("site_title", SiteTitleEnum.MESSAGE_MANAGE.getTitle());
        return new ModelAndView(async == true ? "home/userspace/message :: #mainContainerReplace" : "home/userspace/message");
    }


    /**
     * 发布私信
     *
     * @param friendId
     * @param content
     * @return
     */
    @PostMapping("/messages")
    public ResponseEntity<Response> createMessage(Integer friendId, String content) {

        //1、判断friendId是否存在
        User friend = null;
        try {
            friend = userService.getUserById(friendId);
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, "用户不存在"));
        }
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();        user = userService.getUserByUsername(user.getUsername());

        //2、添加两条message
        try {
            //自己的一条消息
            Message myMessage = new Message(user, friend, user, friend, content, MessageStatusEnum.HAD_READ_MESSAGE.getCode());
            //对方的一条消息
            Message yourMessage = new Message(friend, user, user, friend, content, MessageStatusEnum.NOT_READ_MESSAGE.getCode());
            messageService.saveMessage(myMessage);
            messageService.saveMessage(yourMessage);

            //3、修改redis缓存
            try {
                redis.incr("messageSize:" + friendId, 1);
            } catch (Exception e){
                logger.error("redis服务故障",e);
            }
        } catch (ConstraintViolationException e) {
            return ResponseEntity.ok().body(new Response(false, ConstraintViolationExceptionHandler.getMessage(e)));
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, e.getMessage()));
        }
        return ResponseEntity.ok().body(new Response(true, "操作成功"));
    }

    /**
     * 更新私信状态为删除
     *
     * @param friendId
     * @return
     */
    @PutMapping("/messages")
    public ResponseEntity<Response> updateMessageStatus(Integer friendId) {
        //将我和该朋友的聊天记录最后一条设置为已读
        //1、判断friendId是否存在
        User friend = null;
        try {
            friend = userService.getUserById(friendId);
        } catch (Exception e) {
            return ResponseEntity.ok().body(new Response(false, "用户不存在"));
        }
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();        user = userService.getUserByUsername(user.getUsername());
        //2、修改状态
        List<Integer> statusList = new ArrayList<>();
        statusList.add(MessageStatusEnum.NOT_READ_MESSAGE.getCode());
        statusList.add(MessageStatusEnum.HAD_READ_MESSAGE.getCode());
        List<Message> messageList = messageService.getMessageList(user, friend, statusList);
        for (int i = 0; i < messageList.size(); i++) {
            Message message = messageList.get(i);
            message.setStatus(MessageStatusEnum.DELETE_MESSAGE.getCode());
            messageService.saveMessage(message);
        }
        return ResponseEntity.ok().body(new Response(true, "操作成功"));
    }


}
