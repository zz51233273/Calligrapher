package com.example.hasee.bluecalligrapher.fragment;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.hasee.bluecalligrapher.R;
import com.example.hasee.bluecalligrapher.adapter.MainListAdapter;
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
 * Created by hasee on 2018/6/15.
 */

public class FragmentUserInfo2 extends Fragment{
    private View view;
    private boolean hasLoaded=false;
    private final int SEARCH_SUCCESS=1;
    private final int SEARCH_FAIL=2;
    private List<DynamicsItem> dynamicsItems=new ArrayList<DynamicsItem>();
    private MainListAdapter listAdapter;
    private ListView listView;
    private Bitmap head;
    private String username;
    private Handler mhandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SEARCH_SUCCESS:
                    refreshDynamic((JSONObject) msg.obj);
                    break;
                case SEARCH_FAIL:
                    Toast.makeText(getContext(), "访问失败，请查看网络", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view=inflater.inflate(R.layout.fg_userinfo_dynamic,container, false);
        return view;
    }

    private void getUserInfo(){
        byte[] bitmapByte=((Activity)getContext()).getIntent().getByteArrayExtra("head");
        head=BitmapFactory.decodeByteArray(bitmapByte, 0, bitmapByte.length);
        username=((Activity)getContext()).getIntent().getStringExtra("username");
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser && !hasLoaded) {
            hasLoaded=true;
            getUserInfo();
            ((com.wang.avi.AVLoadingIndicatorView)view.findViewById(R.id.user_info_avi)).setVisibility(View.VISIBLE);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    searchDynamicRequest(((Activity)getContext()).getIntent().getStringExtra("phonenumber"));
                }
            }).start();
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    public void searchDynamicRequest(final String phonenumber) {
        //请求地址
        String url = "http://39.106.154.68:8080/calligrapher/SearchUserDynamicServlet";    //注①
        String tag = "SearchUserDynamic";    //注②

        RequestQueue requestQueue= Volley.newRequestQueue(getContext().getApplicationContext());
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
                            JSONObject jsonObject = (JSONObject) new JSONObject(response).get("params");  //注③
                            String result = jsonObject.getString("Result");  //注④
                            if (result.equals("success")) {  //登录成功后
                                Message message=Message.obtain();
                                message.what=SEARCH_SUCCESS;
                                message.obj=jsonObject;
                                mhandler.sendMessage(message);
                            } else {
                                mhandler.sendEmptyMessage(SEARCH_FAIL);
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
                if(null== MainActivity.user)
                    params.put("selfphonenumber", "");  //注⑥
                else
                    params.put("selfphonenumber", MainActivity.user.getPhoneNumber());  //注⑥
                params.put("phonenumber", phonenumber);  //注⑥
                return params;
            }
        };
        //设置Tag标签
        request.setTag(tag);

        //将请求添加到队列中
        requestQueue.add(request);
    }

    private void refreshDynamic(JSONObject jsonObject){
        listView=view.findViewById(R.id.listview);
        try {
            int len = Integer.parseInt(jsonObject.getString("length"));
            if (len != 0) {
                int pos = 0;
                dynamicsItems.clear();
                while (pos < len) {
                    String context = jsonObject.getString("context" + pos);     //动态内容
                    String focus1 = jsonObject.getString("focus" + pos);        //当前用户是否已点赞
                    String time = jsonObject.getString("time" + pos);        //动态发表时间
                    if(time.length()-5>0)
                        time=time.substring(5,time.length()-5);
                    int dynamicCount = Integer.parseInt(jsonObject.getString("focuscount" + pos)); //动态点赞数
                    int commentCount = Integer.parseInt(jsonObject.getString("commentcount" + pos)); //动态评论数
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
                    DynamicsItem dynamicsItem = new DynamicsItem(dynamicId ,head, username, context, time,bitmaps[0], bitmaps[1], bitmaps[2], bitmaps[3], bitmaps[4], bitmaps[5]);
                    dynamicsItem.setFocus(focus);
                    dynamicsItem.setFocusCount(dynamicCount);
                    dynamicsItem.setCommentCount(commentCount);
                    dynamicsItems.add(dynamicsItem);
                    pos++;
                }
                listAdapter = new MainListAdapter(getContext(), R.layout.main_item, dynamicsItems);
                listView.setAdapter(listAdapter);
                ((com.wang.avi.AVLoadingIndicatorView)view.findViewById(R.id.user_info_avi)).setVisibility(View.GONE);
            }
        }catch (JSONException e){
            Log.e("TAG", e.getMessage(), e);
        }
    }
}
