package com.liuyanzhao.forum.service.impl;

import com.liuyanzhao.forum.entity.Zan;
import com.liuyanzhao.forum.repository.ZanRepository;
import com.liuyanzhao.forum.service.ZanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;


/**
 * @author 言曌
 * @date 2018/5/2 下午11:42
 */

@Service
public class ZanServiceImpl implements ZanService {

    @Autowired
    private ZanRepository zanRepository;

    @Override
    @Transactional
    public void removeZan(Long id) {
        zanRepository.delete(id);
    }

    @Override
    public Zan getZanById(Long id) {
        return zanRepository.findOne(id);
    }

}
