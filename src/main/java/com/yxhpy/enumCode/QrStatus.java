package com.yxhpy.enumCode;

import java.util.Objects;

/**
 * @author liuguohao
 */

public enum QrStatus {
    NEW("等待扫码登录"),
    SCANED("请到手机端确认登录"),
    EXPIRED("二维码失效"),
    CONFIRMED("登录成功");
    private String des;
    QrStatus(String des) {
        this.des = des;
    }
    public String getDes() {
        return des;
    }
    public static QrStatus getQrStatusByName(String name){
        QrStatus[] values = QrStatus.values();
        for (QrStatus value : values) {
            String nameStr = value.name();
            if (Objects.equals(nameStr, name)) {
                return value;
            }
        }
        return null;
    }
}
