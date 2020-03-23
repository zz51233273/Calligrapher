package com.example.hasee.bluecalligrapher.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.hasee.bluecalligrapher.R;
import com.example.hasee.bluecalligrapher.setting.UserStoreActivity;


/**
 * Created by hasee on 2018/4/6.
 */

public class FragmentStore3 extends Fragment{
    private ImageView storeImg;
    public FragmentStore3(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view=inflater.inflate(R.layout.fg_store,container, false);
        storeImg=(ImageView)view.findViewById(R.id.store_photo_char);
        if(null!= UserStoreActivity.storeTracings[2]){
            setImage(UserStoreActivity.storeTracings[2].getPicture());
        }
        return view;
    }
    public void setImage(byte[] image){
        if(image!=null){
            //将字节数组转化为位图
            Bitmap imagebitmap= BitmapFactory.decodeByteArray(image,0,image.length);
            if(null!=storeImg){
                storeImg.setImageBitmap(imagebitmap);
            }
        }
    }
}
