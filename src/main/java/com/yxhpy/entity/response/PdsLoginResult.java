package com.yxhpy.entity.response;
import lombok.*;

import java.util.List;
import java.util.Date;

/**
 * @author liuguohao
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class PdsLoginResult {
    private String role;
    private UserDataEntity userData;
    private boolean isFirstLogin;
    private boolean needLink;
    private String loginType;
    private String nickName;
    private boolean needRpVerify;
    private String avatar;
    private String accessToken;
    private String userName;
    private String userId;
    private String defaultDriveId;
    private List<String> existLink;
    private int expiresIn;
    private Date expireTime;
    private String requestId;
    private boolean dataPinSetup;
    private String state;
    private String tokenType;
    private boolean dataPinSaved;
    private String refreshToken;
    private String status;
}
