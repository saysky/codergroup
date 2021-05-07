package com.liuyanzhao.forum.service;

import com.liuyanzhao.forum.entity.Bulletin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author 言曌
 * @date 2018/6/10 上午11:15
 */

public interface BulletinService {

    /**
     * 添加bulletin
     *
     * @param bulletin
     * @return
     */
    Bulletin savebulletin(Bulletin bulletin);

    /**
     * 删除bulletin
     *
     * @param id
     */
    void removebulletin(Long id);

    /**
     * 根据ID获得公告
     *
     * @param id
     * @return
     */
    Bulletin getbulletinById(Long id);

    /**
     * 根据key获取公告
     * @param key
     * @return
     */
    Bulletin getBulletinByName(String name);

    /**
     * 根据状态获取公告
     *
     * @param pageable
     * @return
     */
    Page<Bulletin> listPublishbulletin(Pageable pageable);

    /**
     * 获得所有的公告
     *
     * @param pageable
     * @return
     */
    Page<Bulletin> listAllbulletin(Pageable pageable);

    Page<Bulletin> listBulletinByStatus(String status, Pageable pageable);

}
