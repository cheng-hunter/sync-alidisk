package com.yxhpy.entity;

import lombok.*;

/**
 * @author liuguohao
 * @date 2021/12/30 17:17
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class DownloadEntity {
    private String localPath;
    private String remoteId;
}
