package com.liuyanzhao.forum.service.impl;

import com.liuyanzhao.forum.entity.Authority;
import com.liuyanzhao.forum.repository.AuthorityRepository;
import com.liuyanzhao.forum.service.AuthorityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 言曌
 * @date 2018/3/21 下午8:20
 */
@Service
public class AuthorityServiceImpl implements AuthorityService {

    @Autowired
    private AuthorityRepository authorityRepository;

    @Override
    public Authority getAuthorityById(Integer id) {
        return authorityRepository.findOne(id);
    }
}
