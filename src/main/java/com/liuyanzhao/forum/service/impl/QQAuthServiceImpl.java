package com.liuyanzhao.forum.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.liuyanzhao.forum.service.QQAuthService;
import com.liuyanzhao.forum.dto.BindUserDTO;
import org.hibernate.service.spi.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 言曌
 * @date 2018/5/9 下午3:15
 */

@Service
public class QQAuthServiceImpl extends DefaultAuthServiceImpl implements QQAuthService {

    private Logger logger = LoggerFactory.getLogger(QQAuthServiceImpl.class);
    //QQ 登陆页面的URL
    private final static String AUTHORIZATION_URL =
            "https://graph.qq.com/oauth2.0/authorize?response_type=code&client_id=%s&redirect_uri=%s&scope=%s";
    //获取token的URL
    private final static String ACCESS_TOKEN_URL = "https://graph.qq.com/oauth2.0/token?grant_type=authorization_code&client_id=%s&client_secret=%s&code=%s&redirect_uri=%s";

    // 获取用户 openid 的 URL
    private static final String OPEN_ID_URL = "https://graph.qq.com/oauth2.0/me?access_token=%s";

    // 获取用户信息的 URL，oauth_consumer_key 为 apiKey
    private static final String USER_INFO_URL = "https://graph.qq.com/user/get_user_info?access_token=%s&oauth_consumer_key=%s&openid=%s";

    // 下面的属性可以通过配置读取
    private static final String CALLBACK_URL = "http://codergroup.cn/oauth/qq/callback"; // QQ 在登陆成功后回调的 URL，这个 URL 必须在 QQ 互联里填写过
    private static final String API_KEY = "XXXXXXXXXXXXXXXXXX"; // QQ 互联应用管理中心的 APP ID
    private static final String API_SECRET = "XXXXXXXXXXXXXXXXXX"; // QQ 互联应用管理中心的 APP Key
    private static final String SCOPE = "get_user_info"; // QQ 互联的 API 接口，访问用户资料

    /**
     * @return QQ 登陆页面的 URL
     */
    @Override
    public String getAuthorizationUrl() {
        String url = String.format(AUTHORIZATION_URL, API_KEY, CALLBACK_URL, SCOPE);
        return url;
    }

    @Override
    public String getAccessToken(String code) {
        String url = String.format(ACCESS_TOKEN_URL, API_KEY, API_SECRET, code, CALLBACK_URL);
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

    //由于QQ的几个接口返回类型不一样，此处是获取key-value类型的参数
    private Map<String, String> getParam(String string) {
        Map<String, String> map = new HashMap();
        String[] kvArray = string.split("&");
        for (int i = 0; i < kvArray.length; i++) {
            String[] kv = kvArray[i].split("=");
            map.put(kv[0], kv[1]);
        }
        return map;
    }

    //QQ接口返回类型是text/plain，此处将其转为json
    public JSONObject ConvertToJson(String string) {
        string = string.substring(string.indexOf("(") + 1, string.length());
        string = string.substring(0, string.indexOf(")"));
        JSONObject jsonObject = JSONObject.parseObject(string);
        return jsonObject;
    }

    @Override
    public String getOpenId(String accessToken) {
        String url = String.format(OPEN_ID_URL, accessToken);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        URI uri = builder.build().encode().toUri();

        String resp = getRestTemplate().getForObject(uri, String.class);
        if (resp.contains("openid")) {
            JSONObject jsonObject = ConvertToJson(resp);
            String openid = jsonObject.getString("openid");
            return openid;
        } else {
            throw new ServiceException(resp);
        }
    }

    /**
     * https://graph.qq.com/user/get_user_info?access_token=YOUR_ACCESS_TOKEN&oauth_consumer_key=YOUR_APP_ID&openid=YOUR_OPENID
     *
     * @param accessToken
     * @param openId
     * @return
     */
    @Override
    public BindUserDTO getUserInfo(String accessToken, String openId) {

        openId = getOpenId(accessToken);
        String url = String.format(USER_INFO_URL, accessToken, API_KEY, openId);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        URI uri = builder.build().encode().toUri();

        String resp = getRestTemplate().getForObject(uri, String.class);
        JSONObject data = JSONObject.parseObject(resp);
        BindUserDTO result = new BindUserDTO();
        result.setOpenId(openId);
        result.setGender(data.getString("gender"));
        result.setAvatar(data.getString("figureurl_qq_2"));
        result.setNickname(data.getString("nickname"));
        return result;
    }

    @Override
    public String refreshToken(String code) {
        return null;
    }
}
