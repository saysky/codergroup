package com.liuyanzhao.forum.service;

import com.liuyanzhao.forum.entity.Bind;
import com.liuyanzhao.forum.entity.BindType;
import com.liuyanzhao.forum.entity.User;
import com.liuyanzhao.forum.vo.UserSessionVO;

import java.util.List;

/**
 * @author 言曌
 * @date 2018/5/13 下午11:22
 */

public interface BindService {

    /**
     * 根据id获得绑定
     * @param id
     * @return
     */
    Bind getBindById(Long id);

    /**
     * 添加绑定
     *
     * @param bind
     */
    void saveBind(Bind bind);

    /**
     * 解除绑定
     *
     * @param bind
     */
    void removeBind(Bind bind);


    /**
     * 根据凭据获得用户sessionVO
     *
     * @param identifier
     * @return
     */
    UserSessionVO getUserSessionVO(BindType bindType, String identifier);

    /**
     * 获得绑定列表
     * @param user
     * @return
     */
    List<Bind> listBinds(User user);


}
