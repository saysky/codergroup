package com.liuyanzhao.forum.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author 言曌
 * @date 2018/6/13 上午1:04
 */

public class BaiduPostUtil {



    /**
     * 百度链接实时推送
     *
     * @param Parameters
     * @return
     */
    public static String Post(String[] Parameters) {
        String PostUrl = "http://data.zz.baidu.com/urls?site=codergroup.cn&token=crF4iVbbiEI7YxVG";
        String result = "";
        PrintWriter out = null;
        BufferedReader in = null;
        try {
            //建立URL之间的连接
            URLConnection conn = new URL(PostUrl).openConnection();
            //设置通用的请求属性
            conn.setRequestProperty("Host", "data.zz.baidu.com");
            conn.setRequestProperty("User-Agent", "curl/7.12.1");
            conn.setRequestProperty("Content-Length", "83");
            conn.setRequestProperty("Content-Type", "text/plain");

            //发送POST请求必须设置如下两行
            conn.setDoInput(true);
            conn.setDoOutput(true);

            //获取conn对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            //发送请求参数
            String param = "";
            for (String s : Parameters) {
                param += s + "\n";
            }
            out.print(param.trim());
            //进行输出流的缓冲
            out.flush();
            //通过BufferedReader输入流来读取Url的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送post请求出现异常！" + e);
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        //网站的服务器连接
        String[] param = {
                //需要推送的网址
                "https://liuyanzhao.com/message.html",
                "https://liuyanzhao.com/article-file.html",
                "https://liuyanzhao.com/sitemap.html"
        };
        //执行推送方法
        String json = Post(param);
        //打印推送结果
        System.out.println("结果是" + json);

    }
}
