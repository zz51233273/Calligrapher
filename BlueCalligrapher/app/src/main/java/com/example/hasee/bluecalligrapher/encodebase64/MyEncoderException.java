package com.example.hasee.bluecalligrapher.encodebase64;

/**
 * Created by hasee on 2018/4/20.
 */

public class MyEncoderException extends Exception{
    private static final long serialVersionUID = 1L;

    public MyEncoderException() {
    }

    public MyEncoderException(String message) {
        super(message);
    }

    public MyEncoderException(String message, Throwable cause) {
        super(message, cause);
    }

    public MyEncoderException(Throwable cause) {
        super(cause);
    }
}
