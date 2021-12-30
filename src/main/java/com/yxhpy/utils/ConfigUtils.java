package com.yxhpy.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author liuguohao
 */
public class ConfigUtils {
    private static Properties properties;
    static {
        try {
            InputStream resource = ConfigUtils.class.getClassLoader().getResourceAsStream("config.properties");
            properties = new Properties();
            properties.load(resource);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static String getConfig(String key){
        return (String) properties.get(key);
    }
    public static Properties getProperties() {
        return properties;
    }
}
