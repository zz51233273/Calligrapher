package com.example.hasee.bluecalligrapher.setting;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.hasee.bluecalligrapher.R;
import com.example.hasee.bluecalligrapher.adapter.UserDynamicListAdapter;
import com.example.hasee.bluecalligrapher.decodebase64.DecodeBase64;
import com.example.hasee.bluecalligrapher.item.DynamicsItem;
import com.example.hasee.bluecalligrapher.main.MainActivity;
import com.example.hasee.bluecalligrapher.utils.ImageUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by hasee on 2018/5/7.
 */

public class UserDynamicActivity extends AppCompatActivity{
    ListView listView;
    public static List<DynamicsItem> dynamicsItems=new ArrayList<DynamicsItem>();
    private UserDynamicListAdapter listAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private final int REFRESH_SUCCESS=1;
    private final int REFRESH_FAIL=2;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case REFRESH_SUCCESS:
                    try {
                        swipeRefreshLayout.setRefreshing(false);//设置不刷新
                        refreshDynamic((JSONObject) msg.obj);
                    }catch (JSONException e) {
                        //做自己的请求异常操作，如Toast提示（“无网络连接”等）
                        Log.e("TAG", e.getMessage(), e);
                    }
                    break;
                case REFRESH_FAIL:
                    swipeRefreshLayout.setRefreshing(false);//设置不刷新
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_dynamic_layout);
        init();
    }

    private void init(){
        listView = findViewById(R.id.dynamic_list);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.dynamic_refresh);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                searchDynamic();
            }
        });
        searchDynamic();
    }

    private void searchDynamic(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                searchPerDynamic();
            }
        }).start();
    }
    //查询个人前十条动态
    private void searchPerDynamic(){
        //请求地址
        String url = "http://39.106.154.68:8080/calligrapher/SearchUserDynamicServlet";    //注①
        String tag = "SearchUserDynamic";    //注②

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
                                Message message=Message.obtain();
                                message.what=REFRESH_SUCCESS;
                                message.obj=jsonObject;
                                handler.sendMessage(message);
                            } else {
                                handler.sendEmptyMessage(REFRESH_FAIL);
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
                params.put("phonenumber", MainActivity.user.getPhoneNumber());
                params.put("selfphonenumber",MainActivity.user.getPhoneNumber());
                return params;
            }
        };
        //设置Tag标签
        request.setTag(tag);
        //将请求添加到队列中
        requestQueue.add(request);
    }

    private void refreshDynamic(JSONObject jsonObject)throws JSONException {
        int len = Integer.parseInt(jsonObject.getString("length"));
        //Log.d("test",len+"");
        if (len != 0) {
            int pos = 0;
            dynamicsItems.clear();
            while (pos < len) {
                String context = jsonObject.getString("context" + pos);     //动态内容
                String focus1 = jsonObject.getString("focus" + pos);        //当前用户是否已点赞
                Bitmap head_img= ImageUtil.Bytes2Bimap(MainActivity.user.getHead_img());
                int dynamicCount = Integer.parseInt(jsonObject.getString("focuscount" + pos)); //动态点赞数
                int commentCount = Integer.parseInt(jsonObject.getString("commentcount" + pos)); //动态评论数
                String time=jsonObject.getString("time"+pos);                //发送时
                if(time.length()-5>0)
                    time=time.substring(5,time.length()-5);
                boolean focus = false;
                if (focus1.equals("1"))
                    focus = true;
                else
                    focus = false;
                int dynamicId = Integer.parseInt(jsonObject.getString("dynamicId" + pos));
                int img_len = Integer.parseInt(jsonObject.getString("img_len" + pos));
                Bitmap[] bitmaps = new Bitmap[10];
                for (int i = 0; i < img_len; i++) {
                    String img_text = jsonObject.getString("img" + i + pos);
                    byte[] img_byte = DecodeBase64.decodeBase(img_text);
                    bitmaps[i] = ImageUtil.Bytes2Bimap(img_byte);
                }
                for (int i = img_len; i < 6; i++) {
                    bitmaps[i] = null;
                }
                DynamicsItem dynamicsItem = new DynamicsItem(dynamicId ,head_img, MainActivity.user.getUserName(), context, time, bitmaps[0], bitmaps[1], bitmaps[2], bitmaps[3], bitmaps[4], bitmaps[5]);
                dynamicsItem.setFocus(focus);
                dynamicsItem.setFocusCount(dynamicCount);
                dynamicsItem.setCommentCount(commentCount);
                dynamicsItems.add(dynamicsItem);
                pos++;
            }
            listAdapter = new UserDynamicListAdapter(this, R.layout.user_dynamic_item, dynamicsItems);
            listView.setAdapter(listAdapter);
        }
    }
}
