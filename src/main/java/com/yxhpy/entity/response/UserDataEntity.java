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
public class UserDataEntity {
    private String DingDingRobotUrl;
    private String EncourageDesc;
    private boolean FeedBackSwitch;
    private String FollowingDesc;
    private String ding_ding_robot_url;
    private String encourage_desc;
    private boolean feed_back_switch;
    private String following_desc;
    private String share;
}