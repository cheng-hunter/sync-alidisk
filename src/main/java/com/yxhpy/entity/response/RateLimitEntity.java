package com.yxhpy.entity.response;

import lombok.*;

/**
 * @author liuguohao
 * @date 2021/12/30 17:43
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class RateLimitEntity {
    private int partSpeed;
    private int partSize;
}
