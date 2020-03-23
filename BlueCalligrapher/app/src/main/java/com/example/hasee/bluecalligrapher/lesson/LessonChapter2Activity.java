package com.example.hasee.bluecalligrapher.lesson;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.hasee.bluecalligrapher.R;

/**
 * Created by hasee on 2018/9/21.
 */

public class LessonChapter2Activity extends AppCompatActivity implements View.OnClickListener{
    private Dialog dialog;
    private ImageView mImageView;   //大图显示
    private ImageView img0,img1,img2,img3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lesson_chapter2);
        init();
    }
    private void init(){
        dialog = new Dialog(this,R.style.Theme_AppCompat);
        (findViewById(R.id.back)).setOnClickListener(this);
        ((ImageView) findViewById(R.id.watch_click)).setOnClickListener(this);
        initTextStyle();
    }

    @Override
    public void onClick(View v){
        int id=v.getId();
        Intent i=null;
        switch (id){
            case R.id.watch_click:
                /*i=new Intent(this,LessonVideoActivity.class);
                i.putExtra("chapter","2");
                startActivity(i);*/
                break;
            case R.id.img0:
                getImageView(img0);
                break;
            case R.id.img1:
                getImageView(img1);
                break;
            case R.id.img2:
                getImageView(img2);
                break;
            case R.id.img3:
                getImageView(img3);
            case R.id.back:
                finish();
                break;
        }
    }

    private void initTextStyle(){
        AssetManager mgr=getAssets();   //得到AssetManager
        Typeface typeface=Typeface.createFromAsset(mgr,"fonts/STXINGKA.TTF");//改变字体
        ((TextView)findViewById(R.id.title_chapter)).setTypeface(typeface);
        ((TextView)findViewById(R.id.title)).setTypeface(typeface);
        ((TextView)findViewById(R.id.title_chapter2_2_1)).setTypeface(typeface);
        ((TextView)findViewById(R.id.title_chapter2_3_1)).setTypeface(typeface);
        ((TextView)findViewById(R.id.title_chapter2_4_1)).setTypeface(typeface);
        ((TextView)findViewById(R.id.title_chapter2_5_1)).setTypeface(typeface);
        ((TextView)findViewById(R.id.content_chapter2_2)).setTypeface(typeface);
        ((TextView)findViewById(R.id.content_chapter2_3)).setTypeface(typeface);
        ((TextView)findViewById(R.id.content_chapter2_4)).setTypeface(typeface);
        ((TextView)findViewById(R.id.content_chapter2_5)).setTypeface(typeface);
        img0=((ImageView)findViewById(R.id.img0));
        img0.setOnClickListener(this);
        img1=((ImageView)findViewById(R.id.img1));
        img1.setOnClickListener(this);
        img2=((ImageView)findViewById(R.id.img2));
        img2.setOnClickListener(this);
        img3=((ImageView)findViewById(R.id.img3));
        img3.setOnClickListener(this);
    }

    //动态的ImageView
    private void getImageView(ImageView imageView){
        mImageView= new ImageView(this);
        //宽高
        mImageView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        //设置Padding
        mImageView.setPadding(20,20,20,20);
        mImageView.setImageDrawable(imageView.getDrawable());
        dialog.setContentView(mImageView);
        dialog.show();
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
}
