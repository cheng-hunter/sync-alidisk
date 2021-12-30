package com.yxhpy.conifg;

import com.yxhpy.utils.ConfigUtils;

/**
 * @author liuguohao
 */
public class RequestConfig {
    public static final String USER_AGENT = ConfigUtils.getConfig("request.header.ua");
    public static final String REFERER = "https://auth.aliyundrive.com/v2/oauth/authorize?client_id=25dzX3vbYqktVxyX&redirect_uri=https%3A%2F%2Fwww.aliyundrive.com%2Fsign%2Fcallback&response_type=code&login_type=custom&state=%7B%22origin%22%3A%22https%3A%2F%2Fwww.aliyundrive.com%22%7D";
}
