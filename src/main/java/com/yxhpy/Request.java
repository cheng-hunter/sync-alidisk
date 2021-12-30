package com.yxhpy;

import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.Method;
import cn.hutool.json.JSONUtil;
import com.yxhpy.conifg.RequestConfig;

import java.net.HttpCookie;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author liuguohao
 */
public class Request {
    private final Map<String, String> headers;
    private final HttpRequest httpRequest;

    public Request() {
        httpRequest = new HttpRequest(UrlBuilder.create());
        httpRequest.enableDefaultCookie();
        headers = new HashMap<>();
        headers.put("User-Agent", RequestConfig.USER_AGENT);
        headers.put("Referer", RequestConfig.REFERER);
        httpRequest.addHeaders(headers);
    }

    public HttpResponse request(String url, Method method, Map<String, Object> params, boolean typeJson) {
        HttpRequest httpRequest = this.httpRequest.setUrl(url).setMethod(method);
        if (params != null) {
            if (typeJson) {
                httpRequest.body(JSONUtil.toJsonStr(params));
            } else {
                httpRequest.form(params);
            }
        }
        return  httpRequest.execute();
    }

    public HttpResponse post(String url, Map<String, Object> params) {
        return request(url, Method.POST, params, false);
    }

    public HttpResponse postJson(String url, Map<String, Object> params) {
        return request(url, Method.POST, params, true);
    }

    public HttpResponse get(String url) {
        return request(url, Method.GET, null, false);
    }

    public void reset(){
        httpRequest.enableDefaultCookie();
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public HttpRequest getHttpRequest() {
        return httpRequest;
    }
}
