package com.yxhpy.entity.response;


import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class PartInfoListEntity {
    private int partNumber;
    private String uploadUrl;
    private String internalUploadUrl;
    private String contentType;
}
