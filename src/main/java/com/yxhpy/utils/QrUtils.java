package com.yxhpy.utils;

import cn.hutool.extra.qrcode.QrCodeUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.yxhpy.server.ImageHandler;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

/**
 * @author liuguohao
 */
public class QrUtils {

    public static void dialogShowQrImg(String url) {
        Thread t = new Thread(() -> {
            QrCodeUtil.generate(url,200,200,new File("/home/hunter/log.png"));
            try {
                HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
                server.createContext("/getImage", new ImageHandler("/home/hunter/log.png"));
                server.setExecutor(null);
                System.out.println("Starting server on port: 8000");
                server.start();
            } catch (IOException e) {

            }

        });
        t.setDaemon(true);
        t.start();
    }
}

