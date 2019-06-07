package com.liuyanzhao.forum.util;

/**
 * @author 言曌
 * @date 2018/2/9 下午5:56
 */

public class RandomUtil {

    /**
     * 获取[m,n)的任意数
     *
     * @param m
     * @param n
     * @return
     */
    public static int getRandom(int m, int n) {
        int random = (int) (Math.random() * (n - m)) + m ;
        return random;
    }

    /**
     * 获取位数为n的随机数
     *
     * @param length
     * @return
     */
    public static int getRandom(int length) {
        int m = getNumber(length);
        int n = m * 10 - 1;
        int random = (int) (Math.random() * (n - m)) + m;
        return random;
    }


    /**
     * 第n位为1，其他为0
     * 1
     * 10
     * 100
     * 1000
     * @param n
     * @return
     */
    public static int getNumber(int n) {
        if (n < 1) {
            n = 1;
        }
        if (n == 1) {
            return 1;
        } else {
            n = n - 1;
            return 10 * getNumber(n);
        }
    }


}