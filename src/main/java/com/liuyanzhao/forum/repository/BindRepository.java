package com.liuyanzhao.forum.repository;

import com.liuyanzhao.forum.entity.Bind;
import com.liuyanzhao.forum.entity.BindType;
import com.liuyanzhao.forum.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author 言曌
 * @date 2018/5/13 下午11:21
 */

public interface BindRepository extends JpaRepository<Bind, Long> {

    /**
     * 根据绑定类型和识别码来确定绑定
     * @param bindType
     * @param identifier
     * @return
     */
    Bind findByBindTypeAndIdentifier(BindType bindType, String identifier);

    /**
     * 根据用户获得绑定列表
     * @param user
     * @return
     */
    List<Bind> findByUser(User user);


    Integer deleteByUser(User user);
}
