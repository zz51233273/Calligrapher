package com.example.hasee.bluecalligrapher.userinfo;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.hasee.bluecalligrapher.R;
import com.example.hasee.bluecalligrapher.adapter.MyFragmentAdapterUserInfo;
import com.example.hasee.bluecalligrapher.letter.SendLetterActivity;
import com.example.hasee.bluecalligrapher.main.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import circleimageview.CircleImageView;


/**
 * Created by hasee on 2018/6/15.
 */

public class UserInfoActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener,
        ViewPager.OnPageChangeListener, View.OnClickListener{
    private RadioButton rb1,rb2;
    private RadioGroup rg_tab_bar;
    private ViewPager vpager;
    private final int PAGE_ONE = 0;
    private final int PAGE_TWO = 1;
    private MyFragmentAdapterUserInfo mAdapter;
    private CircleImageView head;
    private TextView follow_text;       //关注
    private TextView user_info;         //关注人数,粉丝人数
    private TextView letter_text;       //私信
    private String phonenumber;
    private boolean hasFollowed=false;
    private final int HAS_FOLLOWED=1;
    private final int HAS_NOT_FOLLOWED=2;
    private final int CHANGE_SUCCESS=3;
    private final int CHANGE_FAIL=4;
    private int followed=0;     //关注人数
    private int follower=0;     //粉丝人数
    private Handler mhandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case HAS_FOLLOWED:
                    hasFollowed=true;
                    follow_text.setText("已关注");
                    updateFollowText((JSONObject)msg.obj);
                    break;
                case HAS_NOT_FOLLOWED:
                    hasFollowed=false;
                    follow_text.setText("关注");
                    updateFollowText((JSONObject)msg.obj);
                    break;
                case CHANGE_SUCCESS:
                    changeFollowStatus();
                    break;
                case CHANGE_FAIL:
                    Toast.makeText(getApplicationContext(), "关注失败，请查看网络", Toast.LENGTH_LONG).show();
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 1000);
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dynamic_user_info);
        init();
    }

    private void init(){
        follow_text=(TextView)findViewById(R.id.rb_follow);
        letter_text=(TextView)findViewById(R.id.rb_letter);
        user_info=(TextView)findViewById(R.id.user_info);
        phonenumber=getIntent().getStringExtra("phonenumber");
        new Thread(new Runnable() {
            @Override
            public void run() {
                searchFollowInfoRequest();
            }
        }).start();
        rg_tab_bar=findViewById(R.id.rg_tab_bar);
        rb1=findViewById(R.id.rb_about);
        rb1.setTextColor(getResources().getColor(R.color.bilibili_red));
        rb2=findViewById(R.id.rb_dynamic);
        rg_tab_bar.setOnCheckedChangeListener(this);
        mAdapter = new MyFragmentAdapterUserInfo(getSupportFragmentManager());
        vpager=(ViewPager)findViewById(R.id.vpager);
        vpager.setAdapter(mAdapter);
        vpager.setCurrentItem(PAGE_ONE);
        vpager.addOnPageChangeListener(this);
        follow_text.setOnClickListener(this);
        letter_text.setOnClickListener(this);
        head=findViewById(R.id.h_head);
        ((TextView)findViewById(R.id.user_name)).setText(getIntent().getStringExtra("username"));
        getUserHead();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_about:
                vpager.setCurrentItem(PAGE_ONE);
                break;
            case R.id.rb_dynamic:
                vpager.setCurrentItem(PAGE_TWO);
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
                    rb1.setTextColor(getResources().getColor(R.color.bilibili_red));
                    rb2.setTextColor(Color.BLACK);
                    break;
                case PAGE_TWO:
                    rb2.setChecked(true);
                    rb2.setTextColor(getResources().getColor(R.color.bilibili_red));
                    rb1.setTextColor(Color.BLACK);
                    break;
            }
        }
    }

    @Override
    public void onClick(View v){
        int id=v.getId();
        switch (id){
            case R.id.rb_follow:
                clickFollow();
                break;
            case R.id.rb_letter:
                clickLetter();
                break;
        }
    }

    private void clickFollow(){
        if(null== MainActivity.user){
            Toast.makeText(getApplicationContext(), "您还未登录呢", Toast.LENGTH_LONG).show();
        }else{
            new Thread(new Runnable() {
                @Override
                public void run() {
                    changeFollowRequest();
                }
            }).start();
        }
    }

    private void clickLetter(){
        if(null==MainActivity.user){
            Toast.makeText(getApplicationContext(), "您还未登录呢", Toast.LENGTH_LONG).show();
        }else{
            Intent i=new Intent(this, SendLetterActivity.class);
            i.putExtra("followed",phonenumber);
            startActivity(i);
        }
    }

    private void getUserHead(){
        byte[] bitmapByte=getIntent().getByteArrayExtra("head");
        head.setImageBitmap(BitmapFactory.decodeByteArray(bitmapByte, 0, bitmapByte.length));
    }

    private void changeFollowStatus(){
        if(!hasFollowed){   //表示为点击关注
            follow_text.setText("已关注");
            follower++;
            user_info.setText("关注 "+followed+" | 粉丝 "+follower);
            Toast.makeText(getApplicationContext(), "已关注", Toast.LENGTH_LONG).show();
        }else{               //表示为点击取消关注
            follower--;
            user_info.setText("关注 "+followed+" | 粉丝 "+follower);
            follow_text.setText("关注");
            Toast.makeText(getApplicationContext(), "已取消关注", Toast.LENGTH_LONG).show();
        }
        hasFollowed=!hasFollowed;
    }

    private void updateFollowText(JSONObject jsonObject){
        try {
            followed=Integer.parseInt(jsonObject.getString("followed"));
            follower=Integer.parseInt(jsonObject.getString("follower"));
        }catch (JSONException e){
            Log.e("TAG", e.getMessage(), e);
        }
        user_info.setText("关注 "+followed+" | 粉丝 "+follower);
    }

    //查询关注信息
    private void searchFollowInfoRequest(){
        //请求地址
        String url = "http://39.106.154.68:8080/calligrapher/SearchFollowInfoServlet";    //注①
        String tag = "SearchFollowInfo";    //注②

        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        //取得请求队列
        //防止重复请求，所以先取消tag标识的请求队列
        requestQueue.cancelAll(tag);

        //创建StringRequest，定义字符串请求的请求方式为POST(省略第一个参数会默认为GET方式)
        final StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = (JSONObject) new JSONObject(response).get("params");  //注③
                            String result = jsonObject.getString("Result");  //注④
                            Message message=Message.obtain();
                            message.obj=jsonObject;
                            if (result.equals("success")) {  //登录成功后
                                message.what=HAS_FOLLOWED;
                                mhandler.sendMessage(message);
                                //做自己的登录成功操作，如页面跳转
                            } else {
                                message.what=HAS_NOT_FOLLOWED;
                                mhandler.sendMessage(message);
                                //做自己的登录失败操作
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
                Log.e("TAG", error.getMessage(), error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("phonenumber", phonenumber);  //注⑥
                if(null!=MainActivity.user)
                    params.put("selfphonenumber",MainActivity.user.getPhoneNumber());
                else
                    params.put("selfphonenumber","");
                return params;
            }
        };
        //设置Tag标签
        request.setTag(tag);
        //将请求添加到队列中
        requestQueue.add(request);
    }

    private void changeFollowRequest(){
        //请求地址
        String url = "http://39.106.154.68:8080/calligrapher/ChangeFollowServlet";    //注①
        String tag = "ChangeFollow";    //注②

        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        //取得请求队列
        //防止重复请求，所以先取消tag标识的请求队列
        requestQueue.cancelAll(tag);

        //创建StringRequest，定义字符串请求的请求方式为POST(省略第一个参数会默认为GET方式)
        final StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        mhandler.sendEmptyMessage(CHANGE_SUCCESS);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //做自己的响应错误操作，如Toast提示（“请稍后重试”等）
                mhandler.sendEmptyMessage(CHANGE_FAIL);
                Log.e("TAG", error.getMessage(), error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("phonenumber", phonenumber);  //注⑥
                params.put("selfphonenumber",MainActivity.user.getPhoneNumber());
                if(!hasFollowed){
                    params.put("followed","0");
                }else{
                    params.put("followed","1");
                }
                return params;
            }
        };
        //设置Tag标签
        request.setTag(tag);

        //将请求添加到队列中
        requestQueue.add(request);
    }


}
