package com.liuyanzhao.forum.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.liuyanzhao.forum.service.GithubAuthService;
import com.liuyanzhao.forum.dto.BindUserDTO;
import org.hibernate.service.spi.ServiceException;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 言曌
 * @date 2018/5/15 下午11:31
 */
@Service
public class GithubAuthServiceImpl extends DefaultAuthServiceImpl implements GithubAuthService {

    private static final String AUTHORIZE_URL = "https://github.com/login/oauth/authorize?client_id=%s&redirect_uri=%s&state=%s";
    private static final String ACCESS_TOKEN_URL = "https://github.com/login/oauth/access_token?client_id=%s&client_secret=%s&code=%s&redirect_uri=%s&state=%s";
    private static final String USER_INFO_URL = "https://api.github.com/user?access_token=%s";


    // 下面的属性可以通过配置读取
    private static final String CALLBACK_URL = "http://codergroup.cn/oauth/github/callback";//回调地址
    private static final String API_KEY = "96bb0592e6b3b8f2fb90";//Client ID
    private static final String API_SECRET = "46fa699ae82773377548321b1124cc3e6a0f7ca8";//Client Secret

    private static final String GITHUB_STATE = "LiuYanzhaoLoveQiQiNeverGiveUp";//state，随便填，但要一致

    //此处是获取key-value类型的参数
    private Map<String, String> getParam(String string) {
        Map<String, String> map = new HashMap();
        String[] kvArray = string.split("&");
        for (int i = 0; i < kvArray.length; i++) {
            String[] kv = kvArray[i].split("=");
            if(kv.length == 2) {
                map.put(kv[0], kv[1]);
            } else if(kv.length == 1) {
                map.put(kv[0],"");
            }
        }
        return map;
    }

    @Override
    public String getAccessToken(String code) {
        String url = String.format(ACCESS_TOKEN_URL, API_KEY, API_SECRET, code, CALLBACK_URL,GITHUB_STATE);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        URI uri = builder.build().encode().toUri();

        String resp = getRestTemplate().getForObject(uri, String.class);
        if (resp.contains("access_token")) {
            Map<String, String> map = getParam(resp);
            String access_token = map.get("access_token");
            return access_token;
        } else {
            throw new ServiceException(resp);
        }
    }

    @Override
    public String getOpenId(String accessToken) {
        return null;
    }

    @Override
    public String refreshToken(String code) {
        return null;
    }

    @Override
    public String getAuthorizationUrl() throws UnsupportedEncodingException {
        return String.format(AUTHORIZE_URL,API_KEY,CALLBACK_URL,GITHUB_STATE);
    }

    @Override
    public BindUserDTO getUserInfo(String accessToken, String openId) {

        String url = String.format(USER_INFO_URL, accessToken);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        URI uri = builder.build().encode().toUri();

        String resp = getRestTemplate().getForObject(uri, String.class);
        JSONObject data = JSONObject.parseObject(resp);
        BindUserDTO result = new BindUserDTO();
        result.setOpenId(data.getString("id"));
        result.setAvatar(data.getString("avatar_url"));
        result.setNickname(data.getString("name"));
        return result;
    }
}
