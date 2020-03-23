package com.example.hasee.bluecalligrapher.setting;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.example.hasee.bluecalligrapher.R;
import com.example.hasee.bluecalligrapher.main.MainActivity;
import com.example.hasee.bluecalligrapher.utils.ActionDialog;
import com.example.hasee.bluecalligrapher.utils.ActionInfDialog;
import com.example.hasee.bluecalligrapher.utils.CommomDialog;


/**
 * Created by hasee on 2018/8/9.
 */

public class TodayActionActivity extends AppCompatActivity implements View.OnClickListener{
    private String[] mission={"完成每日签到","临摹“万”字15遍(选择万字进行手写临摹，并点击提交)","评论动态1次","赏析名家作品(两幅)，并点评","分享光名"};
    private String[] value={"(+5日练点)","(+2日练点/遍)","(+5日练点)","(+5日练点/幅)","（+5日练点）"};
    private String get_inf="完成活动获取日练点";
    private String value_inf="满20日练点，可兑换5点经验值。满40日练点，可兑换10点经验值。满70日练点，可兑换15点经验值和名师点评的次数1次";
    private String resetting_inf= "日练点在每天的6:00重置，周练点在每周一00:00点清算\n满240周练点。可以获得以下奖励：①经验值50，②收藏容量上限+5";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.today_activity_layout);
        init();
    }

    private void init(){
        (findViewById(R.id.action1)).setOnClickListener(this);
        (findViewById(R.id.action2)).setOnClickListener(this);
        (findViewById(R.id.action3)).setOnClickListener(this);
        (findViewById(R.id.action4)).setOnClickListener(this);
        (findViewById(R.id.action5)).setOnClickListener(this);
        (findViewById(R.id.back)).setOnClickListener(this);
        (findViewById(R.id.action_inf)).setOnClickListener(this);

        if(null!= MainActivity.user){
            ((TextView)findViewById(R.id.action1_finish)).setText("完成度："+ MainActivity.user.getMission1()+"/1");
            ((TextView)findViewById(R.id.action2_finish)).setText("完成度："+ MainActivity.user.getMission2()+"/15");
            ((TextView)findViewById(R.id.action3_finish)).setText("完成度："+ MainActivity.user.getMission3()+"/1");
            ((TextView)findViewById(R.id.action4_finish)).setText("完成度："+ MainActivity.user.getMission4()+"/2");
            ((TextView)findViewById(R.id.action5_finish)).setText("完成度："+ MainActivity.user.getMission5()+"/1");
            ((TextView)findViewById(R.id.day_score)).setText("日练点："+MainActivity.user.getDay_score());
            ((TextView)findViewById(R.id.week_score)).setText("周练点："+MainActivity.user.getWeek_score());
        }
    }

    @Override
    public void onClick(View v){
        int id=v.getId();
        switch (id){
            case R.id.action1:
                createMissionDialog(mission[0],value[0]);
                break;
            case R.id.action2:
                createMissionDialog(mission[1],value[1]);
                break;
            case R.id.action3:
                createMissionDialog(mission[2],value[2]);
                break;
            case R.id.action4:
                createMissionDialog(mission[3],value[3]);
                break;
            case R.id.action5:
                createMissionDialog(mission[4],value[4]);
                break;
            case R.id.action_inf:
                createMissionInfDialog(get_inf,value_inf,resetting_inf);
                break;
            case R.id.back:
                finish();
                break;
        }
    }

    private void createMissionDialog(String mission,String value){
        new ActionDialog(this ,R.style.common_dialog ,mission ,value ,new CommomDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if(confirm){    //点击确定
                    dialog.dismiss();
                }
            }
        }).show();
    }
    private void createMissionInfDialog(String get_inf,String value_inf,String resetting_inf){
        new ActionInfDialog(this ,R.style.common_dialog ,get_inf,value_inf,resetting_inf,new CommomDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if(confirm){    //点击确定
                    dialog.dismiss();
                }
            }
        }).show();
    }
}
