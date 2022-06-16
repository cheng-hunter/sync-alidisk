package com.yxhpy.conifg;

import com.yxhpy.utils.ConfigUtils;

/**
 * @author liuguohao
 */
public class RequestConfig {
    public static final String USER_AGENT = ConfigUtils.getConfigString("request.header.ua");
    public static final String SAFE_PASSWORD = ConfigUtils.getConfigString("file.password");
    public static final String SAFE_PASSWORD_SALT = ConfigUtils.getConfigString("file.password.salt");
    public static final Boolean SAFE_PASSWORD_ENABLE = ConfigUtils.getConfigBoolean("file.password.enable");
    public static final Boolean PROVIDER = ConfigUtils.getConfigBoolean("provider.enable");
    public static final String BASE_PATH = ConfigUtils.getConfigString("local.path");
    public static final String REMOTE_PATH = ConfigUtils.getConfigString("remote.path");
    public static final Integer PART_SIZE = ConfigUtils.getConfigInteger("upload.part.size");
    public static final Boolean ENABLE_RESTART = ConfigUtils.getConfigBoolean("enable.restart");
    public static final Integer MULTI_SIZE = ConfigUtils.getConfigInteger("multi.size");
    public static final Integer MULTI_TRY = ConfigUtils.getConfigInteger("multi.try");
    public static final Integer MULTI_THREAD_NUM = ConfigUtils.getConfigInteger("multi.thread.num",0);
    public static final Boolean MULTI_THREAD = ConfigUtils.getConfigBoolean("multi.thread");
    public static final String REFERER = "https://auth.aliyundrive.com/v2/oauth/authorize?client_id=25dzX3vbYqktVxyX&redirect_uri=https%3A%2F%2Fwww.aliyundrive.com%2Fsign%2Fcallback&response_type=code&login_type=custom&state=%7B%22origin%22%3A%22https%3A%2F%2Fwww.aliyundrive.com%22%7D";
}
