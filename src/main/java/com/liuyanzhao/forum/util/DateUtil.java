package com.liuyanzhao.forum.util;

/**
 * @author 言曌
 * @date 2018/4/20 上午8:12
 */

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    public static String getRelativeDate(Date d) {
        long delta = ((new Date()).getTime() - d.getTime()) / 1000;
        if (delta < 0) {
            return d.toLocaleString();
        }
        if (delta / (60 * 60 * 24 * 365) > 0) {
            return delta / (60 * 60 * 24 * 365) + "年前";
        }
        if (delta / (60 * 60 * 24 * 30) > 0) {
            return delta / (60 * 60 * 24 * 30) + "个月前";
        }
        if (delta / (60 * 60 * 24 * 7) > 0) {
            return delta / (60 * 60 * 24 * 7) + "周前";
        }
        if (delta / (60 * 60 * 24) > 0) {
            return delta / (60 * 60 * 24) + "天前";
        }
        if (delta / (60 * 60) > 0) {
            return delta / (60 * 60) + "小时前";
        }
        if (delta / 60 > 0) {
            return delta / 60 + "分钟前";
        }
//        if (delta / 3 > 0) {
//            return delta / 3 + "秒前";
//        }
        return "刚刚";
    }




}