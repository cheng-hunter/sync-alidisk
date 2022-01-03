package com.yxhpy.entity.response;

import lombok.*;

import java.util.Date;

/**
 * @author liuguohao
 * @date 2021/12/30 15:11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ItemsEntity {
    private String driveId;
    private String domainId;
    private String fileId;
    private String name;
    private String type;
    private Date createdAt;
    private Date updatedAt;
    private boolean hidden;
    private boolean starred;
    private String status;
    private String parentFileId;
    private String encryptMode;
    private String revisionId;
    private String contentType;
    private String fileExtension;
    private String mimeType;
    private String mimeExtension;
    private int size;
    private String userMeta;
    private String uploadId;
    private String crc64Hash;
    private String contentHash;
    private String contentHashName;
    private String downloadUrl;
    private String url;
    private String thumbnail;
    private String category;
    private int punishFlag;
    private String creatorType;
    private String creatorId;
    private String creatorName;
    private String lastModifierType;
    private String lastModifierId;
    private String lastModifierName;

}
