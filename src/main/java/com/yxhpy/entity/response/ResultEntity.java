package com.yxhpy.entity.response;


import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ResultEntity {
    private ResponseEntity content;
    private boolean hasError;
}
