package com.liuyanzhao.forum.service.impl;

import com.liuyanzhao.forum.entity.MailRetrieve;
import com.liuyanzhao.forum.repository.MailRetrieveRepository;
import com.liuyanzhao.forum.service.MailRetrieveService;
import com.liuyanzhao.forum.util.MD5Util;
import com.liuyanzhao.forum.util.RandomUtil;
import com.liuyanzhao.forum.vo.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author 言曌
 * @date 2018/2/23 上午10:30
 */

@Service
public class MailRetrieveServiceImpl implements MailRetrieveService {


    @Autowired
    private MailRetrieveRepository mailRetrieveRepository;

    /**
     * 激活邮箱验证
     *
     * @param basePath
     * @param account
     * @return
     */
    @Override
    public String getEmailUrl(String basePath, String account) {
        //生成邮件URL唯一地址
        String key = RandomUtil.getRandom(6) + "";
        long outtimes = System.currentTimeMillis() + 30 * 60 * 1000;
        String sid = account + "&" + key + "&" + outtimes;
        MailRetrieve mailRetrieve = new MailRetrieve(account, MD5Util.encode(sid), outtimes);
        MailRetrieve findMailRetrieve = mailRetrieveRepository.findByAccount(account);
        if (findMailRetrieve != null) {
            mailRetrieveRepository.delete(findMailRetrieve);
        }
        try {
            mailRetrieveRepository.save(mailRetrieve);
        }catch (Exception e) {
            e.printStackTrace();
        }
        String url = basePath + "?sid=" + MD5Util.encode(sid) + "&username=" + account;
        return url;
    }

    @Override
    public MailRetrieve getMailRetrieveByAccount(String account) {
        return mailRetrieveRepository.findByAccount(account);
    }

    /**
     * 验证链接
     *
     * @param sid
     * @param username
     * @return
     */
    @Override
    public ResultVO verifyMailUrl(String sid, String username) {
        ResultVO resultVO = new ResultVO();
        MailRetrieve mailRetrieve = mailRetrieveRepository.findByAccount(username);
        if (mailRetrieve != null) {
            long outTime = mailRetrieve.getOutTime();
            long nowTime = System.currentTimeMillis();
            if (outTime <= nowTime) {
                resultVO.setCode(1);
                resultVO.setMsg("邮件已经过期！");
            } else if ("".equals(sid)) {
                resultVO.setCode(1);
                resultVO.setMsg("sid不完整！");
            } else if (!sid.equals(mailRetrieve.getSid())) {
                resultVO.setCode(1);
                resultVO.setMsg("sid错误！");
            } else {
                resultVO.setCode(0);
                resultVO.setMsg("验证成功！");
            }
        } else {
            //account 对应的用户不存在
            resultVO.setCode(1);
            resultVO.setMsg("链接无效！");

        }
        return resultVO;
    }

    @Override
    public void removeMailRetrieve(Long id) {
        mailRetrieveRepository.delete(id);
    }
}
