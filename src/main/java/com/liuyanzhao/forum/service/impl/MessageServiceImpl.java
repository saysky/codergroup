package com.liuyanzhao.forum.service.impl;

import com.liuyanzhao.forum.entity.Message;
import com.liuyanzhao.forum.entity.User;
import com.liuyanzhao.forum.enums.MessageStatusEnum;
import com.liuyanzhao.forum.repository.MessageRepository;
import com.liuyanzhao.forum.repository.UserRepository;
import com.liuyanzhao.forum.service.MessageService;
import com.liuyanzhao.forum.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author 言曌
 * @date 2018/4/28 上午9:50
 */

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<Message> getChatList(Integer userId) {
        return messageRepository.getChatList(userId);
    }

    @Override
    public List<Message> getMessageList(User user, User friend, List<Integer> statusList) {
        return messageRepository.findByUserAndFriendAndStatusIn(user, friend, statusList);
    }

    @Override
    public void saveMessage(Message message) {
        messageRepository.save(message);
    }

    @Override
    public void deleteMessage(Long id) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        user = userRepository.findByUsername(user.getUsername());
        Message originalMessage = messageRepository.findOne(id);
        if (Objects.equals(originalMessage.getUser().getId(), user.getId())) {
            originalMessage.setStatus(MessageStatusEnum.DELETE_MESSAGE.getCode());
            messageRepository.save(originalMessage);
        }
    }

    @Override
    public void deleteMessageBatch(List<Long> ids) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        user = userRepository.findByUsername(user.getUsername());
        for (int i = 0; i < ids.size(); i++) {
            Long id = ids.get(i);
            Message originalMessage = messageRepository.findOne(id);
            if (Objects.equals(originalMessage.getUser().getId(), user.getId())) {
                originalMessage.setStatus(MessageStatusEnum.DELETE_MESSAGE.getCode());
                messageRepository.save(originalMessage);
            }
        }
    }

    @Override
    public void removeMessage(Long id) {
        messageRepository.delete(id);
    }


    @Override
    public void removeMessage(List<Long> ids) {
        for (int i = 0; i < ids.size(); i++) {
            messageRepository.delete(ids.get(i));
        }
    }


    @Override
    public Message getTopMessage(User user, User friend, List<Integer> statusList) {
        return messageRepository.findTopByUserAndFriendAndStatusInOrderByIdDesc(user, friend, statusList);
    }


    @Override
    @Cacheable(value = "10m", key = "'messageSize:'+#p0.id")
    public Integer countNotReadMessageSize(User user) {
        return messageRepository.countByUserAndStatus(user, MessageStatusEnum.NOT_READ_MESSAGE.getCode());
    }

    @Override
    public List<Message> listNotReadMessage(Integer userId, Integer status) {
        return messageRepository.findByUserAndStatus(userId, status);
    }


    @Override
    public List<Integer> listCountNotReadMessageSize(Integer userId, Integer status) {
        return messageRepository.listCountNotReadMessageSize(userId, status);
    }

}
