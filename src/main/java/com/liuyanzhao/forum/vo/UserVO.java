package com.liuyanzhao.forum.vo;

import lombok.Data;

/**
 * @author 言曌
 * @date 2018/5/2 上午11:10
 */

@Data
public class UserVO {

    private Integer id;//ID

    private String username;//用户名

    private String nickname;//昵称

    private String avatar;//头像


    private Long followSize;//关注数量

    private Long fanSize;//粉丝数量

    private Integer relationship = 0;//关系

    private String bg;//背景
}
