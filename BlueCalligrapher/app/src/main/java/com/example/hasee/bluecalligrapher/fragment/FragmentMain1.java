package com.example.hasee.bluecalligrapher.fragment;

import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.hasee.bluecalligrapher.R;


/**
 * Created by hasee on 2018/4/12.
 */

public class FragmentMain1 extends Fragment{
    public FragmentMain1(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view=inflater.inflate(R.layout.fg_main_personal,container, false);
        init(view);
        return view;
    }
    private void init(View view){
        AssetManager mgr=getContext().getAssets();   //得到AssetManager
        Typeface typeface=Typeface.createFromAsset(mgr,"fonts/simhei.ttf");//改变字体
        ((TextView)view.findViewById(R.id.text_1)).setTypeface(typeface);
        ((TextView)view.findViewById(R.id.text_2)).setTypeface(typeface);
        ((TextView)view.findViewById(R.id.text_3)).setTypeface(typeface);
        ((TextView)view.findViewById(R.id.text_4)).setTypeface(typeface);
    }
}
