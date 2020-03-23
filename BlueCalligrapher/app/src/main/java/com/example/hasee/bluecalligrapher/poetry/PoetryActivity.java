package com.example.hasee.bluecalligrapher.poetry;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.ImageViewState;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.example.hasee.bluecalligrapher.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hasee on 2018/4/8.
 */

public class PoetryActivity extends AppCompatActivity implements View.OnClickListener{
    private SubsamplingScaleImageView imageView;
    private TextView poetry_comment;
    private int poetryId;
    public static int comment_counts;
    private final int GET_COM_SUCCESS=1;
    private String poetry_inf="此作品出自马永先硬笔书法工作室\n请勿用作其他商业用途";
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what){
                case GET_COM_SUCCESS:
                    poetry_comment.setText("评论("+comment_counts+")");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.poetry_image_show);
        comment_counts=0;
        imageView= (SubsamplingScaleImageView) findViewById(R.id.poetry_image);
        poetry_comment=(TextView)findViewById(R.id.poetry_comment);
        poetryId=getIntent().getIntExtra("poetryId",1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                searchCommentCounts();
            }
        }).start();
        ((ImageView)findViewById(R.id.back)).setOnClickListener(this);
        ((ImageView)findViewById(R.id.poetry_inf)).setOnClickListener(this);
        poetry_comment.setOnClickListener(this);
        imageView.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CUSTOM);
        imageView.setMinScale(0.15F);//最小显示比例
        imageView.setMaxScale(2.0F);//最大显示比例（太大了图片显示会失真，因为一般微博长图的宽度不会太宽）
        String image_url=getIntent().getStringExtra("image_url");
        final String url ="http://39.106.154.68:8080/"+image_url;
        //下载图片保存到本地
        Glide.with(this)
                .load(url).downloadOnly(new SimpleTarget<File>() {
            @Override
            public void onResourceReady(File resource, GlideAnimation<? super File> glideAnimation) {
                // 将保存的图片地址给SubsamplingScaleImageView,这里注意设置ImageViewState设置初始显示比例
                imageView.setImage(ImageSource.uri(Uri.fromFile(resource)), new ImageViewState(0.20F, new PointF(0, 0), 0));
                ((TextView)findViewById(R.id.poetry_wait)).setText("");
                ((com.wang.avi.AVLoadingIndicatorView)findViewById(R.id.poetry_avi)).setVisibility(View.GONE);
                poetry_comment.setVisibility(View.VISIBLE);
            }});
    }

    @Override
    public void onClick(View v){
        int id=v.getId();
        switch (id){
            case R.id.back:
                finish();
                break;
            case R.id.poetry_comment:
                Intent i=new Intent(PoetryActivity.this,PoetryCommentActivity.class);
                i.putExtra("poetryId",poetryId);
                startActivity(i);
                break;
            case R.id.poetry_inf:
                createPoetryInfDialog(poetry_inf);
                break;
        }
    }

    private void createPoetryInfDialog(String title){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // 设置参数
        builder.setTitle("声明")
                .setMessage(title)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    @Override
    public void onResume(){
        super.onResume();
        poetry_comment.setText("评论("+comment_counts+")");
    }

    //请求查询作品评论数
    private void searchCommentCounts(){
        //请求地址
        String url = "http://39.106.154.68:8080/calligrapher/searchPoetryComCounts";    //注①
        final String tag = "PoetryComCounts";    //注②

        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        //取得请求队列
        //防止重复请求，所以先取消tag标识的请求队列
        requestQueue.cancelAll(tag);

        //创建StringRequest，定义字符串请求的请求方式为POST(省略第一个参数会默认为GET方式)
        final StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Log.d("test",response+"");
                        try {
                            JSONObject jsonObject = (JSONObject) new JSONObject(response).get("params");  //注③
                            String result = jsonObject.getString("Result");  //注④
                            if (result.equals("success")) {  //刷新评论成功
                                comment_counts=Integer.parseInt(jsonObject.getString("counts"));
                                handler.sendEmptyMessage(GET_COM_SUCCESS);
                            }
                        } catch (JSONException e) {
                            //做自己的请求异常操作，如Toast提示（“无网络连接”等）
                            Log.e("TAG", e.getMessage(), e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //做自己的响应错误操作，如Toast提示（“请稍后重试”等）
                Toast.makeText(getApplicationContext(), "服务器繁忙，请稍后重试", Toast.LENGTH_LONG).show();
                Log.e("TAG", error.getMessage(), error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("poetryId",poetryId+"");
                return params;
            }
        };
        //设置Tag标签
        request.setTag(tag);
        //将请求添加到队列中
        requestQueue.add(request);
    }
}
