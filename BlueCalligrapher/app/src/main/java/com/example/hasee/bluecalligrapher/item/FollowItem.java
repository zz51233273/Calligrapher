package com.example.hasee.bluecalligrapher.item;

import android.graphics.Bitmap;


/**
 * Created by hasee on 2018/6/16.
 */

public class FollowItem {
    private Bitmap followed_head;   //关注人头像
    private String followed_name;   //关注人姓名
    private String followed;        //关注人手机号

    public Bitmap getFollower_head() {
        return followed_head;
    }

    public void setFollower_head(Bitmap followed_head) {
        this.followed_head = followed_head;
    }

    public String getFollower_name() {
        return followed_name;
    }

    public void setFollower_name(String followed_name) {
        this.followed_name = followed_name;
    }

    public String getfollowed() {
        return followed;
    }

    public void setfollowed(String followed) {
        this.followed = followed;
    }
}
