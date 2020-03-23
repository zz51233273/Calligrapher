package com.example.hasee.bluecalligrapher.fragment;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.hasee.bluecalligrapher.R;
import com.example.hasee.bluecalligrapher.lesson.LessonChapter1Activity;
import com.example.hasee.bluecalligrapher.lesson.LessonChapter2Activity;
import com.example.hasee.bluecalligrapher.lesson.LessonChapter3Activity;


/**
 * Created by hasee on 2018/3/30.
 */

public class Fragment1 extends Fragment implements View.OnClickListener{
    public Fragment1(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view=inflater.inflate(R.layout.fg_lesson,container, false);
        init(view);
        return view;
    }

    void init(View view){
        ((LinearLayout) view.findViewById(R.id.lesson_chapter1)).setOnClickListener(this);
        ((LinearLayout) view.findViewById(R.id.lesson_chapter2)).setOnClickListener(this);
        ((LinearLayout) view.findViewById(R.id.lesson_chapter3)).setOnClickListener(this);
        ((LinearLayout) view.findViewById(R.id.lesson_chapter4)).setOnClickListener(this);
        ((LinearLayout) view.findViewById(R.id.lesson_chapter5)).setOnClickListener(this);
        AssetManager mgr=getContext().getAssets();   //得到AssetManager
        Typeface typeface=Typeface.createFromAsset(mgr,"fonts/STXINWEI.TTF");//改变字体
        ((TextView)view.findViewById(R.id.lesson_chapter1_text)).setTypeface(typeface);
        ((TextView)view.findViewById(R.id.lesson_chapter2_text)).setTypeface(typeface);
        ((TextView)view.findViewById(R.id.lesson_chapter3_text)).setTypeface(typeface);
        ((TextView)view.findViewById(R.id.lesson_chapter4_text)).setTypeface(typeface);
        ((TextView)view.findViewById(R.id.lesson_chapter5_text)).setTypeface(typeface);
    }

    @Override
    public void onClick(View v){
        int id=v.getId();
        Intent i=null;
        switch (id){
            case R.id.lesson_chapter1:
                i=new Intent(getContext(), LessonChapter1Activity.class);
                break;
            case R.id.lesson_chapter2:
                i=new Intent(getContext(), LessonChapter2Activity.class);
                break;
            case R.id.lesson_chapter3:
                i=new Intent(getContext(), LessonChapter3Activity.class);
                break;
        }
        if(null!=i)
            getContext().startActivity(i);
    }
}
