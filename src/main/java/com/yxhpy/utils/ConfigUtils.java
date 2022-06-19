package com.yxhpy.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * @author liuguohao
 */
public class ConfigUtils {
    private static Properties properties;
    static {
        try {
            InputStream resource = new FileInputStream("config.properties");
            properties = new Properties();
            properties.load(resource);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static String getConfigString(String key){
        return new String(properties.getProperty(key).getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
    }


    public static Boolean getConfigBoolean(String key){
        return Boolean.valueOf((String) properties.get(key));
    }

    public static Integer getConfigInteger(String key){
        return Integer.valueOf((String) properties.get(key));
    }

    public static Integer getConfigInteger(String key, int def) {
        return properties.get(key) == null ? def
                : Integer.parseInt((String) properties.get(key));
    }

    public static Properties getProperties() {
        return properties;
    }
}
