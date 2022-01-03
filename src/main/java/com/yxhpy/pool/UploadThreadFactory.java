package com.yxhpy.pool;

import java.util.concurrent.ThreadFactory;

/**
 * @author liuguohao
 * @date 2022/1/2 12:21
 */
public class UploadThreadFactory implements ThreadFactory {
    @Override
    public Thread newThread(Runnable r) {
        return new Thread(r);
    }
}
