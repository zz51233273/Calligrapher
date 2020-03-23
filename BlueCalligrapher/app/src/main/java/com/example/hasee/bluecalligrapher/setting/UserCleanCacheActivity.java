package com.example.hasee.bluecalligrapher.setting;

import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.example.hasee.bluecalligrapher.R;
import com.example.hasee.bluecalligrapher.utils.CommomDialog;
import com.example.hasee.bluecalligrapher.utils.DataCleanManager;

import java.io.File;

/**
 * Created by hasee on 2018/5/28.
 */

public class UserCleanCacheActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView cache_size_text;
    private LinearLayout personal_clean;
    private final int CLEAN_SUCCESS=0;
    private final int CLEAN_FAIL=1;
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case CLEAN_SUCCESS:
                    cache_size_text.setText("0.00KB");
                    break;
                case CLEAN_FAIL:
                    break;
            }
        };
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_clean_cache);
        init();
    }
    private void init(){
        Typeface typeface=Typeface.createFromAsset(getAssets(),"fonts/STXINWEI.TTF");//改变字体
        cache_size_text =(TextView)findViewById(R.id.cache_size);
        personal_clean = (LinearLayout) findViewById(R.id.personal_clean);
        ((TextView)findViewById(R.id.title_name)).setTypeface(typeface);
        ((TextView)findViewById(R.id.clean_text)).setTypeface(typeface);
        cache_size_text.setTypeface(typeface);
        personal_clean.setOnClickListener(this);
        //获得应用内部缓存(/data/data/com.example.androidclearcache/cache)
        File file =new File(this.getCacheDir().getPath());
        try {
            cache_size_text.setText(DataCleanManager.getCacheSize(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v){
        int id=v.getId();
        switch (id){
            case R.id.personal_clean:   //点击清空缓存
                createConfirmDialog("清空缓存后，下载的图片和文件都将被清除\n确认清除？");
                break;
        }
    }

    //创建确认对话框
    private void createConfirmDialog(String title){
        new CommomDialog(this, R.style.common_dialog, title, new CommomDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if(confirm){
                    cleanCache();
                    dialog.dismiss();
                }
            }
        }).setPositiveButton("确定").show();
    }

    //清除缓存方法
    private void cleanCache(){
        ((com.wang.avi.AVLoadingIndicatorView) findViewById(R.id.setting_avi)).setVisibility(View.VISIBLE);
        try {
            DataCleanManager.cleanInternalCache(getApplicationContext());
            handler.sendEmptyMessage(CLEAN_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            handler.sendEmptyMessage(CLEAN_FAIL);
        }
        ((com.wang.avi.AVLoadingIndicatorView) findViewById(R.id.setting_avi)).setVisibility(View.GONE);
    }
}
