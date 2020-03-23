package com.example.hasee.bluecalligrapher.register;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.hasee.bluecalligrapher.R;
import com.example.hasee.bluecalligrapher.bean.User;
import com.example.hasee.bluecalligrapher.main.MainActivity;
import com.example.hasee.bluecalligrapher.utils.StringUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hasee on 2018/3/30.
 */

public class RegisterPasswordActivity extends AppCompatActivity implements View.OnClickListener{
    private Button login;
    private EditText username;
    private EditText password;
    private EditText multy_password;
    private final int REGISTER_SUCCESS=1;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what){
                case REGISTER_SUCCESS:
                    ((com.wang.avi.AVLoadingIndicatorView)findViewById(R.id.poetry_avi)).setVisibility(View.GONE);
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_password);
        init();
    }
    void init(){
        username=(EditText)findViewById(R.id.rp_username);
        password=(EditText)findViewById(R.id.rp_password);
        multy_password=(EditText)findViewById(R.id.rp_multy_password);
        login=(Button)findViewById(R.id.rp_login);
        login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        int id=v.getId();
        switch (id){
            case R.id.rp_login:
                final String password1=password.getText().toString();
                final String username1=username.getText().toString();
                if(username1.equals("")){
                    Toast.makeText(getApplicationContext(), "用户名不能为空", Toast.LENGTH_LONG).show();
                }else if(!StringUtil.check(username1)){
                    Toast.makeText(getApplicationContext(), "存在敏感字词，不能发送", Toast.LENGTH_LONG).show();
                }else if(username1.length()<2||username1.length()>16){
                    Toast.makeText(getApplicationContext(), "用户名长度要在2~16以内", Toast.LENGTH_LONG).show();
                }else{
                    if(!password1.equals(multy_password.getText().toString())){
                        Toast.makeText(getApplicationContext(), "两行密码不一致", Toast.LENGTH_LONG).show();
                    }else if(password1.length()<2||password1.length()>11){
                        Toast.makeText(getApplicationContext(), "密码长度要在2~11以内", Toast.LENGTH_LONG).show();
                    }else{
                        ((com.wang.avi.AVLoadingIndicatorView)findViewById(R.id.poetry_avi)).setVisibility(View.VISIBLE);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                registerRequest(password1, username1);      //在服务器上请求注册账号
                            }
                        }).start();
                    }
                }
                break;
        }
    }

    private void forwardMain(){
        finish();
    }


    public void registerRequest(final String password,final String username) {
        //请求地址
        String url = "http://39.106.154.68:8080/calligrapher/RegisterServlet";    //注①
        String tag = "Register";    //注②

        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        //取得请求队列
        //防止重复请求，所以先取消tag标识的请求队列
        requestQueue.cancelAll(tag);

        //创建StringRequest，定义字符串请求的请求方式为POST(省略第一个参数会默认为GET方式)
        final StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        MainActivity.user=new User(getIntent().getStringExtra("phonenumber"),username,password);
                        MainActivity.user.setCheck_in(false);
                        forwardMain();
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
                params.put("phonenumber", getIntent().getStringExtra("phonenumber"));  //注⑥
                params.put("password", password);
                params.put("username",username);
                return params;
            }
        };
        //设置Tag标签
        request.setTag(tag);
        //将请求添加到队列中
        requestQueue.add(request);
    }
}
