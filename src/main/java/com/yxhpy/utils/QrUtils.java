package com.yxhpy.utils;

import cn.hutool.core.lang.Console;
import cn.hutool.extra.qrcode.QrCodeUtil;
import com.google.zxing.common.BitMatrix;

import javax.swing.*;
import java.awt.*;

/**
 * @author liuguohao
 */
public class QrUtils {
    public static void getQrcodeImg(String url) {
        com.google.zxing.common.BitMatrix encode = QrCodeUtil.encode(url, 10, 10);
        int height = encode.getHeight();
        int width = encode.getWidth();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (!encode.get(i, j)) {
                    // white
                    sb.append("\033[47m  \033[0m");
                } else {
                    sb.append("\033[30m  \033[0;39m");
                }
            }
            sb.append("\n");
        }
        Console.log("扫描二维码开始登录");
        Console.log(sb.toString());
    }

    public static void dialogShowQrImg(String url) {
        Thread t = new Thread(() -> {
            byte[] bytes = QrCodeUtil.generatePng(url, 200, 200);
            ImageIcon icon = new ImageIcon(bytes);
            Image image = icon.getImage();
            float scale = 1;
            int width = Math.round(icon.getIconWidth() * scale);
            int height = Math.round(icon.getIconHeight() * scale);
            Image miniIcon = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            ImageIcon smallIcon = new ImageIcon(miniIcon);
            JOptionPane.showMessageDialog(null, "", "扫描二维码登录", 0, smallIcon);
        });
        t.setDaemon(true);
        t.start();
    }

}

