package com.yxhpy.utils;

import cn.hutool.core.codec.Base64Encoder;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.HexUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Objects;

/**
 * @author liuguohao
 * @date 2022/1/1 22:20
 */
public class MD5Util {
    public static final String SHA1 = "SHA-1";
    public static final String MD5 = "MD5";
    public static final String SHA256 = "SHA-256";

    private static String validHexStr(String str){
        if (str.length() % 2 != 0) {
            return "0" + str;
        }
        return str;
    }


    public static String hashCode(byte[] data, String type) {
        try {
            MessageDigest md = MessageDigest.getInstance(type);
            md.update(data, 0, data.length);
            byte[] md5Bytes = md.digest();
            return HexUtil.encodeHexStr(md5Bytes, true);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String hashCode(InputStream fis, String type) {
        try {
            MessageDigest md = MessageDigest.getInstance(type);
            byte[] buffer = new byte[1024];
            int length = -1;
            while ((length = fis.read(buffer, 0, 1024)) != -1) {
                md.update(buffer, 0, length);
            }
            byte[] md5Bytes = md.digest();
            return HexUtil.encodeHexStr(md5Bytes, true);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static BigDecimal longParseUnsigned(long value) {
        if (value >= 0) {
            return new BigDecimal(value);
        }
        long lowValue = value & 0x7fffffffffffffffL;
        return BigDecimal.valueOf(lowValue).add(BigDecimal.valueOf(Long.MAX_VALUE)).add(BigDecimal.valueOf(1));
    }

    public static void main(String[] args) {
        File file = new File("C:\\Users\\liuguohao\\Desktop\\新建Microsoft Excel 工作表.xlsx");
        byte[] bytes = FileUtil.readBytes(file);
        System.out.println(hashCode(bytes, MD5Util.SHA1));
    }
}
