package com.example.hasee.bluecalligrapher.setting;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.example.hasee.bluecalligrapher.R;


/**
 * Created by hasee on 2018/4/13.
 */

public class SettingAboutActivity extends AppCompatActivity{
    private ImageView back;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_about_us);
        init();
    }
    private void init(){
        back=(ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

}
