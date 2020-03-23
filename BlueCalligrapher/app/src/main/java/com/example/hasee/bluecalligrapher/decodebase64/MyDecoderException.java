package com.example.hasee.bluecalligrapher.decodebase64;

/**
 * Created by hasee on 2018/4/3.
 */

public class MyDecoderException extends Exception{
    private static final long serialVersionUID = 1L;

    public MyDecoderException() {
    }

    public MyDecoderException(String message) {
        super(message);
    }

    public MyDecoderException(String message, Throwable cause) {
        super(message, cause);
    }

    public MyDecoderException(Throwable cause) {
        super(cause);
    }
}
