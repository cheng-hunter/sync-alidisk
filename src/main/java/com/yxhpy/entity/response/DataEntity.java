package com.yxhpy.entity.response;


import lombok.*;

/**
 * @author liuguohao
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class DataEntity {
    private Long t;
    private String codeContent;
    private String qrCodeStatus;
    private String ck;
    private Integer resultCode;
    private String loginResult;
    private String loginSucResultAction;
    private String st;
    private String loginType;
    private String bizExt;
    private String loginScene;
    private String appEntrance;
    private String smartlock;
}
