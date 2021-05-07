package com.liuyanzhao.forum.util;

import org.apache.commons.lang.StringEscapeUtils;


/**
 * @author 言曌
 * @date 2018/5/18 上午10:17
 */
public class HTMLUtil {

    public static String html2text(String str) {
        str = str.replaceAll("\\<.*?\\>", "");
        str = StringEscapeUtils.unescapeHtml(str);
        return str;
    }

}
