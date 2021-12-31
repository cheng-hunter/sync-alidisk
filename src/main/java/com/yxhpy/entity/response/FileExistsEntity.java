package com.yxhpy.entity.response;

import lombok.*;

import java.util.List;

/**
 * @author liuguohao
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class FileExistsEntity {
    private String parentFileId;
    private List<PartInfoListEntity> partInfoList;
    private String uploadId;
    private boolean rapidUpload;
    private String type;
    private String fileId;
    private String domainId;
    private String driveId;
    private String fileName;
    private String encryptMode;
    private String location;
}
