package com.example.hasee.bluecalligrapher.decodebase64;

/**
 * Created by hasee on 2018/4/7.
 */

public class DecodeBase64 {
    public static byte[] decodeBase(String text){
        final MyBase64 base64 = new MyBase64();
        final byte[] bytes=base64.decode(text);
        return bytes;
    }
}
