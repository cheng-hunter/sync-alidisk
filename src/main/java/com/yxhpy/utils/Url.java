package com.yxhpy.utils;

/**
 * @author liuguohao
 * @date 2021/12/30 23:12
 */
public class Url {
    public static String percentEncode(String encodeMe) {
        if (encodeMe == null) {
            return"";
        }
        String encoded = encodeMe.replace("%","%25");
        encoded = encoded.replace("","%20");
        encoded = encoded.replace("!","%21");
        encoded = encoded.replace("#","%23");
        encoded = encoded.replace("$","%24");
        encoded = encoded.replace("&","%26");
        encoded = encoded.replace("'","%27");
        encoded = encoded.replace("(","%28");
        encoded = encoded.replace(")","%29");
        encoded = encoded.replace("*","%2A");
        encoded = encoded.replace("+","%2B");
        encoded = encoded.replace(",","%2C");
        encoded = encoded.replace("/","%2F");
        encoded = encoded.replace(":","%3A");
        encoded = encoded.replace(";","%3B");
        encoded = encoded.replace("=","%3D");
        encoded = encoded.replace("?","%3F");
        encoded = encoded.replace("@","%40");
        encoded = encoded.replace("[","%5B");
        encoded = encoded.replace("]","%5D");
        return encoded;
    }

    /**
     * Percent-decodes a string, such as used in a URL Path (not a query string / form encode, which uses + for spaces, etc)
     */
    public static String percentDecode(String encodeMe) {
        if (encodeMe == null) {
            return"";
        }
        String decoded = encodeMe.replace("%21","!");
        decoded = decoded.replace("%20","");
        decoded = decoded.replace("%23","#");
        decoded = decoded.replace("%24","$");
        decoded = decoded.replace("%26","&");
        decoded = decoded.replace("%27","'");
        decoded = decoded.replace("%28","(");
        decoded = decoded.replace("%29",")");
        decoded = decoded.replace("%2A","*");
        decoded = decoded.replace("%2B","+");
        decoded = decoded.replace("%2C",",");
        decoded = decoded.replace("%2F","/");
        decoded = decoded.replace("%3A",":");
        decoded = decoded.replace("%3B",";");
        decoded = decoded.replace("%3D","=");
        decoded = decoded.replace("%3F","?");
        decoded = decoded.replace("%40","@");
        decoded = decoded.replace("%5B","[");
        decoded = decoded.replace("%5D","]");
        decoded = decoded.replace("%25","%");
        return decoded;
    }
}
