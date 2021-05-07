package com.liuyanzhao.forum.service;

import com.liuyanzhao.forum.entity.MailRetrieve;
import com.liuyanzhao.forum.vo.ResultVO;

/**
 * 邮件找回密码Service
 *
 * @author 言曌
 * @date 2018/2/23 上午10:27
 */

public interface MailRetrieveService {



    /**
     * 生成验证邮件的URL
     * @param basePath
     * @param account
     * @return
     */
    String getEmailUrl(String basePath, String account);

    /**
     * 根据account名称获取记录
     * @param username
     * @return
     */
    MailRetrieve getMailRetrieveByAccount(String username);
    /**
     * 邮件找回密码URL校验
     * @param sid
     * @param account
     * @return
     */
     ResultVO verifyMailUrl(String sid, String account);

    /**
     * 删除邮件
     * @param id
     */
     void removeMailRetrieve(Long id);
}
