package com.yxhpy.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author liuguohao
 */
public class FileUtils {
    public static long getFilePartsSize(long fileSize, long partSize){
        if (fileSize % partSize == 0) {
            return fileSize / partSize;
        } else {
            return fileSize / partSize + 1;
        }
    }
    public static List<Map<String, Long>> getFileParts(long fileSize, long partSize){
        ArrayList<Map<String, Long>> res = new ArrayList<>();
        for (int i = 0; i < getFilePartsSize(fileSize, partSize); i++) {
            HashMap<String, Long> hashMap = new HashMap<>();
            hashMap.put("part_number", i + 1L);
            res.add(hashMap);
        }
        return res;
    }
}
