package com.example.hasee.bluecalligrapher;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


import com.example.hasee.bluecalligrapher.main.MainActivity;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by hasee on 2018/4/10.
 */

public class StartActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_layout);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                startActivity(new Intent(StartActivity.this, MainActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        }, 2500);
    }
}
