package com.example.hasee.bluecalligrapher.bean;


/**
 * Created by hasee on 2018/4/3.
 */

public class Tracing {  //临摹字体类
    private String character;   //字名
    private byte[] picture;    //图片的二进制流
    private String id;             //汉字编号
    public byte[] getPicture() {
        return picture;
    }

    public void setPicture(byte[] picture) {
        this.picture = picture;
    }

    public String getCharacter() {
        return character;
    }

    public void setCharacter(String character) {
        this.character = character;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
