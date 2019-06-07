package com.liuyanzhao.forum.service.impl;

import com.liuyanzhao.forum.entity.Authority;
import com.liuyanzhao.forum.entity.Bind;
import com.liuyanzhao.forum.entity.BindType;
import com.liuyanzhao.forum.entity.User;
import com.liuyanzhao.forum.enums.UserStatusEnum;
import com.liuyanzhao.forum.exception.CustomException;
import com.liuyanzhao.forum.repository.*;
import com.liuyanzhao.forum.service.MessageService;
import com.liuyanzhao.forum.service.NoticeService;
import com.liuyanzhao.forum.service.UserService;
import com.liuyanzhao.forum.util.BeanUtils;
import com.liuyanzhao.forum.vo.UserSessionVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;


/**
 * @author 言曌
 * @date 2018/3/20 下午5:32
 */

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BindRepository bindRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageService messageService;

    @Autowired
    private NoticeService noticeService;


    @Transactional
    @Override
    public User saveUser(User user) {

        if (user.getId() != null) {
            //更新用户
            User originalUser = getUserById(user.getId());
            //这个地方是个坑，因为 getAuthorities 得到的是角色名称，不是角色对象
            BeanUtils.copyProperties(user, originalUser);
            return userRepository.save(originalUser);
        } else {
            //创建用户
            return userRepository.save(user);
        }
    }

    @Async //走异步
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void removeUser(Integer id) {

        User user = userRepository.findOne(id);
        if (user != null) {
            //删除该用户的文章
            articleRepository.deleteByUser(user);

            //删除用户评论
            commentRepository.deleteByUser(user);
            commentRepository.deleteByReplyUser(user);

            //删除问题
            questionRepository.deleteByUser(user);

            //删除回答
            answerRepository.deleteByUser(user);

            //删除绑定
            bindRepository.deleteByUser(user);

            //删除分类
            categoryRepository.deleteByUser(user);

            //...其他的可以不删除

            //删除用户【逻辑删除】
            user.setStatus("deleted");
            user.setUsername(user.getUsername()+"-deleted");
            userRepository.save(user);
        }

    }


    @Override
    public User getUserById(Integer id) {
        return userRepository.findOne(id);
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Page<User> listUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public Page<User> listUsersByNicknameLike(String nickname, Pageable pageable) {
        // 模糊查询
        nickname = "%" + nickname + "%";
        Page<User> users = userRepository.findByNicknameLike(nickname, pageable);
        return users;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        //判断用户状态是否可以登录
        if (user != null) {
            //正常
            if (UserStatusEnum.NORMAL.getCode().equals(user.getStatus())) {
                return user;
            } else if (UserStatusEnum.DELETED.getCode().equals(user.getStatus())) {
                throw new CustomException("该用户已被删除");
            } else {
                throw new CustomException("该用户禁止登录");
            }
        }
        return null;
    }

    @Override
    public List<User> listUsersByUsername(Collection<String> username) {
        return userRepository.findByUsernameIn(username);
    }

    @Override
    public UserSessionVO getUserSessionVO(User user) {
        UserSessionVO userVO = new UserSessionVO();
        BeanUtils.copyProperties(user, userVO);
        try {
            userVO.setMessageSize(messageService.countNotReadMessageSize(user));
            userVO.setNoticeSize(noticeService.countNotReadNotices(user));
        } catch (Exception e) {
            logger.error("redis服务故障", e);
        }
        return userVO;
    }


    @Override
    public User getUserByCondition(BindType bindType, String identifier) {
        Bind bind = bindRepository.findByBindTypeAndIdentifier(bindType, identifier);
        if (bind == null) {
            return null;
        } else {
            return bind.getUser();
        }
    }

    @Override
    public Page<User> listUsersByRoleOrKeywords(List<Authority> authorityList, String keywords, Pageable pageable) {
        return userRepository.findByAuthoritiesContainsAndUsernameLikeOrAuthoritiesContainsAndNicknameLike(authorityList, "%" + keywords + "%", authorityList, "%" + keywords + "%", pageable);
    }

    @Override
    public Integer countUserByAuthority(List<Authority> authorityList) {
        return userRepository.countByAuthoritiesContains(authorityList);
    }

    @Override
    public Long countUser() {
        return userRepository.count();
    }

}
