package com.example.hasee.bluecalligrapher.register;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.hasee.bluecalligrapher.R;
import com.example.hasee.bluecalligrapher.bean.Store;
import com.example.hasee.bluecalligrapher.bean.User;
import com.example.hasee.bluecalligrapher.decodebase64.DecodeBase64;
import com.example.hasee.bluecalligrapher.main.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hasee on 2018/3/30.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    private final int LOGIN_FAIL=2;
    private ImageView passwordClear;               //叉号图片
    private ImageView numberClear;          //叉号图片
    private EditText phoneNumber;         //手机号
    private EditText password;             //密码
    private Button login;                   //登录
    private Button register;                //注册
    private Button forgive_pwd;             //忘记密码
    private boolean isEyeOpened=false;      //密码是否显示
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case LOGIN_FAIL:
                    Toast.makeText(getApplicationContext(), "手机号或密码错误", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        init();
    }
    void init(){
        passwordClear=(ImageView)findViewById(R.id.l_pwd_clear);
        passwordClear.setOnClickListener(this);
        numberClear=(ImageView)findViewById(R.id.l_phone_number_clear);
        numberClear.setOnClickListener(this);
        register=(Button)findViewById(R.id.l_register);
        register.setOnClickListener(this);
        login=(Button)findViewById(R.id.l_login);
        login.setOnClickListener(this);
        phoneNumber=(EditText)findViewById(R.id.l_phone_number);
        password=(EditText)findViewById(R.id.l_password);
        forgive_pwd=(Button)findViewById(R.id.forgive_pwd);
        forgive_pwd.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.l_pwd_clear:           //点击眼睛事件
                password.setText("");
                break;
            case R.id.l_phone_number_clear:   //清空输入的手机号事件
                phoneNumber.setText("");
                break;
            case R.id.l_login:            //登录事件
                String phonenumber=phoneNumber.getText().toString();
                if(phonenumber.length()!=11){      //判断手机号长度
                    Toast.makeText(getApplicationContext(), "手机号长度不正确", Toast.LENGTH_LONG).show();
                }
                else{
                    boolean flag=true;
                    for(int i=0;i<11;i++){
                        if(phonenumber.charAt(i)<'0'||phonenumber.charAt(i)>'9')
                            flag=false;
                    }
                    if(flag) {
                        sendLogin();
                    }
                    else
                        Toast.makeText(getApplicationContext(), "手机号格式不正确", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.l_register:
                forwardRegister();
                break;
            case R.id.forgive_pwd:
                forwardLoginByIdentity();
                break;
        }
    }


    private void forwardRegister(){ //跳转到注册页面
        startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
        finish();
    }
    private void forwardMain(){    //跳转到主界面
        finish();
    }
    private void forwardLoginByIdentity(){  //跳转到验证码登录界面
        startActivity(new Intent(LoginActivity.this,ForgetPasswordActivity.class));
        finish();
    }

    public void LoginRequest(final String phonenumber, final String password) {
        //请求地址
        final String url = "http://39.106.154.68:8080/calligrapher/LoginServlet";    //注①
        String tag = "Login";    //注②

        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        //取得请求队列
        //防止重复请求，所以先取消tag标识的请求队列
        requestQueue.cancelAll(tag);

        //创建StringRequest，定义字符串请求的请求方式为POST(省略第一个参数会默认为GET方式)
        final StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("test",response+"");
                        try {
                            ((com.wang.avi.AVLoadingIndicatorView)findViewById(R.id.poetry_avi)).setVisibility(View.GONE);
                            JSONObject jsonObject = (JSONObject) new JSONObject(response).get("params");  //注③
                            String result = jsonObject.getString("Result");  //注④
                            if (result.equals("success")) {  //登录成功后
                                MainActivity.user=new User(phonenumber,jsonObject.getString("Username"),password);//当前用户信息
                                String head_img=jsonObject.getString("headicon");
                                String sex=jsonObject.getString("sex");
                                String birth=jsonObject.getString("birth");
                                //是否签到
                                int check_in=Integer.parseInt(jsonObject.getString("check_in"));
                                if(!head_img.equals("")){
                                    byte[] img= DecodeBase64.decodeBase(head_img);
                                    MainActivity.user.setHead_img(img);
                                    MainActivity.head_bitmap=Bytes2Bimap(img);
                                }
                                if(!sex.equals(""))
                                    MainActivity.user.setSex(sex);
                                if(!birth.equals(""))
                                    MainActivity.user.setBirth(birth);
                                if(check_in==1){    //是否登录
                                    MainActivity.user.setCheck_in(true);
                                }else{
                                    MainActivity.user.setCheck_in(false);
                                }
                                //日练点，周练点
                                MainActivity.user.setDay_score((Integer.parseInt(jsonObject.getString("day_score"))));
                                MainActivity.user.setWeek_score((Integer.parseInt(jsonObject.getString("week_score"))));
                                //经验值
                                MainActivity.user.setExp((Integer.parseInt(jsonObject.getString("exp"))));
                                String hasStore=jsonObject.getString("hasStore");
                                String hasMission=jsonObject.getString("hasMission");
                                if(hasStore.equals("true")){    //用户拥有收藏的文字
                                    int number=Integer.parseInt(jsonObject.getString("storeNumber"));   //收藏的文字数量
                                    for(int i=0;i<number;i++){
                                        MainActivity.storeCharacter=MainActivity.storeCharacter+jsonObject.getString("char"+i);
                                        MainActivity.storeStyle=MainActivity.storeStyle+jsonObject.getString("style"+i);
                                        MainActivity.store[i]=new Store();
                                        MainActivity.store[i].setPicture(DecodeBase64.decodeBase(jsonObject.getString("charPic"+i)));
                                    }
                                }
                                if(hasMission.equals("true")){    //用户任务状态
                                    MainActivity.user.setMission1(Integer.parseInt(jsonObject.getString("mission1")));
                                    MainActivity.user.setMission2(Integer.parseInt(jsonObject.getString("mission2")));
                                    MainActivity.user.setMission3(Integer.parseInt(jsonObject.getString("mission3")));
                                    MainActivity.user.setMission4(Integer.parseInt(jsonObject.getString("mission4")));
                                    MainActivity.user.setMission5(Integer.parseInt(jsonObject.getString("mission5")));
                                }
                                forwardMain();
                                //做自己的登录成功操作，如页面跳转
                            } else {
                                handler.sendEmptyMessage(LOGIN_FAIL);
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
                params.put("password", password);
                return params;
            }
        };

        //设置Tag标签
        request.setTag(tag);

        //将请求添加到队列中
        requestQueue.add(request);
    }

    private void sendLogin(){
        ((com.wang.avi.AVLoadingIndicatorView)findViewById(R.id.poetry_avi)).setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                LoginRequest(phoneNumber.getText().toString(),password.getText().toString());
            }
        }).start();
    }

    public Bitmap Bytes2Bimap(byte[] b) {
        if (b.length != 0) {
            Bitmap bitmap= BitmapFactory.decodeByteArray(b, 0, b.length);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 80, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
            int options = 80;//先压缩到80%
            while (baos.toByteArray().length / 1024 > 200) { // 循环判断如果压缩后图片是否大于200kb,大于继续压缩
                if (options <= 0) {             //有的图片过大，可能当options小于或者等于0时，它的大小还是大于目标大小，于是就会发生异常，异常的原因是options超过规定值。所以此处需要判断一下
                    break;
                }
                baos.reset();// 重置baos即清空baos
                options -= 10;// 每次都减少10
                bitmap.compress(Bitmap.CompressFormat.PNG, options, baos);
            }
            return bitmap;
        } else {
            return null;
        }
    }
}
