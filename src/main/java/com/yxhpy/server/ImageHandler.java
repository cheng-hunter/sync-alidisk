package com.yxhpy.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

public class ImageHandler implements HttpHandler {
    private String path;

    public ImageHandler(String path){
        this.path=path;
    }

    @Override
    public void handle(HttpExchange t) throws IOException {
        File f=new File(path);
        t.getResponseHeaders().set("Content-Type", "image/png");
        t.sendResponseHeaders(200, f.length());
        OutputStream os = t.getResponseBody();
        os.write(Files.readAllBytes(f.toPath()));
        os.close();
    }
}
