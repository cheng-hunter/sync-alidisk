package com.yxhpy.pool;

import java.util.concurrent.*;

/**
 * @author liuguohao
 * @date 2022/1/2 12:19
 */
public class UploadThreadPool{
    private static final TimeUnit UNIT = TimeUnit.SECONDS;
    private static final BlockingQueue<Runnable> WORK_QUEUE = new ArrayBlockingQueue<>(2);
    private static final ThreadFactory UPLOAD_THREAD_FACTORY = new UploadThreadFactory();
    private static final RejectedExecutionHandler UPLOAD_HANDLER = new UploadHandler();
    public static ThreadPoolExecutor getInstance() {
        int corePoolSize = 2;
        int maximumPoolSize = 4;
        long keepAliveTime = 10;
        return new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                UNIT,
                WORK_QUEUE,
                UPLOAD_THREAD_FACTORY,
                UPLOAD_HANDLER
        );
    }
}
