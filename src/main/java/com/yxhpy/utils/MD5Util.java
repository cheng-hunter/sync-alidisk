package com.yxhpy.utils;

import cn.hutool.core.codec.Base64Encoder;
import cn.hutool.core.io.FileUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * @author liuguohao
 * @date 2022/1/1 22:20
 */
public class MD5Util {
    public static final String SHA1 = "SHA-1";
    public static final String MD5 = "MD5";
    public static final String SHA256 = "SHA-256";

    public static String hashCode(byte[] data, String type) {
        try {
            MessageDigest md = MessageDigest.getInstance(type);
            md.update(data, 0, data.length);
            byte[] md5Bytes = md.digest();
            BigInteger bigInt = new BigInteger(1, md5Bytes);
            return bigInt.toString(16);
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
            BigInteger bigInt = new BigInteger(1, md5Bytes);
            return bigInt.toString(16);
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
        File file = new File("D:\\sync\\ideaIU-2021.2.1.exe");
        byte[] bytes = FileUtil.readBytes(file);
        byte[] proofCodeLimit = new byte[8];
        System.arraycopy(bytes, 414605323, proofCodeLimit, 0, 8);
        String proofCode = Base64Encoder.encode(proofCodeLimit);
        System.out.println(proofCode);
    }
}
