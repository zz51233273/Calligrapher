package com.example.hasee.bluecalligrapher.setting;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.hasee.bluecalligrapher.R;
import com.example.hasee.bluecalligrapher.adapter.MyFragmentPagerAdapterMessage;


/**
 * Created by hasee on 2018/5/28.
 */

public class UserMessageActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener,
        ViewPager.OnPageChangeListener{
    private ViewPager vpager;
    //几个代表页面的常量
    private final int PAGE_ONE = 0;
    private final int PAGE_TWO = 1;
    private final int PAGE_THREE = 2;
    private RadioButton rb1,rb2,rb3;
    private MyFragmentPagerAdapterMessage mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_message);
        init();
    }

    private void init(){
        Typeface typeface=Typeface.createFromAsset(getAssets(),"fonts/STXINGKA.TTF");//改变字体
        ((TextView)findViewById(R.id.title_name)).setTypeface(typeface);
        rb1=(RadioButton)findViewById(R.id.rb_letter);
        rb2=(RadioButton)findViewById(R.id.rb_comment);
        rb3=(RadioButton)findViewById(R.id.rb_inform);
        ((RadioGroup)findViewById(R.id.rg_tab_bar)).setOnCheckedChangeListener(this);
        mAdapter=new MyFragmentPagerAdapterMessage(getSupportFragmentManager());
        vpager=(ViewPager)findViewById(R.id.vpager);
        vpager.setAdapter(mAdapter);
        vpager.setCurrentItem(PAGE_ONE);
        rb1.setChecked(true);
        vpager.addOnPageChangeListener(this);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId){
        switch (checkedId) {
            case R.id.rb_letter:
                vpager.setCurrentItem(PAGE_ONE);
                break;
            case R.id.rb_comment:
                vpager.setCurrentItem(PAGE_TWO);
                break;
            case R.id.rb_inform:
                vpager.setCurrentItem(PAGE_THREE);
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
                case PAGE_ONE:
                    rb1.setChecked(true);
                    break;
                case PAGE_TWO:
                    rb2.setChecked(true);
                    break;
                case PAGE_THREE:
                    rb3.setChecked(true);
                    break;
            }
        }
    }


}
