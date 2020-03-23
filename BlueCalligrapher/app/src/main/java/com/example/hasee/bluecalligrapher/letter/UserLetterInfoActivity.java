package com.example.hasee.bluecalligrapher.letter;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.hasee.bluecalligrapher.R;
import com.example.hasee.bluecalligrapher.decodebase64.DecodeBase64;
import com.example.hasee.bluecalligrapher.utils.ImageUtil;

import circleimageview.CircleImageView;

/**
 * Created by hasee on 2018/6/18.
 */

public class UserLetterInfoActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_letter_info);
        init();
    }

    private void init(){
        byte[] bitmapByte=getIntent().getByteArrayExtra("head");
        ((CircleImageView)findViewById(R.id.letter_item_head)).setImageBitmap(BitmapFactory.decodeByteArray(bitmapByte, 0, bitmapByte.length));
        ((TextView)findViewById(R.id.letter_item_writer)).setText(getIntent().getStringExtra("sender"));
        ((TextView)findViewById(R.id.letter_item_context)).setText(getIntent().getStringExtra("context"));
        ((TextView)findViewById(R.id.letter_item_date)).setText(getIntent().getStringExtra("time"));
        String img=getIntent().getStringExtra("img");
        if(!"".equals(img)){
            byte[] imgs= DecodeBase64.decodeBase(img);
            ((ImageView)findViewById(R.id.img)).setImageBitmap(ImageUtil.Bytes2Bimap(imgs));
        }
    }

}
