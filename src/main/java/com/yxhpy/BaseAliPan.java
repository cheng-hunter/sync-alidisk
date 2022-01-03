package com.yxhpy;

import cn.hutool.core.codec.Base64Encoder;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.watch.*;
import cn.hutool.http.*;
import cn.hutool.log.GlobalLogFactory;
import cn.hutool.log.Log;
import cn.hutool.script.JavaScriptEngine;
import com.yxhpy.fileWatch.FileListener;
import com.yxhpy.fileWatch.WatchRun;
import com.yxhpy.entity.DownloadEntity;
import com.yxhpy.entity.EncFileInfoEntity;
import com.yxhpy.entity.response.*;
import com.yxhpy.enumCode.QrStatus;
import com.yxhpy.utils.*;
import okhttp3.*;

import javax.script.*;
import java.io.*;
import java.math.BigInteger;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author liuguohao
 */
public class BaseAliPan {
    private final Request request;
    private PdsLoginResult loginInfo;
    private static final String R_CODE = "code=(\\w+)";
    private static final String BASE_PATH = "D:\\sync";
    private static final String REMOTE_PATH = "sync_path";
    private static final Integer PART_SIZE = 10 * 1024 * 1024;
    private static final String FILE_TYPE_FILE = "file";
    private static final String FILE_TYPE_FOLDER = "folder";
    private String REMOTE_FILE_ID;
    private static final ConcurrentSkipListSet<String> EXISTS_FILE_NAMES = new ConcurrentSkipListSet<>();
    private final Log log = GlobalLogFactory.get().createLog(BaseAliPan.class);

    public BaseAliPan() {
        request = new Request();
    }

    public DataEntity getLoginQr() {
        request.get("https://auth.aliyundrive.com/v2/oauth/authorize?client_id=25dzX3vbYqktVxyX&redirect_uri=https%3A%2F%2Fwww.aliyundrive.com%2Fsign%2Fcallback&response_type=code&login_type=custom&state=%7B%22origin%22%3A%22https%3A%2F%2Fwww.aliyundrive.com%22%7D");
        HttpResponse response = request.get("https://passport.aliyundrive.com/newlogin/qrcode/generate.do?appName=aliyun_drive&fromSite=52&appName=aliyun_drive&appEntrance=web&_csrf_token=8iPG8rL8zndjoUQhrQnko5&umidToken=27f197668ac305a0a521e32152af7bafdb0ebc6c&isMobile=false&lang=zh_CN&returnUrl=&hsiz=1d3d27ee188453669e48ee140ea0d8e1&fromSite=52&bizParams=&_bx-v=2.0.31");
        ResultEntity resultEntity = JsonUtils.responseToBean(response, ResultEntity.class);
        if (!AnalyticalResults.isSuccess(resultEntity)) {
            log.error("获取二维码失败");
            return null;
        }
        return resultEntity.getContent().getData();
    }

    public ResultEntity getQrStatus(HashMap<String, Object> map) {
        while (true) {
            HttpResponse response = request.post("https://passport.aliyundrive.com/newlogin/qrcode/query.do?appName=aliyun_drive&fromSite=52&_bx-v=2.0.31", map);
            ResultEntity resultEntity = JsonUtils.responseToBean(response, ResultEntity.class);
            if (!AnalyticalResults.isSuccess(resultEntity)) {
                log.error("二维码状态获取失败");
                break;
            }
            String qrCodeStatus = resultEntity.getContent().getData().getQrCodeStatus();
            QrStatus qrStatus = QrStatus.getQrStatusByName(qrCodeStatus);
            if (qrStatus == QrStatus.CONFIRMED || qrStatus == QrStatus.EXPIRED) {
                return resultEntity;
            }
            assert qrStatus != null;
            log.info(qrStatus.getDes());
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public String getRefreshToken(String accessToken) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("token", accessToken);
        HttpResponse response = request.postJson("https://auth.aliyundrive.com/v2/oauth/token_login", map);
        Pattern compile = Pattern.compile(R_CODE);
        Matcher matcher = compile.matcher(response.body());
        if (matcher.find()) {
            String code = matcher.group(1);
            map.clear();
            map.put("code", code);
            response = request.postJson("https://api.aliyundrive.com/token/get", map);
            PdsLoginResult pdsLoginResult = JsonUtils.responseToBean(response, PdsLoginResult.class);
            return pdsLoginResult.getRefreshToken();
        }
        return null;
    }

    public boolean refreshToken(String refreshToken) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("refresh_token", refreshToken);
        Request request = new Request();
        HttpResponse response = request.postJson("https://api.aliyundrive.com/token/refresh", map);
        PdsLoginResult loginEntity = JsonUtils.responseToBean(response, PdsLoginResult.class);
        if (loginEntity == null || loginEntity.getTokenType() == null || loginEntity.getAccessToken() == null) {
            log.warn("token已经过期，请重新登录:" + response.body());
            return false;
        }
        loginInfo = loginEntity;
        this.request.getHttpRequest().bearerAuth(loginEntity.getAccessToken());
        log.info("免密登录成功，无需重新扫码");
        return true;
    }


    public FolderEntity getFolderFileList(String parentFileId) {
        String defaultDriveId = loginInfo.getDefaultDriveId();
        HashMap<String, Object> map = new HashMap<>();
        map.put("all", false);
        map.put("drive_id", defaultDriveId);
        map.put("fields", "*");
        map.put("image_thumbnail_process", "image/resize,w_400/format,jpeg");
        map.put("image_url_process", "image/resize,w_1920/format,jpeg");
        map.put("limit", 100);
        map.put("order_by", "");
        map.put("order_direction", "DESC");
        map.put("parent_file_id", parentFileId);
        map.put("url_expire_sec", 1600);
        map.put("video_thumbnail_process", "video/snapshot,t_0,f_jpg,ar_auto,w_300");
        while (true) {
            HttpResponse response = request.postJson("https://api.aliyundrive.com/v2/file/list", map);
            if (response.getStatus() == 200) {
                return JsonUtils.responseToBean(response, FolderEntity.class);
            }
            // 防止429
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void trashFile(String fileId) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("drive_id", loginInfo.getDefaultDriveId());
        map.put("file_id", fileId);
        HttpResponse response = request.postJson("https://api.aliyundrive.com/v2/recyclebin/trash", map);
        log.info("删除文件：" + response.body());
    }

    public EncFileInfoEntity getFileInfo(String filePath) {
        File file = new File(filePath);
        String name = FileUtil.getName(file);
        long size = FileUtil.size(file);
        String accessToken = loginInfo.getAccessToken();
        JavaScriptEngine instance = JavaScriptEngine.instance();
        try (FileReader fileReader = new FileReader("en.js")) {
            instance.eval(fileReader);
            String limit = (String) instance.invokeFunction("h", instance.invokeFunction("m", accessToken));
            BigInteger bigInteger = new BigInteger(limit.substring(0, 16), 16);
            long left = size == 0 ? size : bigInteger.mod(BigInteger.valueOf(size)).longValueExact();
            long right = Math.min(left + 8, size);
            byte[] bytes = FileUtil.readBytes(filePath);
            int preSize = (int) Math.min(1024, size);
            byte[] preLimit = new byte[preSize];
            byte[] proofCodeLimit = new byte[(int) (right - left)];
            System.arraycopy(bytes, 0, preLimit, 0, preSize);
            System.arraycopy(bytes, (int) left, proofCodeLimit, 0, (int) (right - left));
            String preHash = MD5Util.hashCode(preLimit, MD5Util.SHA1);
            String contentHash = MD5Util.hashCode(new FileInputStream(file), MD5Util.SHA1);
            String proofCode = Base64Encoder.encode(proofCodeLimit);
            return new EncFileInfoEntity(name, size, preHash, contentHash, proofCode);
        } catch (IOException | ScriptException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    public FileExistsEntity fileExists(String parentFileId, EncFileInfoEntity fileInfo) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("drive_id", loginInfo.getDefaultDriveId());
        map.put("part_info_list", FileUtils.getFileParts(fileInfo.getFileSize(), PART_SIZE));
        map.put("parent_file_id", parentFileId);
        map.put("name", fileInfo.getFileName());
        map.put("type", "file");
//        refuse auto_rename
        map.put("check_name_mode", "refuse");
        map.put("size", fileInfo.getFileSize());
        map.put("pre_hash", fileInfo.getPreHash());
        HttpResponse response = request.postJson("https://api.aliyundrive.com/adrive/v2/file/createWithFolders", map);
        return JsonUtils.responseToBean(response, FileExistsEntity.class);
    }

    public FileExistsEntity createFile(String parentFileId, EncFileInfoEntity fileInfo) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("drive_id", loginInfo.getDefaultDriveId());
        map.put("name", fileInfo.getFileName());
        map.put("parent_file_id", parentFileId);
        map.put("type", FILE_TYPE_FILE);
        map.put("part_info_list", FileUtils.getFileParts(fileInfo.getFileSize(), PART_SIZE));
        map.put("check_name_mode", "refuse");
        map.put("size", fileInfo.getFileSize());
        map.put("content_hash", fileInfo.getContentHash());
        map.put("content_hash_name", "sha1");
        map.put("proof_code", fileInfo.getProofCode());
        map.put("proof_version", "v1");
        HttpResponse response = request.postJson("https://api.aliyundrive.com/adrive/v2/file/createWithFolders", map);
        return JsonUtils.responseToBean(response, FileExistsEntity.class);
    }

    public FolderCreateEntity createWithFolders(String parentFileId, String name) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("drive_id", loginInfo.getDefaultDriveId());
        map.put("name", name);
        map.put("parent_file_id", parentFileId);
        map.put("type", FILE_TYPE_FOLDER);
        map.put("check_name_mode", "refuse");
        HttpResponse response = request.postJson("https://api.aliyundrive.com/adrive/v2/file/createWithFolders", map);
        return JsonUtils.responseToBean(response, FolderCreateEntity.class);
    }

    public void startLogin() {
        while (true) {
            DataEntity loginQr = this.getLoginQr();
            if (loginQr == null) {
                return;
            }
            QrUtils.dialogShowQrImg(loginQr.getCodeContent());
            HashMap<String, Object> map = new HashMap<>();
            map.put("ck", loginQr.getCk());
            map.put("t", loginQr.getT());
            ResultEntity resultEntity = this.getQrStatus(map);
            if (resultEntity == null) {
                return;
            }
            if (!AnalyticalResults.isSuccess(resultEntity)) {
                log.error("二维码状态获取失败");
                return;
            }
            String qrCodeStatus = resultEntity.getContent().getData().getQrCodeStatus();
            QrStatus qrStatus = QrStatus.getQrStatusByName(qrCodeStatus);
            if (qrStatus == QrStatus.EXPIRED) {
                log.info("二维码超时，重新获取二维码");
                continue;
            }
            if (qrStatus == QrStatus.CONFIRMED) {
                log.info("登录成功，登录类型：" + resultEntity.getContent().getData().getLoginType());
                String bizExt = resultEntity.getContent().getData().getBizExt();
                LoginEntity loginEntity = JsonUtils.base64ToBean(bizExt, LoginEntity.class);
                if (loginEntity == null) {
                    log.error("错误，获取登录信息失败");
                    return;
                }
                loginInfo = loginEntity.getPdsLoginResult();
                request.getHttpRequest().bearerAuth(loginInfo.getAccessToken());
                log.info("从登录获取的AccessToken获取RefreshToken");
                String refreshToken = getRefreshToken(loginInfo.getAccessToken());
                log.info("获取RefreshToken为：" + refreshToken);
                loginInfo.setRefreshToken(refreshToken);
                log.info("将RefreshToken保存到本地");
                JsonUtils.writeBean(loginInfo, "loginInfo");
                break;
            }
        }
    }

    public void downloadFile(String path, String name, String fileId) {
        File file = new File(path);
        if (!file.exists()) {
            if (file.mkdirs()) {
                log.info("文件夹 " + path + " 不存在自动创建成功");
            }
        }
        String fileName = path + File.separator + name;
        if (EXISTS_FILE_NAMES.contains(fileName)) {
            log.info("该文件已经存在于本地无需下载" + fileName);
            return;
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("drive_id", loginInfo.getDefaultDriveId());
        map.put("file_id", fileId);
        HttpResponse response = request.postJson("https://api.aliyundrive.com/v2/file/get_download_url", map);
        FileDownloadEntity fileDownloadEntity = JsonUtils.responseToBean(response, FileDownloadEntity.class);
        log.info("获取文件成功：" + fileDownloadEntity);
        log.info("下载文件到" + fileName);
        String url = fileDownloadEntity.getUrl();
        //异步请求
        try (FileOutputStream fileOutputStream = new FileOutputStream(fileName)) {
            ResponseBody body = Request.download(url, Headers.of("Referer", "https://www.aliyundrive.com/"));
            if (body != null) {
                fileOutputStream.write(body.bytes());
            }
            log.info("下载完成" + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private final Queue<DownloadEntity> downloadEntities = new LinkedBlockingQueue<>();

    public void download(String parentPath, String parentPathId) {
        FolderEntity folder = getFolderFileList(parentPathId);
        for (ItemsEntity item : folder.getItems()) {
            String path = parentPath + File.separator + item.getName();
            if ("folder".equals(item.getType())) {
                downloadEntities.offer(new DownloadEntity(path, item.getFileId()));
            }
            if ("file".equals(item.getType())) {
                downloadFile(parentPath, item.getName(), item.getFileId());
            }
        }
        DownloadEntity downloadEntity = downloadEntities.poll();
        while (downloadEntity != null) {
            download(downloadEntity.getLocalPath(), downloadEntity.getRemoteId());
            downloadEntity = downloadEntities.poll();
        }
    }

    public FileExistsEntity complete(String fileId, String uploadId) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("drive_id", loginInfo.getDefaultDriveId());
        map.put("file_id", fileId);
        map.put("upload_id", uploadId);
        HttpResponse response = request.postJson("https://api.aliyundrive.com/v2/file/complete", map);
        return JsonUtils.responseToBean(response, FileExistsEntity.class);
    }

    private final HashMap<File, String> fileIdMap = new HashMap<>();

    public String getFileIdByPath(String path) {
        return fileIdMap.get(new File(path));
    }

    public String addFileId(String path, String fileId) {
        log.info("添加文件id文件（夹）" + path + ":" + fileId);
        return fileIdMap.put(new File(path), fileId);
    }

    public String removeFileId(String path) {
        log.info("删除文件id文件（夹）" + path);
        return fileIdMap.remove(new File(path));
    }


    public void uploadSingleFile(String realFileName, String remoteParentId) {
        EncFileInfoEntity fileInfo = getFileInfo(realFileName);
        FileExistsEntity fileExists = fileExists(remoteParentId, fileInfo);
        if ("PreHashMatched".equals(fileExists.getCode())) {
            FileExistsEntity file = createFile(remoteParentId, fileInfo);
            addFileId(realFileName, file.getFileId());
            log.info(realFileName + "快传成功");
        } else {
            List<PartInfoListEntity> partInfoList = fileExists.getPartInfoList();
            if (partInfoList != null) {
                try (FileInputStream fileInputStream = new FileInputStream(realFileName)) {
                    int available = fileInputStream.available();
                    int i = 0;
                    while (available > 0) {
                        byte[] bytes = new byte[Math.min(available, PART_SIZE)];
                        fileInputStream.read(bytes);
                        PartInfoListEntity partInfoListEntity = partInfoList.get(i);
                        Response upload = Request.upload(partInfoListEntity.getUploadUrl(), bytes);
                        int code = upload.code();
                        if (code == 200) {
                            log.info("上传成功：" + partInfoListEntity);
                        } else {
                            log.info("上传失败：" + partInfoListEntity);
                        }
                        if (partInfoList.size() == ++i) {
                            FileExistsEntity complete = complete(fileExists.getFileId(), fileExists.getUploadId());
                            addFileId(realFileName, complete.getFileId());
                            log.info("complete:" + complete);
                            log.info(realFileName + "上传完毕");
                        }
                        available = fileInputStream.available();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void uploadFile(String remoteParentId, String filePath) {
        addFileId(filePath, remoteParentId);
        File file = new File(filePath);
        for (String fileName : Objects.requireNonNull(file.list())) {
            String realFileName = filePath + File.separator + fileName;
            if (FileUtil.isDirectory(realFileName)) {
                FolderCreateEntity withFolders = createWithFolders(remoteParentId, fileName);
                if (withFolders.isExist()) {
                    log.info("该目录已经存在：root" + realFileName.replace(BASE_PATH, ""));
                } else {
                    log.info("服务器创建目录成功：root" + realFileName.replace(BASE_PATH, ""));
                }
                uploadFile(withFolders.getFileId(), realFileName);
            }
            if (FileUtil.isFile(realFileName)) {
                uploadSingleFile(realFileName, remoteParentId);
                EXISTS_FILE_NAMES.add(realFileName);
            }
        }
    }

    public String getFileId(String... path) {
        String fileId = "root";
        for (String name : path) {
            FolderEntity fileList = getFolderFileList(fileId);
            ItemsEntity item = fileList.getItemByName(name);
            if (item == null) {
                FolderCreateEntity withFolders = createWithFolders(fileId, name);
                fileId = withFolders.getFileId();

            } else {
                fileId = item.getFileId();
            }
        }
        return fileId;
    }

    private final LinkedBlockingQueue<WatchRun> watchRuns = new LinkedBlockingQueue<>();

    public String getFolderId(String path) {
        File file = new File(path);
        File file2 = new File(BASE_PATH);
        Stack<String> stack = new Stack<>();
        if (Objects.equals(file, file2)) {
            return REMOTE_FILE_ID;
        }
        do {
            stack.push(file.getName());
            file = file.getParentFile();
        } while (!Objects.equals(file, file2));
        String parentId = REMOTE_FILE_ID;
        String absPath = BASE_PATH;
        while (!stack.isEmpty()) {
            String pop = stack.pop();
            absPath += (File.separator + pop);
            if (FileUtil.isDirectory(absPath)) {
                FolderCreateEntity withFolders = createWithFolders(parentId, pop);
                parentId = withFolders.getFileId();
            } else {
                List<ItemsEntity> itemsEntity = getFolderFileList(parentId).getItems();
                for (ItemsEntity entity : itemsEntity) {
                    if (Objects.equals(pop, entity.getName())) {
                        addFileId(absPath, entity.getFileId());
                        return entity.getFileId();
                    }
                }
            }
            addFileId(absPath, parentId);
        }
        return parentId;
    }

    public void init() {
        fileIdMap.clear();
        EXISTS_FILE_NAMES.clear();
        REMOTE_FILE_ID = getFileId("code", "abc");
        log.info("开始执行双端同步，该操作只执行一次，后续本地文件会向远程同步");
        uploadFile(REMOTE_FILE_ID, BASE_PATH);
        download(BASE_PATH, REMOTE_FILE_ID);
        log.info("双端同步执行完毕，后续只会本地同步到远程");
    }

    private WatchMonitor watchMonitor;

    public void refreshFolderWatch() {
        log.info("刷新文件Watch。。。");
        watchMonitor = WatchMonitor.create(BASE_PATH, 100, WatchMonitor.EVENTS_ALL);
        BaseAliPan baseAliPan = this;
        watchMonitor.watch(new Watcher() {
            @Override
            public void onCreate(WatchEvent<?> event, Path currentPath) {
                watchRuns.add(new WatchRun(event, currentPath));
                watchRuns.poll().run(baseAliPan);
            }

            @Override
            public void onModify(WatchEvent<?> event, Path currentPath) {
                watchRuns.add(new WatchRun(event, currentPath));
                watchRuns.poll().run(baseAliPan);
            }

            @Override
            public void onDelete(WatchEvent<?> event, Path currentPath) {
                watchRuns.add(new WatchRun(event, currentPath));
                watchRuns.poll().run(baseAliPan);
            }

            @Override
            public void onOverflow(WatchEvent<?> event, Path currentPath) {
                watchRuns.add(new WatchRun(event, currentPath));
                watchRuns.poll().run(baseAliPan);
            }
        });
    }

    public void run() {
        File file = new File("loginInfo");
        if (file.exists()) {
            PdsLoginResult loginInfo = JsonUtils.readBean("loginInfo", PdsLoginResult.class);
            String refreshToken = loginInfo.getRefreshToken();
            if (!refreshToken(refreshToken)) {
                startLogin();
            }
        } else {
            startLogin();
        }
        init();
        try {
            log.info("文件监控器开启");
            FileListener.start(BASE_PATH, this);
            log.info("文件监控器开启成功");
        } catch (Exception e) {
            log.info("文件监控器开启失败");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        BaseAliPan baseAliPan = new BaseAliPan();
        baseAliPan.run();

    }
}
