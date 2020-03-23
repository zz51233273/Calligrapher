package com.example.hasee.bluecalligrapher.fragment;

import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.hasee.bluecalligrapher.R;
import com.example.hasee.bluecalligrapher.adapter.MyFragmentPagerAdapterMain;
import com.example.hasee.bluecalligrapher.main.MainActivity;


/**
 * Created by hasee on 2018/3/30.
 */

public class Fragment3 extends Fragment implements RadioGroup.OnCheckedChangeListener,
        ViewPager.OnPageChangeListener{

    private RadioButton rb1;
    private RadioButton rb2;
    private RadioGroup rg_tab_bar;
    private ViewPager vpager;
    private MyFragmentPagerAdapterMain mAdapter;

    public Fragment3(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view=inflater.inflate(R.layout.fg_main,container, false);
        init(view);
        return view;
    }
    private void init(View view){
        rg_tab_bar=(RadioGroup)view.findViewById(R.id.rg_main_bar);
        rg_tab_bar.setOnCheckedChangeListener(this);
        rb1=(RadioButton)view.findViewById(R.id.rb_main_personal);
        rb2=(RadioButton)view.findViewById(R.id.rb_main_dynamic);
        mAdapter=new MyFragmentPagerAdapterMain(getChildFragmentManager());
        vpager=(ViewPager)view.findViewById(R.id.main_vpager);
        vpager.setAdapter(mAdapter);
        vpager.addOnPageChangeListener(this);
        AssetManager mgr=getContext().getAssets();   //得到AssetManager
        Typeface typeface=Typeface.createFromAsset(mgr,"fonts/simhei.ttf");//改变字体
        rb1.setTypeface(typeface);
        rb2.setTypeface(typeface);
        rb1.setChecked(true);
    }
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_main_personal:
                vpager.setCurrentItem(MainActivity.PAGE_ONE);
                break;
            case R.id.rb_main_dynamic:
                vpager.setCurrentItem(MainActivity.PAGE_TWO);
                break;
        }
    }

    //重写ViewPager页面切换的处理方法
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
    }
    @Override
    public void onPageScrollStateChanged(int state) {
        //state的状态有三个，0表示什么都没做，1正在滑动，2滑动完毕
        if (state == 2) {
            switch (vpager.getCurrentItem()) {
                case MainActivity.PAGE_ONE:
                    rb1.setChecked(true);
                    break;
                case MainActivity.PAGE_TWO:
                    rb2.setChecked(true);
                    break;
            }
        }
    }
}
