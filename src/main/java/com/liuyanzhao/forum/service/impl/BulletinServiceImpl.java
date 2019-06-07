package com.liuyanzhao.forum.service.impl;

import com.liuyanzhao.forum.entity.Bulletin;
import com.liuyanzhao.forum.enums.PostStatusEnum;
import com.liuyanzhao.forum.repository.BulletinRepository;
import com.liuyanzhao.forum.service.BulletinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author 言曌
 * @date 2018/6/10 上午11:19
 */
@Service
public class BulletinServiceImpl implements BulletinService {

    @Autowired
    private BulletinRepository bulletinRepository;


    @Transactional
    @Override
    public Bulletin savebulletin(Bulletin bulletin) {
        bulletin.setGuid("/bulletins/" + bulletin.getName());
        return bulletinRepository.save(bulletin);
    }

    @Transactional
    @Override
    public void removebulletin(Long id) {
        bulletinRepository.delete(id);
    }

    @Override
    public Bulletin getbulletinById(Long id) {
        return bulletinRepository.findOne(id);
    }

    @Override
    public Bulletin getBulletinByName(String name) {
        return bulletinRepository.findByName(name);
    }

    @Override
    public Page<Bulletin> listPublishbulletin(Pageable pageable) {

        return bulletinRepository.findByStatus(PostStatusEnum.PUBLISH_POST.getCode(), pageable);
    }

    @Override
    public Page<Bulletin> listAllbulletin(Pageable pageable) {
        return bulletinRepository.findAll(pageable);
    }

    @Override
    public Page<Bulletin> listBulletinByStatus(String status, Pageable pageable) {
        return bulletinRepository.findByStatus(status, pageable);
    }


}
