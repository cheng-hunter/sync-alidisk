package com.yxhpy.utils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author liuguohao
 */
public class SafeFile {
    private String encodeStr = "520612lgh";
    private String salt = "123456789";
    private Charset defaultCharset = StandardCharsets.UTF_8;
    private byte[] encodeStrBytes;
    private byte[] saltBytes;
    private int encodeStrBytesLength;
    private int saltBytesLength;

    public SafeFile() {
        encodeStrBytes = encodeStr.getBytes(defaultCharset);
        saltBytes = salt.getBytes(defaultCharset);
        encodeStrBytesLength = encodeStrBytes.length;
        saltBytesLength = saltBytes.length;
    }

    public SafeFile(String encodeStr, String salt, Charset defaultCharset) {
        this.encodeStr = encodeStr;
        this.salt = salt;
        this.defaultCharset = defaultCharset;
    }

    public SafeFile(String encodeStr, String salt) {
        this.encodeStr = encodeStr;
        this.salt = salt;
    }

    public byte xorEec(byte enc, byte src, byte salt) {
        return (byte) ((enc ^ src) ^ salt);
    }

    public byte xorDec(byte enc, byte src, byte salt) {
        return (byte) (enc ^ (src ^ salt));
    }


    public void handler(byte[] bytes, boolean encode) {
        for (int i = 0; i < bytes.length; i++) {
            int currentIndex = i % encodeStrBytesLength;
            int currentSaltIndex = i % saltBytesLength;
            bytes[i] = encode?xorEec(encodeStrBytes[currentIndex], bytes[i], saltBytes[currentSaltIndex]):xorDec(encodeStrBytes[currentIndex], bytes[i], saltBytes[currentSaltIndex]);
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
        FileOutputStream fileOutputStream1 = new FileOutputStream("C:\\Users\\liuguohao\\Downloads\\PS20211.exe");
        FileOutputStream fileOutputStream2 = new FileOutputStream("C:\\Users\\liuguohao\\Downloads\\PS20212.exe");
        byte[] enc = safeFile.handler("C:\\Users\\liuguohao\\Downloads\\PS2021.exe", true);
        fileOutputStream1.write(enc);
        safeFile.handler(enc, false);
        fileOutputStream2.write(enc);
        fileOutputStream1.close();
        fileOutputStream2.close();
    }
}
