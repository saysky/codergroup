package com.liuyanzhao.forum.service;

import com.liuyanzhao.forum.entity.Authority;
import com.liuyanzhao.forum.entity.BindType;
import com.liuyanzhao.forum.entity.User;
import com.liuyanzhao.forum.vo.UserSessionVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;

/**
 * @author 言曌
 * @date 2018/3/20 下午5:32
 */

public interface UserService {

    /**
     * 添加用户/更新用户
     *
     * @param user
     * @return
     */
    User saveUser(User user);

    /**
     * 删除用户
     *
     * @param id
     * @return
     */
    void removeUser(Integer id);

    /**
     * 根据id获取用户
     *
     * @param id
     * @return
     */
    User getUserById(Integer id);

    /**
     * 根据用户名得到用户
     *
     * @param username
     * @return
     */
    User getUserByUsername(String username);

    /**
     * 根据Email得到用户
     *
     * @param email
     * @return
     */
    User getUserByEmail(String email);

    /**
     * 获取用户列表
     *
     * @return
     */
    Page<User> listUsers(Pageable pageable);

    /**
     * 根据用户名进行分页模糊查询
     *
     * @param nickname
     * @param pageable
     * @return
     */
    Page<User> listUsersByNicknameLike(String nickname, Pageable pageable);

    /**
     * 更具名称列表查询
     *
     * @param username
     * @return
     */
    List<User> listUsersByUsername(Collection<String> username);

    /**
     * 将User转成UserSessionVO
     *
     * @param user
     * @return
     */
    UserSessionVO getUserSessionVO(User user);



    /**
     * 根据凭证查询
     *
     * @param credential
     * @return
     */
    User getUserByCondition(BindType bindType, String credential);

    /**
     * 根据用户名和昵称模糊查询，状态，权限查询
     * @param authorityList
     * @param keywords
     * @param pageable
     * @return
     */
    Page<User> listUsersByRoleOrKeywords(List<Authority> authorityList, String keywords, Pageable pageable);


    /**
     * 根据权限查询用户
     * @param authorityList
     * @return
     */
    Integer countUserByAuthority(List<Authority> authorityList);

    /**
     * 统计用户总数
     * @return
     */
    Long countUser();

}