package com.example.hasee.bluecalligrapher.encodebase64;

/**
 * Created by hasee on 2018/4/20.
 */

public interface MyBinaryEncoder extends MyEncoder{
    byte[] encode(byte[] var1) throws MyEncoderException;
}
