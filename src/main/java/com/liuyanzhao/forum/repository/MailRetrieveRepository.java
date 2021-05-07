package com.liuyanzhao.forum.repository;

import com.liuyanzhao.forum.entity.MailRetrieve;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author 言曌
 * @date 2018/2/23 上午10:40
 */

public interface MailRetrieveRepository extends JpaRepository<MailRetrieve, Long> {

    /**
     * 根据账号查找记录
     *
     * @param account
     * @return
     */
    MailRetrieve findByAccount(String account);
}
