package com.yxhpy.entity.response;

import lombok.*;

/**
 * @author liuguohao
 * @date 2022/1/1 17:49
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class FolderCreateEntity {
    private String parentFileId;
    private String type;
    private String fileId;
    private String domainId;
    private String driveId;
    private String fileName;
    private String encryptMode;
    private String status;
    private boolean exist;
}
