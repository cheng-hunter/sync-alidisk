package com.yxhpy.utils;

import com.yxhpy.conifg.RequestConfig;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author liuguohao
 */
public class SafeFile {
    private String encodeStr = "520612lgh";
    private String salt = "123456789";
    private final Charset defaultCharset = StandardCharsets.UTF_8;
    private final byte[] encodeStrBytes;
    private final byte[] saltBytes;
    private final int encodeStrBytesLength;
    private final int saltBytesLength;
    private int step = 1;
    private boolean enable = true;

    public void setStep(int step) {
        this.step = step;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public SafeFile() {
        encodeStrBytes = encodeStr.getBytes(defaultCharset);
        saltBytes = salt.getBytes(defaultCharset);
        encodeStrBytesLength = encodeStrBytes.length;
        saltBytesLength = saltBytes.length;
    }

    public SafeFile(String encodeStr, String salt) {
        this.encodeStr = encodeStr;
        this.salt = salt;
        encodeStrBytes = encodeStr.getBytes(defaultCharset);
        saltBytes = salt.getBytes(defaultCharset);
        encodeStrBytesLength = encodeStrBytes.length;
        saltBytesLength = saltBytes.length;
    }

    public byte xorEec(byte enc, byte src, byte salt) {
        return (byte) ((enc ^ src) ^ salt);
    }

    public byte xorDec(byte enc, byte src, byte salt) {
        return (byte) (enc ^ (src ^ salt));
    }


    public static SafeFile getInstance(){
        SafeFile safeFile = new SafeFile(RequestConfig.SAFE_PASSWORD, RequestConfig.SAFE_PASSWORD_SALT);
        safeFile.setStep(RequestConfig.PART_SIZE);
        safeFile.setEnable(RequestConfig.SAFE_PASSWORD_ENABLE);
        return safeFile;
    }

    public void handler(byte[] bytes, boolean encode) {
        if (!enable) {
            return;
        }
        int block;
        int length = bytes.length;
        if (bytes.length % step == 0) {
            block = length / step;
        } else {
            block = length / step + 1;
        }
        for (int j = 0; j < block; j++) {
            for (int i = step * j; i < Math.min(step * j + step, length); i++) {
                int currentIndex = (i - step * j) % encodeStrBytesLength;
                int currentSaltIndex = (i - step * j)  % saltBytesLength;
                bytes[i] = encode?xorEec(encodeStrBytes[currentIndex], bytes[i], saltBytes[currentSaltIndex]):xorDec(encodeStrBytes[currentIndex], bytes[i], saltBytes[currentSaltIndex]);
            }
        }
    }
    public byte[] handler(String file, boolean encode) {
        byte[] bytes = null;
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            int available = fileInputStream.available();
            bytes = new byte[available];
            fileInputStream.read(bytes);
            handler(bytes, encode);
            return bytes;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    public static void main(String[] args) throws IOException {
        SafeFile safeFile = new SafeFile();
        safeFile.setStep(1024);
        FileOutputStream fileOutputStream1 = new FileOutputStream("C:\\Users\\Administrator\\Desktop\\312-尚硅谷-用户认证-Kerberos实战Kylin之认证测试1.mp4");
        FileOutputStream fileOutputStream2 = new FileOutputStream("C:\\Users\\Administrator\\Desktop\\312-尚硅谷-用户认证-Kerberos实战Kylin之认证测试2.mp4");
        byte[] enc = safeFile.handler("C:\\Users\\Administrator\\Desktop\\312-尚硅谷-用户认证-Kerberos实战Kylin之认证测试.mp4", true);
        fileOutputStream1.write(enc);
        safeFile.handler(enc, false);
        fileOutputStream2.write(enc);
        fileOutputStream1.close();
        fileOutputStream2.close();
    }
}
