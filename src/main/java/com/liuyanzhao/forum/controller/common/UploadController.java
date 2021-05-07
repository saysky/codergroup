package com.liuyanzhao.forum.controller.common;

import com.liuyanzhao.forum.util.FileUtil;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 言曌
 * @date 2018/4/13 下午9:30
 */

@Controller
public class UploadController {


    @PostMapping("/upload")
    @ResponseBody
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")  // 指定角色权限才能操作方法
    public Map<String, String> upload(@RequestParam(value = "upload_file", required = false) MultipartFile file) throws IOException {
        Map<String, String> map = new HashMap<String, String>();
        try {

            if (file.getSize() > 2 * 1024 * 1024) {
                map.put("success", "false");
                map.put("msg", "文件太大，请选择小于2MB的图片");
                return map;
            }

            String url = FileUtil.upload(file);
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
