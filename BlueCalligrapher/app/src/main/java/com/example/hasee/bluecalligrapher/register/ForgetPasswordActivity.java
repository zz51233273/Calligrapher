package com.example.hasee.bluecalligrapher.register;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
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
import com.mob.MobSDK;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * Created by hasee on 2018/3/30.
 */

public class ForgetPasswordActivity extends AppCompatActivity implements View.OnClickListener{
    private Button login;           //登录
    private EditText identity;      //验证码
    private Button send_identity;   //发送验证码
    private Button loginByPassword; //密码登录
    private Button register;
    private EventHandler handler;
    private EditText phonenumber;
    private final int SEARCH_SUCCESS=1;
    private final int SEARCH_FAIL=2;
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what){
                case SEARCH_SUCCESS:
                    SMSSDK.submitVerificationCode("86", phonenumber.getText().toString(), identity.getText().toString());
                    break;
                case SEARCH_FAIL:
                    Toast.makeText(getApplicationContext(), "当前手机号未被注册", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_by_identity);
        MobSDK.init(this, "24f989c07b706","71e94bad2de5fa4075b57feca938c8ac");
        init();
    }

    void init(){
        handler= new EventHandler(){
            @Override
            public void afterEvent(int event, int result, Object data) {
                if (result == SMSSDK.RESULT_COMPLETE){
                    //回调完成
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        //提交验证码成功
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                forwardModifyPassword();
                                Log.d("test","验证成功");
                            }
                        });
                    }else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        //获取验证码成功
                        runOnUiThread(new Runnable(){
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "验证码已发送", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "验证码错误", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        };
        SMSSDK.registerEventHandler(handler);
        login=(Button)findViewById(R.id.li_login);
        identity=(EditText)findViewById(R.id.li_identity);
        send_identity=(Button)findViewById(R.id.r_send_identity);
        phonenumber=(EditText)findViewById(R.id.li_phone_number);
        register=(Button)findViewById(R.id.li_register);
        loginByPassword=(Button)findViewById(R.id.login_pwd);
        ((ImageView)findViewById(R.id.li_phone_number_clear)).setOnClickListener(this);
        send_identity.setOnClickListener(this);
        loginByPassword.setOnClickListener(this);
        register.setOnClickListener(this);
        login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        int id=v.getId();
        switch (id){
            case R.id.r_send_identity:
                if(checkPhone())
                    sendIdentity();
                break;
            case R.id.li_login:
                if(checkPhone()){
                    isRegistered(phonenumber.getText().toString());
                }
                break;
            case R.id.login_pwd:
                forwardLogin();
                break;
            case R.id.li_register:
                forwardRegister();
                break;
            case R.id.li_phone_number_clear:
                phonenumber.setText("");
                break;
        }
    }
    void forwardRegister(){
        startActivity(new Intent(ForgetPasswordActivity.this,RegisterActivity.class));
        finish();
    }
    void forwardModifyPassword(){
        Intent i=new Intent(ForgetPasswordActivity.this,ModifyPasswordActivity.class);
        i.putExtra("phonenumber",phonenumber.getText().toString());
        startActivity(i);
        finish();
    }
    void forwardLogin(){
        startActivity(new Intent(ForgetPasswordActivity.this,LoginActivity.class));
        finish();
    }

    private void sendIdentity(){      //发送短信
        //获取验证码
        SMSSDK.getVerificationCode("86",phonenumber.getText().toString());
    }

    private boolean checkPhone(){
        String pn=phonenumber.getText().toString();
        if(pn.equals("")){
            Toast.makeText(getApplicationContext(), "手机号不能为空", Toast.LENGTH_LONG).show();
            return false;
        }else if(pn.length()!=11){
            Toast.makeText(getApplicationContext(), "号码长度不正确", Toast.LENGTH_LONG).show();
            return false;
        }else{
            for(int i=0;i<pn.length();i++){     //保证手机格式正确
                if(pn.charAt(i)<'0'||pn.charAt(i)>'9'){
                    Toast.makeText(getApplicationContext(), "手机号格式不正确", Toast.LENGTH_LONG).show();
                    return false;
                }
            }
            return true;
        }
    }

    private void isRegistered(final String phonenumber){
        new Thread(new Runnable() {
            @Override
            public void run() {
                searchPhoneRequest(phonenumber);
            }
        }).start();
    }

    private void searchPhoneRequest(final String phonenumber){
        //请求地址
        String url = "http://39.106.154.68:8080/calligrapher/SearchPhoneServlet";    //注①
        String tag = "searchPhone";    //注②

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
                            if (result.equals("success")) {  //登录成功后
                                MainActivity.user = new User();//当前用户信息
                                MainActivity.user.setPhoneNumber(phonenumber);
                                MainActivity.user.setUserName(jsonObject.getString("Username"));
                                String head_img = jsonObject.getString("headicon");
                                String sex=jsonObject.getString("sex");
                                String birth=jsonObject.getString("birth");
                                int check_in=Integer.parseInt(jsonObject.getString("check_in"));
                                if (!head_img.equals("")) {
                                    byte[] img = DecodeBase64.decodeBase(head_img);
                                    MainActivity.user.setHead_img(img);
                                    MainActivity.head_bitmap = Bytes2Bimap(img);
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
                                String hasStore = jsonObject.getString("hasStore");
                                String hasMission=jsonObject.getString("hasMission");
                                if (hasStore.equals("true")) {    //用户拥有收藏的文字
                                    int number = Integer.parseInt(jsonObject.getString("storeNumber"));   //收藏的文字数量
                                    for (int i = 0; i < number; i++) {
                                        MainActivity.storeCharacter = MainActivity.storeCharacter + jsonObject.getString("char" + i);
                                        MainActivity.storeStyle = MainActivity.storeStyle + jsonObject.getString("style" + i);
                                        MainActivity.store[i] = new Store();
                                        MainActivity.store[i].setPicture(DecodeBase64.decodeBase(jsonObject.getString("charPic" + i)));
                                    }
                                }
                                if(hasMission.equals("true")){    //用户任务状态
                                    MainActivity.user.setMission1(Integer.parseInt(jsonObject.getString("mission1")));
                                    MainActivity.user.setMission2(Integer.parseInt(jsonObject.getString("mission2")));
                                    MainActivity.user.setMission3(Integer.parseInt(jsonObject.getString("mission3")));
                                    MainActivity.user.setMission4(Integer.parseInt(jsonObject.getString("mission4")));
                                    MainActivity.user.setMission5(Integer.parseInt(jsonObject.getString("mission5")));
                                }
                                mHandler.sendEmptyMessage(SEARCH_SUCCESS);
                            } else {
                                mHandler.sendEmptyMessage(SEARCH_FAIL);
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
                return params;
            }
        };

        //设置Tag标签
        request.setTag(tag);

        //将请求添加到队列中
        requestQueue.add(request);
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
