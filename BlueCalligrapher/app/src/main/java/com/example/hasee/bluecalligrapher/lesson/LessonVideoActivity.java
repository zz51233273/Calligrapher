package com.example.hasee.bluecalligrapher.lesson;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.hasee.bluecalligrapher.R;


/**
 * Created by hasee on 2018/4/9.
 */

public class LessonVideoActivity extends AppCompatActivity{
    private VideoView videoView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lesson_video_layout);
        init();
    }
    private void init(){
        videoView = (VideoView)this.findViewById(R.id.lesson_video);
        getChapter();
    }
    private void getChapter(){
        String id=getIntent().getStringExtra("chapter");
        String uri="";
        switch (id){
            case "1":
                uri ="android.resource://" + getPackageName() + "/" + R.raw.chapter01;
                break;
            case "2":
                break;
            case "3":
                break;
            case "4":
                break;
            case "5":
                break;
        }
        videoView.setMediaController(new MediaController(this));
        videoView.setVideoURI(Uri.parse(uri));
        videoView.start();
        videoView.requestFocus();
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
        {
            @Override
            public void onCompletion(MediaPlayer mp)
            {
                ((TextView)findViewById(R.id.lesson_video_finish)).setText("播放完成");
                //播放结束后的动作
            }
        });
    }
}
