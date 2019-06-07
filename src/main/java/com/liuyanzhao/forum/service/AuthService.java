package com.liuyanzhao.forum.service;

import com.liuyanzhao.forum.dto.BindUserDTO;

import java.io.UnsupportedEncodingException;

/**
 * @author 言曌
 * @date 2018/5/9 下午3:00
 */

public interface AuthService {

    String getAccessToken(String code);

    String getOpenId(String accessToken);

    String refreshToken(String code);

    String getAuthorizationUrl() throws UnsupportedEncodingException;

    BindUserDTO getUserInfo(String accessToken, String openId);
}
