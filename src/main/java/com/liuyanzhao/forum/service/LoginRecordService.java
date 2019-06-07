package com.liuyanzhao.forum.service;

import com.liuyanzhao.forum.entity.LoginRecord;
import com.liuyanzhao.forum.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author 言曌
 * @date 2018/4/10 下午10:54
 */

public interface LoginRecordService {

    /**
     * 获取所有
     *
     * @return
     */
    List<LoginRecord> listTop50LoginRecords(User user);

    /**
     * 获得用户登录记录
     * @param user
     * @param pageable
     * @return
     */
    Page<LoginRecord> listLoginRecords(User user, Pageable pageable);

    /**
     * 获得所有用户的登录记录
     * @param pageable
     * @return
     */
    Page<LoginRecord> listAllLoginRecords(Pageable pageable);

    /**
     * 添加登录记录
     *
     * @param loginRecord
     * @return
     */
    void saveLoginRecord(LoginRecord loginRecord);

    /**
     * 删除日志
     * @param id
     */
    void deleteLoginRecord(Long id);

}
