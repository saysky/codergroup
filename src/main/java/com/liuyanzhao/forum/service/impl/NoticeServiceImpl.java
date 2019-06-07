package com.liuyanzhao.forum.service.impl;

import com.liuyanzhao.forum.entity.Notice;
import com.liuyanzhao.forum.entity.NoticeType;
import com.liuyanzhao.forum.entity.User;
import com.liuyanzhao.forum.enums.NoticeStatusEnum;
import com.liuyanzhao.forum.repository.NoticeRepository;
import com.liuyanzhao.forum.repository.NoticeTypeRepository;
import com.liuyanzhao.forum.repository.UserRepository;
import com.liuyanzhao.forum.service.NoticeService;
import com.liuyanzhao.forum.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author 言曌
 * @date 2018/5/31 下午9:39
 */

@Service
public class NoticeServiceImpl implements NoticeService {

    @Autowired
    private NoticeRepository noticeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NoticeTypeRepository noticeTypeRepository;


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void clearNoticesByUser(User toUser) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        user = userRepository.findByUsername(user.getUsername());
        List<Notice> noticeList = noticeRepository.findByToUser(user);
        noticeRepository.delete(noticeList);
    }


    @Override
    public void createNotice(Notice notice) {
        noticeRepository.save(notice);
    }

    @Override
    public void markreadNoticesByUser(User user) {
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Notice> noticeList = noticeRepository.findByToUserAndStatus(principal, NoticeStatusEnum.NOT_READ.getCode());
        for (int i = 0; i < noticeList.size(); i++) {
            noticeList.get(i).setStatus(NoticeStatusEnum.HAD_READ.getCode());
        }
        noticeRepository.save(noticeList);
    }

    @Override
    public Page<Notice> listNoticesByUser(User user, Pageable pageable) {
        return noticeRepository.findByToUser(user, pageable);
    }

    @Override
    public List<NoticeType> mergeAndListNewNotices(User user) {
        //根据NoticeType分类合并通知数量
        List<Integer> countList = noticeRepository.getCountListByNotRead(user.getId());
        List<Integer> typeIds = noticeRepository.getTypeIdsByNotRead(user.getId());
        List<NoticeType> noticeTypeList = noticeTypeRepository.findByIdInOrderById(typeIds);
        for (int i = 0; i < countList.size(); i++) {
            noticeTypeList.get(i).setCount(Integer.valueOf(countList.get(i) + ""));
        }
        return noticeTypeList;
    }

    @Override
    @Cacheable(value = "10m", key = "'noticeSize:'+#p0.id")
    public Integer countNotReadNotices(User user) {
        return noticeRepository.countByToUserAndStatus(user, NoticeStatusEnum.NOT_READ.getCode());
    }
}
