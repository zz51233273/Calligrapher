package com.example.hasee.bluecalligrapher.register;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.hasee.bluecalligrapher.R;
import com.mob.MobSDK;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * Created by hasee on 2018/3/30.
 */

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText phoneNumber;           //手机号
    private EditText identity_code;         //验证码
    private Button identity;                 //点击验证码
    private Button register;                //注册
    private Button has_pwd;
    private Button send_identity;          //验证码按钮
    private EventHandler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);
        MobSDK.init(this, "27d89fb6eacde","b4d86147ed34ba2cd57ff374f25b2d40");
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
                                forwardRegisterPassword();
                                Log.d("test","验证成功");
                            }
                        });
                    }else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        //获取验证码成功
                        runOnUiThread(new Runnable(){
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "验证码已发送" ,Toast.LENGTH_LONG).show();
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
        phoneNumber=(EditText)findViewById(R.id.r_phone_number);
        identity_code=(EditText)findViewById(R.id.r_identity);
        identity=(Button)findViewById(R.id.r_send_identity);
        identity.setOnClickListener(this);
        register=(Button)findViewById(R.id.r_register);
        register.setOnClickListener(this);
        has_pwd=(Button)findViewById(R.id.has_pwd);
        has_pwd.setOnClickListener(this);
        send_identity=(Button)findViewById(R.id.r_send_identity);
        send_identity.setOnClickListener(this);
        ((ImageView)findViewById(R.id.r_phone_clear)).setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        int id=v.getId();
        switch (id){
            case R.id.r_send_identity:      //点击发送验证码
                if(checkPhone())
                    sendIdentity();
                break;
            case R.id.r_register:           //点击注册
                // 触发操作,判断验证码是否正确
                if(checkPhone())
                    SMSSDK.submitVerificationCode("86", phoneNumber.getText().toString(), identity_code.getText().toString());
                break;
            case R.id.has_pwd:
                forwardLogin();
                break;
            case R.id.r_phone_clear:
                phoneNumber.setText("");
                break;
        }
    }

    private boolean checkPhone(){
        String pn=phoneNumber.getText().toString();
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

    private void sendIdentity(){      //发送短信
        String pn=phoneNumber.getText().toString();
        //获取验证码
        SMSSDK.getVerificationCode("86",pn);
    }

    private void forwardRegisterPassword(){     //跳转到注册密码界面
        Intent i=new Intent(RegisterActivity.this,RegisterPasswordActivity.class);
        i.putExtra("phonenumber",phoneNumber.getText().toString());
        startActivity(i);
        finish();
    }

    private void forwardLogin(){    //跳转到登录界面
        startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterEventHandler(handler);
    }
}
