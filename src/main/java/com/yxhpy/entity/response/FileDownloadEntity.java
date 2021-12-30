package com.yxhpy.entity.response;

import lombok.*;

import java.util.Date;

/**
 * @author liuguohao
 * @date 2021/12/30 17:42
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class FileDownloadEntity {
    private String method;
    private String url;
    private String internalUrl;
    private Date expiration;
    private int size;
    private RateLimitEntity rateLimit;
    private String crc64Hash;
    private String contentHash;
    private String contentHashName;
}
