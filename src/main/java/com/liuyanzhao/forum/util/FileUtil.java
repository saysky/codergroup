package com.liuyanzhao.forum.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.StrBuilder;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件上传根据类
 *
 * @author 言曌
 * @date 2020/3/8 5:45 下午
 */
public class FileUtil {


    /**
     * 文件上传
     *
     * @param file
     * @return
     */
    public static String upload(MultipartFile file) throws Exception {
        try {
            //用户目录
            final StrBuilder uploadPath = new StrBuilder(System.getProperties().getProperty("user.home"));
            uploadPath.append("/sens/upload/" + DateUtil.thisYear()).append("/").append(DateUtil.thisMonth() + 1).append("/");
            final File mediaPath = new File(uploadPath.toString());
            if (!mediaPath.exists()) {
                if (!mediaPath.mkdirs()) {
                    throw new Exception("文件上传失败，无法创建文件夹");
                }
            }
            // 原始文件名
            String originFileName = file.getOriginalFilename();
            // 后缀
            final String fileSuffix = originFileName.substring(file.getOriginalFilename().lastIndexOf('.') + 1);
            // 新文件名
            String nameWithOutSuffix = UUID.randomUUID().toString().replaceAll("-", "");

            //带后缀
            String newFileName = nameWithOutSuffix + "." + fileSuffix;

            // 判断文件名是否已存在
            File descFile = new File(mediaPath.getAbsoluteFile(), newFileName);
            file.transferTo(descFile);

            //映射路径
            final StrBuilder filePath = new StrBuilder("/upload/");
            filePath.append(DateUtil.thisYear());
            filePath.append("/");
            filePath.append(DateUtil.thisMonth() + 1);
            filePath.append("/");
            filePath.append(nameWithOutSuffix + "." + fileSuffix);

            return filePath.toString();
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception("文件上传失败");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
