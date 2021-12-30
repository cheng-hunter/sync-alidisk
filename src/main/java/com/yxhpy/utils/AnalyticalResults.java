package com.yxhpy.utils;

import com.yxhpy.entity.response.ResultEntity;

/**
 * @author liuguohao
 */
public class AnalyticalResults {
    public static boolean isSuccess(ResultEntity entity){
        try {
            return !entity.isHasError() && entity.getContent().getSuccess();
        } catch (Exception e){
            return false;
        }
    }
}
