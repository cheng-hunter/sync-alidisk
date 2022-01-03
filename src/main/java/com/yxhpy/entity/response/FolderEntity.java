package com.yxhpy.entity.response;
import lombok.*;

import java.util.List;
import java.util.Objects;

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

    public ItemsEntity getItemByName(String name){
        for (ItemsEntity item : items) {
            if (Objects.equals(item.getName(), name)) {
                return item;
            }
        }
        return null;
    }
}