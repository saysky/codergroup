package com.liuyanzhao.forum.converter;

import com.liuyanzhao.forum.entity.Message;
import com.liuyanzhao.forum.vo.MessageVO;

/**
 * @author 言曌
 * @date 2018/5/1 上午11:41
 */

public class Message2MessageVOConverter {

    public static MessageVO convert(Message message) {
        MessageVO messageVO = new MessageVO(message.getId(), message.getContent(), message.getFriend().getId(), message.getFriend().getNickname(), message.getFriend().getAvatar(), message.getEasyCreateTime());
        return messageVO;
    }
}
