package com.example.hasee.bluecalligrapher.tracing;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hasee.bluecalligrapher.R;
import com.example.hasee.bluecalligrapher.utils.BitmapCompareUtil;
import com.example.hasee.bluecalligrapher.utils.ImageUtil;

/**
 * Created by hasee on 2018/7/28.
 */

public class CharCompareActivity extends AppCompatActivity{
    private ImageView back;
    private Bitmap tracing_bitmap,img_bitmap;
    private ImageView img0,img1;
    private TextView result;
    private final int NOT_SIMILIAR=1;
    private final int CHANGE_SIMILIAR=2;
    private double sum=0;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what){
                case NOT_SIMILIAR:
                    result.setText("完全不相似");
                    break;
                case CHANGE_SIMILIAR:
                    result.setText(String.format("%.3f", sum)+"%");
                    break;
            }
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_compare_layout);
        init();
    }
    private void init(){
        AssetManager assetManager=getAssets();
        Typeface typeface=Typeface.createFromAsset(assetManager,"fonts/simhei.ttf");
        back=(ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        img0=(ImageView)findViewById(R.id.img0);
        img1=(ImageView)findViewById(R.id.img1);
        result=(TextView)findViewById(R.id.compare_text);
        result.setTypeface(typeface);
        ((TextView)findViewById(R.id.compare_title)).setTypeface(typeface);
        tracing_bitmap= ImageUtil.Bytes2Bimap(getIntent().getByteArrayExtra("tracing_img"));
        img_bitmap= ImageUtil.Bytes2Bimap(getIntent().getByteArrayExtra("img"));
        img_bitmap=ImageUtil.drawBgBitmap(Color.WHITE,img_bitmap);
        tracing_bitmap=ImageUtil.drawBgBitmap(Color.WHITE,tracing_bitmap);
        tracing_bitmap=Bitmap.createBitmap(tracing_bitmap,25, 25, tracing_bitmap.getWidth() - 50, tracing_bitmap.getHeight() - 50);
        img_bitmap=ImageUtil.scaleBitmap(img_bitmap,tracing_bitmap.getWidth(),tracing_bitmap.getHeight());

        img0.setImageBitmap(tracing_bitmap);
        img1.setImageBitmap(img_bitmap);
        result.setText(BitmapCompareUtil.similarity(tracing_bitmap,img_bitmap));
        comparePic();
    }

    //图片比较
    private void comparePic(){
        double value=((BitmapCompareUtil.getT() * 1.0) / (BitmapCompareUtil.getT()+ BitmapCompareUtil.getF()))*0.8;
        createPerAnim(value,2);

/*      CV4JImage cv4jImage = new CV4JImage(tracing_bitmap);
        CV4JImage cv4jImage2 = new CV4JImage(img_bitmap);
        ImageProcessor imageProcessor = cv4jImage.convert2Gray().getProcessor();
        ImageProcessor imageProcessor2 = cv4jImage2.convert2Gray().getProcessor();
        int[][] source = null;
        int[][] target = null;

        CalcHistogram calcHistogram = new CalcHistogram();
        int bins = 180;
        source = new int[imageProcessor.getChannels()][bins];
        calcHistogram.calcHSVHist(imageProcessor,bins,source,true);

        target = new int[imageProcessor2.getChannels()][bins];
        calcHistogram.calcHSVHist(imageProcessor2,bins,target,true);

        CompareHist compareHist = new CompareHist();
        double ncc=compareHist.ncc(source[0],target[0]);*/
/*        if(ncc>=0.99){
            result.setText(String.format("%.3f", ncc*100)+"%");
        }
        else if(ncc<=0.1){
            result.setText("完全不相似");
        }*/

    }

    private void createPerAnim(final double ncc,final double time){
        new Thread(new Runnable() {
            @Override
            public void run() {
                double num=0;
                num=ncc*100;
                double per_value=num/(100*time);
                Log.d("test",num+" "+per_value);
                while(sum<num){
                    mHandler.sendEmptyMessage(CHANGE_SIMILIAR);
                    sum+=per_value;
                    try {
                        Thread.sleep(10);
                    }catch (InterruptedException e){
                        Log.d("test",e.getMessage());
                    }
                }
                mHandler.sendEmptyMessage(CHANGE_SIMILIAR);
            }
        }).start();
    }
}
