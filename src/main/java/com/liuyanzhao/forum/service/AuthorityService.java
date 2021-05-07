package com.liuyanzhao.forum.service;

import com.liuyanzhao.forum.entity.Authority;

/**
 * @author 言曌
 * @date 2018/3/21 下午8:19
 */

public interface AuthorityService {


    /**
     * 根据id获取 Authority
     * @param id
     * @return
     */
    Authority getAuthorityById(Integer id);
}
