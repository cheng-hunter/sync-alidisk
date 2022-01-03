package com.yxhpy.utils;

import java.io.File;
import java.nio.file.Path;
import java.util.*;

/**
 * @author liuguohao
 */
public class FileUtils {
    public static long getFilePartsSize(long fileSize, long partSize) {
        if (fileSize % partSize == 0) {
            return fileSize / partSize;
        } else {
            return fileSize / partSize + 1;
        }
    }

    public static List<Map<String, Long>> getFileParts(long fileSize, long partSize) {
        ArrayList<Map<String, Long>> res = new ArrayList<>();
        for (int i = 0; i < getFilePartsSize(fileSize, partSize); i++) {
            HashMap<String, Long> hashMap = new HashMap<>();
            hashMap.put("part_number", i + 1L);
            res.add(hashMap);
        }
        return res;
    }

    public static void main(String[] args) {
        File file = new File("D:\\sync\\新建文件夹1");
        File file2 = new File("D:\\sync");
        Stack<String> stack = new Stack<>();
        if (Objects.equals(file, file2)){
            return;
        }
        do {
            System.out.println(file.getName());
            stack.push(file.getName());
            file = file.getParentFile();
        } while (!Objects.equals(file, file2));
        System.out.println(stack);
    }

}
