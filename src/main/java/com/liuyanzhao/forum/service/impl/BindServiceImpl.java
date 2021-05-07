package com.liuyanzhao.forum.service.impl;

import com.liuyanzhao.forum.entity.Bind;
import com.liuyanzhao.forum.entity.BindType;
import com.liuyanzhao.forum.entity.User;
import com.liuyanzhao.forum.repository.BindRepository;
import com.liuyanzhao.forum.service.BindService;
import com.liuyanzhao.forum.service.UserService;
import com.liuyanzhao.forum.vo.UserSessionVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 言曌
 * @date 2018/5/13 下午11:23
 */

@Service
public class BindServiceImpl implements BindService {

    @Autowired
    private BindRepository bindRepository;

    @Autowired
    private UserService userService;

    @Override
    public Bind getBindById(Long id) {
        return bindRepository.findOne(id);
    }

    @Override
    public void saveBind(Bind bind) {
        Bind temp = bindRepository.findByBindTypeAndIdentifier(bind.getBindType(),bind.getIdentifier());
        if(temp == null) {
            bindRepository.save(bind);
        }
    }

    @Override
    public void removeBind(Bind bind) {
        bindRepository.delete(bind);
    }

    @Override
    public UserSessionVO getUserSessionVO(BindType bindType, String identifier) {
        User user = bindRepository.findByBindTypeAndIdentifier(bindType, identifier).getUser();
        return userService.getUserSessionVO(user);
    }

    @Override
    public List<Bind> listBinds(User user) {
        return bindRepository.findByUser(user);
    }
}
