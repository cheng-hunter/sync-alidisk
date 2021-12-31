package com.yxhpy.entity;

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
public class EncFileInfoEntity {
    private String fileName;
    private Long fileSize;
    private String preHash;
    private String contentHash;
    private String proofCode;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getPreHash() {
        return preHash;
    }

    public void setPreHash(String preHash) {
        this.preHash = preHash;
    }

    public String getContentHash() {
        return contentHash;
    }

    public void setContentHash(String contentHash) {
        this.contentHash = contentHash;
    }

    public String getProofCode() {
        return proofCode;
    }

    public void setProofCode(String proofCode) {
        this.proofCode = proofCode;
    }
}
