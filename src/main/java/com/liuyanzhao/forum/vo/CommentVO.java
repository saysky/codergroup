package com.liuyanzhao.forum.vo;

import com.liuyanzhao.forum.entity.User;
import lombok.Data;


/**
 * @author 言曌
 * @date 2018/4/20 上午8:06
 */

@Data
public class CommentVO {

    private Long id;

    private Long pid;

    private String content;

    private User user;

    private String createTime;

    private Integer floor;


}
