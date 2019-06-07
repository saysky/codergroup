package com.liuyanzhao.forum.service;

import com.liuyanzhao.forum.entity.Zan;

/**
 * @author 言曌
 * @date 2018/5/2 下午11:41
 */

public interface ZanService {
    /**
     * 根据id获取 Zan
     * @param id
     * @return
     */
    Zan getZanById(Long id);
    /**
     * 删除Zan
     * @param id
     * @return
     */
    void removeZan(Long id);
}
