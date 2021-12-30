package com.yxhpy.entity.response;


import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ResponseEntity {
    private DataEntity data;
    private Integer status;
    private Boolean success;
}
