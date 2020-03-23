package com.example.hasee.bluecalligrapher.item;

import android.graphics.Bitmap;

/**
 * Created by hasee on 2018/4/8.
 */

public class PoetryItem {
    String name;    //诗名
    Bitmap pic;     //作品缩略图

    public Bitmap getPic() {
        return pic;
    }

    public void setPic(Bitmap pic) {
        this.pic = pic;
    }

    public PoetryItem(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
