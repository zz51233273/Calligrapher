package com.example.hasee.bluecalligrapher.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hasee on 2018/6/15.
 */

public class FragmentUserInfo1 extends Fragment{
    private View view;
    private boolean hasLoaded=false;
    private String sex="保密";
    private String birth="保密";
    private final int GET_INFO_SUCCESS=1;
    private final int GET_INFO_FAIL=2;
    private Handler mhandler=new Handler(){
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what){
                case GET_INFO_SUCCESS:
                    initInfo();
                    break;
                case GET_INFO_FAIL:
                    Toast.makeText(getContext(), "访问失败，请查看网络", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view=inflater.inflate(R.layout.fg_userinfo_about,container, false);
        return view;
    }
    private void initInfo(){
        ((TextView)view.findViewById(R.id.info_sex)).setText(sex);
        if(birth.length()>2){
            birth=birth.substring(2,4);
            ((TextView)view.findViewById(R.id.info_age)).setText(birth+"年");
        }else{
            ((TextView)view.findViewById(R.id.info_age)).setText(birth);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser && !hasLoaded) {
            hasLoaded=true;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while(null==getContext()){
                        try {
                            Thread.sleep(50);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    searchUserInfoRequest(((Activity)getContext()).getIntent().getStringExtra("phonenumber"));
                }
            }).start();
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    public void searchUserInfoRequest(final String phonenumber) {
        //请求地址
        String url = "http://39.106.154.68:8080/calligrapher/SearchUserInfoServlet";    //注①
        String tag = "SearchUserInfo";    //注②

        RequestQueue requestQueue= Volley.newRequestQueue(getContext().getApplicationContext());
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
                                sex=jsonObject.getString("sex");
                                birth=jsonObject.getString("birth");
                                mhandler.sendEmptyMessage(GET_INFO_SUCCESS);
                                //做自己的登录成功操作，如页面跳转
                            } else {
                                mhandler.sendEmptyMessage(GET_INFO_FAIL);
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
                return params;
            }
        };
        //设置Tag标签
        request.setTag(tag);

        //将请求添加到队列中
        requestQueue.add(request);
    }
}
