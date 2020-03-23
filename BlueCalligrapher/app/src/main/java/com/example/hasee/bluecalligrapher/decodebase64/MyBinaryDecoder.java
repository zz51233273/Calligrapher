package com.example.hasee.bluecalligrapher.decodebase64;

/**
 * Created by hasee on 2018/4/3.
 */

public interface MyBinaryDecoder extends MyDecoder{
    byte[] decode(byte[] var1) throws MyDecoderException;
}
