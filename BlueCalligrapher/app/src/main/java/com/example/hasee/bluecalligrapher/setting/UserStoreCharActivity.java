package com.example.hasee.bluecalligrapher.setting;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.hasee.bluecalligrapher.R;
import com.example.hasee.bluecalligrapher.adapter.MyFragmentPagerAdapterStore;
import com.example.hasee.bluecalligrapher.main.MainActivity;

/**
 * Created by hasee on 2018/4/6.
 * 显示某个已收藏的文字的四种字体
 */

public class UserStoreCharActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener,
        ViewPager.OnPageChangeListener,View.OnClickListener{
    private RadioButton rb1,rb2,rb3,rb4;
    private RadioGroup rg_store_bar;
    private ViewPager vpager;
    private MyFragmentPagerAdapterStore mAdapter;
    private ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_store_char);
        init();
    }
    private void init(){
        back=(ImageView)findViewById(R.id.store_back_char_home);
        back.setOnClickListener(this);
        rg_store_bar=(RadioGroup)findViewById(R.id.rg_store_bar);
        rb1=(RadioButton)findViewById(R.id.rb_store_jian);
        rb2=(RadioButton)findViewById(R.id.rb_store_fan);
        rb3=(RadioButton)findViewById(R.id.rb_store_xing);
        rb4=(RadioButton)findViewById(R.id.rb_store_cao);
        rg_store_bar.setOnCheckedChangeListener(this);
        mAdapter=new MyFragmentPagerAdapterStore(getSupportFragmentManager());
        vpager=(ViewPager)findViewById(R.id.vpager3);
        vpager.setAdapter(mAdapter);
        vpager.setCurrentItem(0);
        vpager.addOnPageChangeListener(this);
    }
    @Override
    public void onClick(View v){
        int id=v.getId();
        switch (id){
            case R.id.store_back_char_home:
                finish();
                break;
        }
    }
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_store_jian:
                vpager.setCurrentItem(MainActivity.PAGE_ONE);
                break;
            case R.id.rb_store_fan:
                vpager.setCurrentItem(MainActivity.PAGE_TWO);
                break;
            case R.id.rb_store_xing:
                vpager.setCurrentItem(MainActivity.PAGE_THREE);
                break;
            case R.id.rb_store_cao:
                vpager.setCurrentItem(MainActivity.PAGE_FOUR);
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
                case MainActivity.PAGE_THREE:
                    rb3.setChecked(true);
                    break;
                case MainActivity.PAGE_FOUR:
                    rb4.setChecked(true);
                    break;
            }
        }
    }


}
