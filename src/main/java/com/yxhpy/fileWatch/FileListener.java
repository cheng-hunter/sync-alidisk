package com.yxhpy.fileWatch;
import java.io.File;
import java.util.concurrent.TimeUnit;

import cn.hutool.log.GlobalLogFactory;
import cn.hutool.log.Log;
import com.yxhpy.BaseAliPan;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
/**
 * 文件变化监听器
 * 在Apache的Commons-IO中有关于文件的监控功能的代码. 文件监控的原理如下：
 * 由文件监控类FileAlterationMonitor中的线程不停的扫描文件观察器FileAlterationObserver，
 * 如果有文件的变化，则根据相关的文件比较器，判断文件时新增，还是删除，还是更改。（默认为1000毫秒执行一次扫描）
 * @author Administrator
 */
public class FileListener extends FileAlterationListenerAdaptor {
    private BaseAliPan aliPan;
    private final Log log = GlobalLogFactory.get().createLog(FileListener.class);

    public FileListener(BaseAliPan aliPan) {
        this.aliPan = aliPan;
    }

    public static void start(String rootDir, BaseAliPan aliPan) throws Exception {
        // 轮询间隔 3 秒
        long interval = TimeUnit.SECONDS.toMillis(3);
        // 使用过滤器
        FileAlterationObserver observer = new FileAlterationObserver(new File(rootDir));
        // 不使用过滤器
        // FileAlterationObserver observer = new FileAlterationObserver(new
        // File(rootDir));
        observer.addListener(new FileListener(aliPan));
        // 创建文件变化监听器
        FileAlterationMonitor monitor = new FileAlterationMonitor(interval, observer);
        // 开始监控
        monitor.start();
    }
    /**
     * 文件创建执行
     */
    @Override
    public void onFileCreate(File file) {
        String absoluteFileName = file.getAbsolutePath();
        log.info("[新建]:" + absoluteFileName);
        String absoluteParentPath = file.getParent();
        String folderId = aliPan.getFolderId(absoluteParentPath);
        aliPan.uploadSingleFile(absoluteFileName, folderId);
    }
    /**
     * 文件修改
     */
    @Override
    public void onFileChange(File file) {
        log.info("[修改]:" + file.getAbsolutePath());
        onFileDelete(file);
        onFileCreate(file);
    }
    /**
     * 文件删除
     */
    @Override
    public void onFileDelete(File file) {
        String absoluteFileName = file.getAbsolutePath();
        log.info("[删除]:" + absoluteFileName);
        String id = aliPan.getFolderId(absoluteFileName);
        aliPan.trashFile(id);
        aliPan.removeFileId(absoluteFileName);
    }
    /**
     * 目录创建
     */
    @Override
    public void onDirectoryCreate(File directory) {
        String absolutePath = directory.getAbsolutePath();
        log.info("[新建]:" + absolutePath);
        aliPan.getFolderId(absolutePath);
    }
    /**
     * 目录修改
     */
    @Override
    public void onDirectoryChange(File directory) {
        log.info("[修改]:" + directory.getAbsolutePath());
    }
    /**
     * 目录删除
     */
    @Override
    public void onDirectoryDelete(File directory) {
        String absolutePath = directory.getAbsolutePath();
        log.info("[删除]:" + absolutePath);
        String id = aliPan.getFileIdByPath(absolutePath);
        aliPan.trashFile(id);
    }
}
