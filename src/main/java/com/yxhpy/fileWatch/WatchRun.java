package com.yxhpy.fileWatch;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.watch.WatchKind;
import cn.hutool.log.GlobalLogFactory;
import cn.hutool.log.Log;
import com.yxhpy.BaseAliPan;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.WatchEvent;

/**
 * @author liuguohao
 * @date 2022/1/2 17:59
 */
public class WatchRun {
    private final WatchEvent<?> event;
    private final Path currentPath;
    private final Log log = GlobalLogFactory.get().createLog(WatchRun.class);
    public WatchRun(WatchEvent<?> event, Path currentPath) {
        this.event = event;
        this.currentPath = currentPath;
    }
    public void run(BaseAliPan aliPan){
        synchronized (WatchRun.class){
            String name = event.context().toString();
            String path = currentPath.toString();
            String absName = path + File.separator + name;
            if (event.kind() == WatchKind.MODIFY.getValue() || event.kind() == WatchKind.CREATE.getValue()){
                if (FileUtil.isFile(absName)){
                    String fileIdByPath = aliPan.getFileIdByPath(absName);
                    if (fileIdByPath != null){
                        aliPan.trashFile(fileIdByPath);
                    }
                    String fileId = aliPan.getFileIdByPath(path);
                    aliPan.uploadSingleFile(absName, fileId);
                    log.info(absName + "文件更新完成");
                }
                if (FileUtil.isDirectory(absName)){
                    if (event.kind() == WatchKind.CREATE.getValue()){
                        String fileIdByPath = aliPan.getFileIdByPath(absName);
                        if (fileIdByPath != null){
                            aliPan.trashFile(fileIdByPath);
                        }
                    }
                    aliPan.getFolderId(absName);
                }
            }
            if (event.kind() == WatchKind.DELETE.getValue()){
                aliPan.trashFile(aliPan.getFileIdByPath(absName));
                aliPan.removeFileId(absName);
                log.info(absName + "删除成功");
            }
        }
    }
}
