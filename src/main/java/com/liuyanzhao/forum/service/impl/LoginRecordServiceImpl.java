package com.liuyanzhao.forum.service.impl;

import com.liuyanzhao.forum.entity.LoginRecord;
import com.liuyanzhao.forum.entity.User;
import com.liuyanzhao.forum.repository.LoginRecordRepository;
import com.liuyanzhao.forum.repository.UserRepository;
import com.liuyanzhao.forum.service.LoginRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author 言曌
 * @date 2018/4/10 下午10:57
 */

@Service
public class LoginRecordServiceImpl implements LoginRecordService {

    @Autowired
    private LoginRecordRepository loginRecordRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<LoginRecord> listTop50LoginRecords(User user) {
        return loginRecordRepository.findTop50ByUserOrderByIdDesc(user);
    }

    @Override
    public Page<LoginRecord> listLoginRecords(User user, Pageable pageable) {
        return loginRecordRepository.findByUser(user, pageable);
    }

    @Override
    public Page<LoginRecord> listAllLoginRecords(Pageable pageable) {
        return loginRecordRepository.findAll(pageable);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveLoginRecord(LoginRecord loginRecord) {
        //添加登录记录
        LoginRecord returnLoginRecord = loginRecordRepository.save(loginRecord);
        //修改用户最后登录时间
        User user = loginRecord.getUser();
        user.setLastLoginTime(returnLoginRecord.getCreateTime());
        userRepository.save(user);
    }

    @Override
    public void deleteLoginRecord(Long id) {
        loginRecordRepository.delete(id);
    }
}
