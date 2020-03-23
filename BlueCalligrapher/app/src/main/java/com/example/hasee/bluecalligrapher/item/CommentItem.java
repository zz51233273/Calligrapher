package com.example.hasee.bluecalligrapher.item;

import android.graphics.Bitmap;

/**
 * Created by hasee on 2018/4/30.
 */

public class CommentItem {
    private int id; //评论id
    private Bitmap head;   //评论作者头像
    private String writer;  //评论作者
    private String context; //评论内容
    private int dyId;   //动态id

    public CommentItem(int id, Bitmap head, String writer, String context, int dyId) {
        this.id = id;
        this.head = head;
        this.writer = writer;
        this.context = context;
        this.dyId = dyId;
    }

    public CommentItem(Bitmap head, String writer, String context, int dyId) {
        this.head = head;
        this.writer = writer;
        this.context = context;
        this.dyId = dyId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
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

    public Bitmap getHead() {
        return head;
    }

    public void setHead(Bitmap head) {
        this.head = head;
    }
}
