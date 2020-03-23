package com.example.hasee.bluecalligrapher.encodebase64;

/**
 * Created by hasee on 2018/4/20.
 */

public class EncodeBase64 {
    public static String encodeBase(byte[] bytes){
        final MyBase64 base64=new MyBase64();
        String text=base64.encodeToString(bytes);
        return text;
    }
}
