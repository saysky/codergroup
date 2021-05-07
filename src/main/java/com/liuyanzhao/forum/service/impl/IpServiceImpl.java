package com.liuyanzhao.forum.service.impl;

import com.liuyanzhao.forum.service.IpService;
import com.liuyanzhao.forum.util.IPSeekerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;

/**
 * @author 言曌
 * @date 2018/4/9 下午9:32
 */

@Service
public class IpServiceImpl implements IpService{

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static IPSeekerUtil ipSeeker;
    @Value("${qqwry.dat.path}")
    private String filepath;
    @Override
    public String getIpArea(String ip) {
        if (ipSeeker == null) {
            try {
                ipSeeker = new IPSeekerUtil(new File(filepath));
            } catch (Exception e) {
                logger.error("IP地址库实例化出错", e);
                return "IP地址库实例化出错";
            }
        }
        IPSeekerUtil.IPLocation ipl = ipSeeker.getIpLocation(ip);
        return ipl.getArea();
    }

    public static void main(String[] args) {
        String str = new IpServiceImpl().getIpArea("216.204.2.3");
        System.out.println(str);
    }
}
