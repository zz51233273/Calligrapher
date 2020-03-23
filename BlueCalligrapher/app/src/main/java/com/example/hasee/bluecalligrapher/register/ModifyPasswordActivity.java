package com.example.hasee.bluecalligrapher.register;

import android.os.Bundle;
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
import com.example.hasee.bluecalligrapher.bean.Store;
import com.example.hasee.bluecalligrapher.main.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hasee on 2018/5/27.
 */

public class ModifyPasswordActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText password;
    private EditText multy_password;
    private Button login;
    private String phonenumber;
    private boolean flag=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_modify_password);
        init();
    }
    private void init(){
        phonenumber=getIntent().getStringExtra("phonenumber");
        password=findViewById(R.id.rp_password);
        multy_password=findViewById(R.id.rp_multy_password);
        login=findViewById(R.id.rp_login);
        login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        int id=v.getId();
        switch (id){
            case R.id.rp_login:
                if(checkPassword())
                    modifyPasswordRequest(password.getText().toString());
                break;
        }
    }

    private boolean checkPassword(){
        String password1=password.getText().toString();
        if(!password1.equals(multy_password.getText().toString())){
            Toast.makeText(getApplicationContext(), "两行密码不一致", Toast.LENGTH_LONG).show();
            return false;
        }else if(password1.length()<2||password1.length()>11){
            Toast.makeText(getApplicationContext(), "密码长度要在2~11以内", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void modifyPasswordRequest(final String password){
        //请求地址
        String url = "http://39.106.154.68:8080/calligrapher/ModifyPasswordServlet";    //注①
        String tag = "ModifyPassword";    //注②

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
                            if (result.equals("success")) {  //修改成功后
                                MainActivity.user.setPassword(password);
                                forwardMain();
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

    private void forwardMain(){
        flag=true;
        MainActivity.dynamicsItems.clear();
        finish();
    }

    @Override
    protected void onPause(){
        super.onPause();
        if(!flag){
            clearUser();
        }
    }

    private void clearUser(){
        MainActivity.user=null;
        MainActivity.store=new Store[40];
        MainActivity.storeCharacter="";
        MainActivity.storeStyle="";
        MainActivity.head_bitmap=null;
    }
}
