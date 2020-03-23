package com.example.hasee.bluecalligrapher.tracing;

import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.hasee.bluecalligrapher.R;
import com.example.hasee.bluecalligrapher.adapter.TracingLibraryAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hasee on 2018/4/12.
 */

public class TracingLibraryActivity extends AppCompatActivity{
    private ImageView back; //返回箭头
    private ListView listView;
    private TracingLibraryAdapter mAdapter;
    private List<Character> storeItems=new ArrayList<>();
    private final int GET_SUCCESS=0;
    private final int GET_FAIL=1;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case GET_SUCCESS:
                    getCharactersList((String)msg.obj);
                    break;
                case GET_FAIL:
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tracing_library);
        mAdapter = new TracingLibraryAdapter(this, R.layout.manage_character_item, storeItems);
        listView = (ListView) findViewById(R.id.tracing_library_list);
        initLibrary();
        AssetManager mgr=getAssets();   //得到AssetManager
        Typeface typeface=Typeface.createFromAsset(mgr,"fonts/STXINGKA.TTF");//改变字体
        ((TextView)findViewById(R.id.tracing_library_title)).setTypeface(typeface);
    }

    private void initLibrary(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                getCharactersRequest();
            }
        }).start();
    }
    private void getCharactersList(String get_characters){
        Character character;
        int len=get_characters.length();
        for(int i=0 ; i<len ; i++){
            character=get_characters.charAt(i);
            storeItems.add(character);
        }
        listView.setAdapter(mAdapter);
    }

    //得到所有文字
    private void getCharactersRequest(){
        //请求地址
        String url = "http://39.106.154.68:8080/calligrapher/GetCharactersServlet";    //注①
        String tag = "GetCharacters";    //注②

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
                            if (result.equals("success")) {  //刷新动态成功
                                String characters=jsonObject.getString("characters");
                                Message message=Message.obtain();
                                message.what=GET_SUCCESS;
                                message.obj=characters;
                                handler.sendMessage(message);
                            } else {
                                handler.sendEmptyMessage(GET_FAIL);
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
                return params;
            }
        };
        //设置Tag标签
        request.setTag(tag);
        //将请求添加到队列中
        requestQueue.add(request);
    }
}
