package com.liuyanzhao.forum.vo;

import lombok.Data;

/**
 * @author 言曌
 * @date 2018/5/24 上午11:34
 */

@Data
public class UserRankVO {

    private Integer id;

    private String nickname;

    private String username;

    private String avatar;

    private Long viewSize;

    private Long articleSize;

    private Long answerSize;

    private Long questionSize;

    private Integer reputation;


}
