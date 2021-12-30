package com.yxhpy;
import cn.hutool.http.HttpResponse;
import cn.hutool.log.GlobalLogFactory;
import cn.hutool.log.Log;
import com.yxhpy.entity.response.DataEntity;
import com.yxhpy.entity.response.LoginEntity;
import com.yxhpy.entity.response.PdsLoginResult;
import com.yxhpy.entity.response.ResultEntity;
import com.yxhpy.enumCode.QrStatus;
import com.yxhpy.utils.AnalyticalResults;
import com.yxhpy.utils.JsonUtils;
import com.yxhpy.utils.QrUtils;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author liuguohao
 */
public class BaseAliPan {
    private final Request request;
    private LoginEntity loginInfo;
    private static final String R_CODE = "code=(\\w+)";
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

    public void getRefreshLoginStatus(String token) {

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
        LoginEntity loginEntity = JsonUtils.responseToBean(response, LoginEntity.class);
        PdsLoginResult pdsLoginResult = loginEntity.getPdsLoginResult();
        if (pdsLoginResult == null){
            log.warn("token已经过期，请重新登录:" + response.body());
            return false;
        }
        this.request.getHeaders().put("authorization", pdsLoginResult.getTokenType() + " " + pdsLoginResult.getAccessToken());
        log.info("免密登录成功，无需重新扫码");
        return true;
    }


    public void getFileList(String parentFileId) {
        String defaultDriveId = loginInfo.getPdsLoginResult().getDefaultDriveId();
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
        HttpResponse response = request.postJson("https://api.aliyundrive.com/v2/file/list", map);
        System.out.println(response.body());
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
                loginInfo = loginEntity;
                PdsLoginResult pdsLoginResult = loginInfo.getPdsLoginResult();
                request.getHeaders().put("authorization", pdsLoginResult.getTokenType() + " " + pdsLoginResult.getAccessToken());
                log.info("从登录获取的AccessToken获取RefreshToken");
                String refreshToken = getRefreshToken(pdsLoginResult.getAccessToken());
                log.info("获取RefreshToken为：" + refreshToken);
                loginInfo.getPdsLoginResult().setRefreshToken(refreshToken);
                log.info("将RefreshToken保存到本地");
                JsonUtils.writeBean(loginInfo, "loginInfo");
//                getFileList("root");
                break;
            }
        }
    }
    public void run(){
        PdsLoginResult loginInfo = JsonUtils.readBean("loginInfo", PdsLoginResult.class);
        String refreshToken = loginInfo.getRefreshToken();
        if (!refreshToken(refreshToken)) {
            startLogin();
        }
    }

    public static void main(String[] args) {
        BaseAliPan baseAliPan = new BaseAliPan();
        baseAliPan.run();
    }
}
