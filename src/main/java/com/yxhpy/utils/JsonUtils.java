package com.yxhpy.utils;

import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @author liuguohao
 */
public class JsonUtils {
    public static <T> T base64ToBean(String b4Str, Class<T> aClass) {
        try {
            Base64.Decoder decoder = Base64.getDecoder();
            byte[] gbks = decoder.decode(b4Str.getBytes("gbk"));
            return JSONUtil.parse(new String(gbks, "gbk")).toBean(aClass);
        } catch (Exception ignored) {
            return null;
        }
    }

    public static <T> T responseToBean(HttpResponse response, Class<T> aClass) {
        return JSONUtil.parse(response.body()).toBean(aClass);
    }


    public static <T> void writeBean(T bean, File file) {
        byte[] bytes = JSONUtil.toJsonStr(bean).getBytes(StandardCharsets.UTF_8);
        SafeFile safeFile = new SafeFile();
        safeFile.handler(bytes, true);
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            fileOutputStream.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static <T> void writeBean(T bean, String fileName) {
        writeBean(bean, new File(fileName));
    }

    public static <T> T readBean(String fileName, Class<T> aClass) {
        return readBean(new File(fileName), aClass);
    }

    public static <T> T readBean(File file, Class<T> aClass) {
        try (FileInputStream inputStream = new FileInputStream(file)) {
            int available = inputStream.available();
            byte[] bytes = new byte[available];
            if (inputStream.read(bytes) > 0) {
                SafeFile safeFile = new SafeFile();
                safeFile.handler(bytes, false);
                return JSONUtil.parse(new String(bytes, StandardCharsets.UTF_8)).toBean(aClass);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
