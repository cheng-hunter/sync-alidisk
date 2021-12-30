package com.yxhpy.entity.response;
import lombok.*;

import java.util.List;

/**
 * @author liuguohao
 * @date 2021/12/30 15:09
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class FolderEntity {
    private List<ItemsEntity> items;
    private String nextMarker;
    private int punishedFileCount;
}