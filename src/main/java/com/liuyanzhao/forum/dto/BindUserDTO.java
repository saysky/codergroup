package com.liuyanzhao.forum.dto;

import com.liuyanzhao.forum.entity.BindType;
import lombok.Data;

/**
 * @author 言曌
 * @date 2018/5/14 上午6:56
 */
@Data
public class BindUserDTO {

    private String openId;

    private String nickname;

    private String gender;

    private String avatar;

    private BindType bindType;



}
