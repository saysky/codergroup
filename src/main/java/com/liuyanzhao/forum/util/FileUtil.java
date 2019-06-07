package com.liuyanzhao.forum.util;

import com.liuyanzhao.forum.vo.FileVO;
import com.liuyanzhao.forum.vo.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

/**
 * @author 言曌
 * @date 2018/4/5 下午5:06
 */

public class FileUtil {

    //上传地址
    @Value("upload.root-path")
    private String rootPath;

    @Value("upload.url.prefix")
    private String urlPrefix;


    public Response uploadFile(MultipartFile file) throws IOException {
        //文件的完整名称,如spring.jpeg
        String filename = file.getOriginalFilename();
        //文件名,如spring
        String name = filename.substring(0, filename.indexOf("."));
        //文件后缀,如.jpeg
        String suffix = filename.substring(filename.lastIndexOf("."));
        //创建年月文件夹
        Calendar date = Calendar.getInstance();
        File dateDirs = new File(date.get(Calendar.YEAR)
                + File.separator + (date.get(Calendar.MONTH) + 1));
        //目标文件
        File targetFile = new File(rootPath + File.separator + dateDirs + File.separator + filename);
        int i = 1;
        //若文件存在重命名
        String newFilename = filename;
        while (targetFile.exists()) {
            newFilename = name + "(" + i + ")" + suffix;
            String parentPath = targetFile.getParent();
            targetFile = new File(parentPath + File.separator + dateDirs + File.separator + newFilename);
            i++;
        }
        //判断目标文件所在的目录是否存在
        if (!targetFile.getParentFile().exists()) {
            //如果目标文件所在的目录不存在，则创建父目录
            targetFile.getParentFile().mkdirs();
        }
        //将内存中的数据写入磁盘
        file.transferTo(targetFile);
        //完整的url
        String fileUrl = urlPrefix + dateDirs + "/" + newFilename;
        FileVO fileVO = new FileVO();
        fileVO.setFileName(newFilename);
        fileVO.setFileUrl(fileUrl);
        Response response = new Response(true, "上传成功", fileVO);
        return response;
    }
}
