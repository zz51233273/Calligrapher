package com.example.hasee.bluecalligrapher.item;

import android.graphics.Bitmap;

/**
 * Created by hasee on 2018/4/4.
 */

public class DynamicsItem {
    private String phonenumber; //作者手机号
    private Bitmap headId;     //作者头像(图片)
    private int dynamicId;     //动态id
    private String writerName;  //作者名
    private String content;     //内容
    private String time;        //发表时间
    private Bitmap img0;private Bitmap img1;private Bitmap img2;
    private Bitmap img3;private Bitmap img4;private Bitmap img5;
    private boolean isFocus=false;  //是否点赞
    private int focusCount;     //点赞人数
    private int commentCount;   //评论人数
    public DynamicsItem(int dynamicId,Bitmap headId, String writerName, String content,String time,Bitmap img0,Bitmap img1,Bitmap img2,Bitmap img3,Bitmap img4,Bitmap img5) {
        this.dynamicId=dynamicId;
        this.headId = headId;
        this.writerName = writerName;
        this.content = content;
        this.time=time;
        this.img0=img0;this.img1=img1;this.img2=img2;
        this.img3=img3;this.img4=img4;this.img5=img5;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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

    public String getWriterName() {
        return writerName;
    }

    public String getContent() {
        return content;
    }

    public Bitmap getImg0() {
        return img0;
    }

    public Bitmap getImg1() {
        return img1;
    }

    public Bitmap getImg2() {
        return img2;
    }

    public Bitmap getImg3() {
        return img3;
    }


    public Bitmap getImg4() {
        return img4;
    }


    public Bitmap getImg5() {
        return img5;
    }

    public boolean isFocus() {
        return isFocus;
    }

    public void setFocus(boolean focus) {
        isFocus = focus;
    }

    public int getDynamicId() {
        return dynamicId;
    }

    public int getFocusCount() {
        return focusCount;
    }

    public void setFocusCount(int focusCount) {
        this.focusCount = focusCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }
}
