package com.liuyanzhao.forum.vo;

import lombok.Data;

/**
 * User session
 *
 * @author 言曌
 * @date 2018/4/30 下午8:55
 */

@Data
public class UserSessionVO {

    private Integer id;

    private String nickname;

    private String username;

    private String email;

    private String avatar;

    private Integer messageSize = 0;

    private Integer noticeSize = 0;
}
