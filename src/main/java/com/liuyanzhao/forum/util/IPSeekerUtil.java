package com.liuyanzhao.forum.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * qqwry.dat获取归属地工具类
 * @author 言曌
 * @date 2018/4/9 下午8:39
 */

public class IPSeekerUtil {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final int IP_RECORD_LENGTH = 7;
    private static final byte AREA_FOLLOWED = 0x01;
    private static final byte NO_AREA = 0x2;

    private MappedByteBuffer buffer; // 内存映射文件,提高IO 读取效率

    private HashMap<String, IPLocation> cache = new HashMap<String, IPLocation>(); // 用来做为cache，查询一个ip时首先查看cache，以减少不必要的重复查找

    private int ipBegin;
    private int ipEnd;

    @SuppressWarnings("resource")
    public IPSeekerUtil(File file) throws Exception {
        buffer = new RandomAccessFile(file, "r").getChannel().map(
                FileChannel.MapMode.READ_ONLY, 0, file.length());

        if (buffer.order().toString().equals(ByteOrder.BIG_ENDIAN.toString())) {
            buffer.order(ByteOrder.LITTLE_ENDIAN);
        }

        ipBegin = readInt(0);
        ipEnd = readInt(4);

        if (ipBegin == -1 || ipEnd == -1) {
            throw new IOException("IP地址信息文件格式有错误，IP显示功能将无法使用");
        }
        logger.debug("使用IP地址库:" + file.getAbsolutePath());
    }

    /**
     * 给定一个ip 得到一个 ip地址信息
     *
     * @param ip
     * @return
     */
    public String getAddress(String ip) {
        return getCountry(ip) + " " + getArea(ip);
    }

    /**
     * 根据IP得到国家名
     *
     * @param ip
     *            IP的字符串形式
     * @return 国家名字符串
     */
    public String getCountry(String ip) {
        IPLocation cache = getIpLocation(ip);
        return cache.getCountry();
    }

    /**
     * 根据IP得到地区名
     *
     * @param ip
     *            IP的字符串形式
     * @return 地区名字符串
     */
    public String getArea(String ip) {
        IPLocation cache = getIpLocation(ip);
        return cache.getArea();
    }

    /**
     * 获得一个IP地址信息
     *
     * @param ip
     * @return
     */
    public IPLocation getIpLocation(String ip) {
        IPLocation ipLocation = null;
        try {
            if (cache.get(ip) != null) {
                return cache.get(ip);
            }
            ipLocation = getIPLocation(getIpByteArrayFromString(ip));
            if (ipLocation != null) {
                cache.put(ip, ipLocation);
            }
        } catch (Exception e) {
            logger.error(String.valueOf(e));
        }
        if (ipLocation == null) {
            ipLocation = new IPLocation();
            ipLocation.setCountry("未知国家");
            ipLocation.setArea("未知地区");
        }
        return ipLocation;
    }

    /**
     * 给定一个地点的不完全名字，得到一系列包含s子串的IP范围记录
     *
     * @param s
     *            地点子串
     * @return 包含IPEntry类型的List
     */
    public List<IPEntry> getIPEntries(String s) {
        List<IPEntry> ret = new ArrayList<IPEntry>();
        byte[] b4 = new byte[4];
        int endOffset = ipEnd + 4;
        for (int offset = ipBegin + 4; offset <= endOffset; offset += IP_RECORD_LENGTH) {
            // 读取结束IP偏移
            int temp = readInt3(offset);
            // 如果temp不等于-1，读取IP的地点信息
            if (temp != -1) {
                IPLocation loc = getIPLocation(temp);
                // 判断是否这个地点里面包含了s子串，如果包含了，添加这个记录到List中，如果没有，继续
                if (loc.country.indexOf(s) != -1 || loc.area.indexOf(s) != -1) {
                    IPEntry entry = new IPEntry();
                    entry.country = loc.country;
                    entry.area = loc.area;
                    // 得到起始IP
                    readIP(offset - 4, b4);
                    entry.beginIp = getIpStringFromBytes(b4);
                    // 得到结束IP
                    readIP(temp, b4);
                    entry.endIp = getIpStringFromBytes(b4);
                    // 添加该记录
                    ret.add(entry);
                }
            }
        }
        return ret;
    }

    /**
     * 根据ip搜索ip信息文件，得到IPLocation结构，所搜索的ip参数从类成员ip中得到
     *
     * @param ip
     *            要查询的IP
     * @return IPLocation结构
     */
    private IPLocation getIPLocation(byte[] ip) {
        IPLocation info = null;
        int offset = locateIP(ip);
        if (offset != -1) {
            info = getIPLocation(offset);
        }
        return info;
    }

    // -----------------以下为内部方法

    /**
     * 读取4个字节
     *
     * @param offset
     * @return
     */
    private int readInt(int offset) {
        buffer.position(offset);
        return buffer.getInt();
    }

    private int readInt3(int offset) {
        buffer.position(offset);
        return buffer.getInt() & 0x00FFFFFF;
    }

    /**
     * 从内存映射文件的offset位置得到一个0结尾字符串
     *
     * @param offset
     * @return
     */
    private String readString(int offset) {
        try {
            byte[] buf = new byte[100];
            buffer.position(offset);
            int i;
            for (i = 0, buf[i] = buffer.get(); buf[i] != 0; buf[++i] = buffer
                    .get()) {
            }
            if (i != 0) {
                return getString(buf, 0, i, "GBK");
            }
        } catch (IllegalArgumentException e) {
        }
        return "";
    }

    /**
     * 从offset位置读取四个字节的ip地址放入ip数组中，读取后的ip为big-endian格式，但是
     * 文件中是little-endian形式，将会进行转换
     *
     * @param offset
     * @param ip
     */
    private void readIP(int offset, byte[] ip) {
        buffer.position(offset);
        buffer.get(ip);
        byte temp = ip[0];
        ip[0] = ip[3];
        ip[3] = temp;
        temp = ip[1];
        ip[1] = ip[2];
        ip[2] = temp;
    }

    /**
     * 把类成员ip和beginIp比较，注意这个beginIp是big-endian的
     *
     * @param ip
     *            要查询的IP
     * @param beginIp
     *            和被查询IP相比较的IP
     * @return 相等返回0，ip大于beginIp则返回1，小于返回-1。
     */
    private int compareIP(byte[] ip, byte[] beginIp) {
        for (int i = 0; i < 4; i++) {
            int r = compareByte(ip[i], beginIp[i]);
            if (r != 0) {
                return r;
            }
        }
        return 0;
    }

    /**
     * 把两个byte当作无符号数进行比较
     *
     * @param b1
     * @param b2
     * @return 若b1大于b2则返回1，相等返回0，小于返回-1
     */
    private int compareByte(byte b1, byte b2) {
        if ((b1 & 0xFF) > (b2 & 0xFF)) // 比较是否大于
        {
            return 1;
        } else if ((b1 ^ b2) == 0)// 判断是否相等
        {
            return 0;
        } else {
            return -1;
        }
    }

    /**
     * 这个方法将根据ip的内容，定位到包含这个ip国家地区的记录处，返回一个绝对偏移 方法使用二分法查找。
     *
     * @param ip
     *            要查询的IP
     * @return 如果找到了，返回结束IP的偏移，如果没有找到，返回-1
     */
    private int locateIP(byte[] ip) {
        int m = 0;
        int r;
        byte[] b4 = new byte[4];
        // 比较第一个ip项
        readIP(ipBegin, b4);
        r = compareIP(ip, b4);
        if (r == 0) {
            return ipBegin;
        } else if (r < 0) {
            return -1;
        }
        // 开始二分搜索
        for (int i = ipBegin, j = ipEnd; i < j;) {
            m = getMiddleOffset(i, j);
            readIP(m, b4);
            r = compareIP(ip, b4);
            // log.debug(Utils.getIpStringFromBytes(b));
            if (r > 0) {
                i = m;
            } else if (r < 0) {
                if (m == j) {
                    j -= IP_RECORD_LENGTH;
                    m = j;
                } else {
                    j = m;
                }
            } else {
                return readInt3(m + 4);
            }
        }
        // 如果循环结束了，那么i和j必定是相等的，这个记录为最可能的记录，但是并非
        // 肯定就是，还要检查一下，如果是，就返回结束地址区的绝对偏移
        m = readInt3(m + 4);
        readIP(m, b4);
        r = compareIP(ip, b4);
        if (r <= 0) {
            return m;
        } else {
            return -1;
        }
    }

    /**
     * 得到begin偏移和end偏移中间位置记录的偏移
     *
     * @param begin
     * @param end
     * @return
     */
    private int getMiddleOffset(int begin, int end) {
        int records = (end - begin) / IP_RECORD_LENGTH;
        records >>= 1;
        if (records == 0) {
            records = 1;
        }
        return begin + records * IP_RECORD_LENGTH;
    }

    /**
     * @param offset
     * @return
     */
    private IPLocation getIPLocation(int offset) {
        IPLocation loc = new IPLocation();
        // 跳过4字节ip
        buffer.position(offset + 4);
        // 读取第一个字节判断是否标志字节
        byte b = buffer.get();
        if (b == AREA_FOLLOWED) {
            // 读取国家偏移
            int countryOffset = readInt3();
            // 跳转至偏移处
            buffer.position(countryOffset);
            // 再检查一次标志字节，因为这个时候这个地方仍然可能是个重定向
            b = buffer.get();
            if (b == NO_AREA) {
                loc.country = readString(readInt3());
                buffer.position(countryOffset + 4);
            } else {
                loc.country = readString(countryOffset);
            }
            // 读取地区标志
            loc.area = readArea(buffer.position());
        } else if (b == NO_AREA) {
            loc.country = readString(readInt3());
            loc.area = readArea(offset + 8);
        } else {
            loc.country = readString(buffer.position() - 1);
            loc.area = readArea(buffer.position());
        }
        return loc;
    }

    /**
     * @param offset
     * @return
     */
    private String readArea(int offset) {
        buffer.position(offset);
        byte b = buffer.get();
        if (b == 0x01 || b == 0x02) {
            int areaOffset = readInt3();
            if (areaOffset == 0) {
                return "未知地区";
            } else {
                return readString(areaOffset);
            }
        } else {
            return readString(offset);
        }
    }

    /**
     * 从内存映射文件的当前位置开始的3个字节读取一个int
     *
     * @return
     */
    private int readInt3() {
        return buffer.getInt() & 0x00FFFFFF;
    }

    /**
     * 从ip的字符串形式得到字节数组形式
     *
     * @param ip
     *            字符串形式的ip
     * @return 字节数组形式的ip
     */
    private static byte[] getIpByteArrayFromString(String ip) throws Exception {
        byte[] ret = new byte[4];
        java.util.StringTokenizer st = new java.util.StringTokenizer(ip, ".");
        try {
            ret[0] = (byte) (Integer.parseInt(st.nextToken()) & 0xFF);
            ret[1] = (byte) (Integer.parseInt(st.nextToken()) & 0xFF);
            ret[2] = (byte) (Integer.parseInt(st.nextToken()) & 0xFF);
            ret[3] = (byte) (Integer.parseInt(st.nextToken()) & 0xFF);
        } catch (Exception e) {
            throw e;
        }
        return ret;
    }

    /**
     * 根据某种编码方式将字节数组转换成字符串
     *
     * @param b
     *            字节数组
     * @param offset
     *            要转换的起始位置
     * @param len
     *            要转换的长度
     * @param encoding
     *            编码方式
     * @return 如果encoding不支持，返回一个缺省编码的字符串
     */
    private static String getString(byte[] b, int offset, int len,
                                    String encoding) {
        try {
            return new String(b, offset, len, encoding);
        } catch (UnsupportedEncodingException e) {
            return new String(b, offset, len);
        }
    }

    /**
     * @param ip
     *            ip的字节数组形式
     * @return 字符串形式的ip
     */
    private static String getIpStringFromBytes(byte[] ip) {
        StringBuffer sb = new StringBuffer();
        sb.append(ip[0] & 0xFF);
        sb.append('.');
        sb.append(ip[1] & 0xFF);
        sb.append('.');
        sb.append(ip[2] & 0xFF);
        sb.append('.');
        sb.append(ip[3] & 0xFF);
        return sb.toString();
    }


    public class IPLocation {
        private String country;// 所在国家
        private String area;// 所在地区

        public IPLocation() {
        }

        public IPLocation(String country, String area) {
            this.country = country;
            this.area = area;
        }

        public IPLocation getCopy() {
            return new IPLocation(country, area);
        }

        public String getArea() {
            return " CZ88.NET".equals(area) ? "" : area;
        }

        public void setArea(String area) {
            this.area = area;
        }

        public String getCountry() {
            return " CZ88.NET".equals(country) ? "" : country;
        }

        public void setCountry(String country) {
            this.country = country;
        }
    }

    /**
     * * 一条IP范围记录，不仅包括国家和区域，也包括起始IP和结束IP *
     *
     */
    public class IPEntry {
        public String beginIp;
        public String endIp;
        public String country;
        public String area;

        /**
         * 构造函数
         */
        public IPEntry() {
        }

        @Override
        public String toString() {
            return new StringBuilder(this.area).append(";")
                    .append(this.country).append(";").append("IP范围:")
                    .append(beginIp).append("-").append(endIp).toString();
        }
    }

}
