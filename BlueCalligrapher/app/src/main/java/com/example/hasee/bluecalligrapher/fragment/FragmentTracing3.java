package com.example.hasee.bluecalligrapher.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.example.hasee.bluecalligrapher.R;
import com.example.hasee.bluecalligrapher.utils.ImageUtil;
import com.example.hasee.bluecalligrapher.write_field_character.FieldCharacterShapeActivity;


/**
 * Created by hasee on 2018/3/30.
 */

public class FragmentTracing3 extends Fragment {
    private View view;
    private ImageView tracing_write;    //手写图标
    private ImageView showImg;
    public FragmentTracing3(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view=inflater.inflate(R.layout.fg_tracing_styles,container, false);
        init();
        return view;
    }
    public void setImage(byte[] image){
        if(image!=null){
            //将字节数组转化为位图
            Bitmap imagebitmap= BitmapFactory.decodeByteArray(image,0,image.length);
            if(null!=showImg)
                showImg.setImageBitmap(imagebitmap);
        }
    }
    private void init(){
        showImg=(ImageView)view.findViewById(R.id.photo_char);
        tracing_write=(ImageView)view.findViewById(R.id.fg_tracing_write);
        if(null!=Fragment2.tracings[2]){
            setImage(Fragment2.tracings[2].getPicture());
        }
        tracing_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(), FieldCharacterShapeActivity.class);
                if(null!=showImg.getDrawable()){
                    Bitmap image = ((BitmapDrawable)showImg.getDrawable()).getBitmap();
                    byte[] img_bytes= ImageUtil.getImgBytes(image);
                    intent.putExtra("img_bytes",img_bytes);
                }
                startActivity(intent);
            }
        });
        //隐藏键盘
        view.findViewById(R.id.fg_tracing_styles_layout).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });
    }
}
