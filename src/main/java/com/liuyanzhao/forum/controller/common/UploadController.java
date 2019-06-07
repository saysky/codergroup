package com.liuyanzhao.forum.controller.common;

import com.google.gson.Gson;
import com.liuyanzhao.forum.entity.User;
import com.liuyanzhao.forum.properties.QiNiuProperties;
import com.liuyanzhao.forum.service.QiNiuService;
import com.qiniu.common.QiniuException;
import com.qiniu.storage.model.DefaultPutRet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 言曌
 * @date 2018/4/13 下午9:30
 */

@Controller
public class UploadController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private QiNiuService qiNiuService;

    @Autowired
    private QiNiuProperties qiNiuProperties;

    @PostMapping("/upload")
    @ResponseBody
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")  // 指定角色权限才能操作方法
    public Map<String, String> uploadToQiNiu(@RequestParam(value = "upload_file", required = false) MultipartFile file) throws IOException {
            Map<String, String> map = new HashMap<String, String>();
        try {

            //1、后缀过滤，只支持 jpg,jpeg,png,bmp,gif
            //浏览器不兼容，safari是blob
//            String filename = file.getOriginalFilename();
//            String suffix = (filename.substring(filename.lastIndexOf("."))).toLowerCase();
//            if (!".jpg".equals(suffix) && !".png".equals(suffix) && !".jpeg".equals(suffix) && !".gif".equals(suffix) && !".bmp".equals(suffix)) {
//                map.put("success", "false");
//                map.put("msg", "文件格式不允许");
//                return map;
//            }

            //2、文件大小过滤
            if (file.getSize() > 2 * 1024 * 1024) {
                map.put("success", "false");
                map.put("msg", "文件太大，请选择小于2MB的图片");
                return map;
            }

            String url = "";
            //3、开始上传
            InputStream inputStream = file.getInputStream();
            try {
                com.qiniu.http.Response response = qiNiuService.uploadFile(inputStream);
                DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
                url = qiNiuProperties.getCdnPrefix() + putRet.key;
            } catch (QiniuException e) {
                logger.error("七牛云接口错误");
                map.put("success", "false");
                map.put("msg", "七牛云接口错误");
                return map;
            }

            map.put("success", "true");
            map.put("msg", "成功");
            //返回服务器地址
            map.put("file_path", url);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;


    }


}
