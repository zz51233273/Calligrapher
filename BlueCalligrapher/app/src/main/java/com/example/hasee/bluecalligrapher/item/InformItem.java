package com.example.hasee.bluecalligrapher.item;

import android.graphics.Bitmap;

/**
 * Created by hasee on 2018/5/29.
 */

public class InformItem {
    private String phonenumber;     //用户人手机号
    private Bitmap headId;          //通知人头像(图片)
    private String username;        //通知人
    private String context;         //通知标题
    private int dyId;               //通知动态id
    private String dy_context;      //通知动态内容
    private String time;            //通知时间

    public InformItem(String phonenumber,Bitmap headId, String username, String context, int dyId, String dy_context, String time) {
        this.phonenumber=phonenumber;
        this.headId = headId;
        this.username = username;
        this.context = context;
        this.dyId = dyId;
        this.dy_context = dy_context;
        this.time=time;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public Bitmap getHeadId() {
        return headId;
    }

    public void setHeadId(Bitmap headId) {
        this.headId = headId;
    }

    public String getDy_context() {
        return dy_context;
    }

    public void setDy_context(String dy_context) {
        this.dy_context = dy_context;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public int getDyId() {
        return dyId;
    }

    public void setDyId(int dyId) {
        this.dyId = dyId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
