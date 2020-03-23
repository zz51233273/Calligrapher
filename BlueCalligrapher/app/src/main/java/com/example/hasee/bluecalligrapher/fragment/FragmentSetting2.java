package com.example.hasee.bluecalligrapher.fragment;

import android.graphics.Bitmap;
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
import com.example.hasee.bluecalligrapher.adapter.CommentListAdapter;
import com.example.hasee.bluecalligrapher.item.CommentItem;
import com.example.hasee.bluecalligrapher.main.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hasee on 2018/5/29.
 */

public class FragmentSetting2 extends Fragment{
    private ListView listView;
    private CommentListAdapter mAdapter;
    private List<CommentItem> commentItems=new ArrayList<CommentItem>();
    private final int GET_COM_SUCCESS=1;
    private final int GET_COM_FAIL=2;
    private boolean hasLoaded=false;
    private View view;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case GET_COM_SUCCESS:
                    getComments((JSONObject)msg.obj);
                    break;
                case GET_COM_FAIL:
                    Toast.makeText(getContext(), "服务器繁忙，请稍后重试", Toast.LENGTH_LONG).show();
                    ((com.wang.avi.AVLoadingIndicatorView)view.findViewById(R.id.message_avi)).setVisibility(View.GONE);
                    break;
            }
        }
    };
    public FragmentSetting2(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view=inflater.inflate(R.layout.fg_message_comment,container, false);
        init();
        return view;
    }

    private void init(){
        listView=(ListView)view.findViewById(R.id.comment_list);
    }

    //得到当前用户发表的评论
    private void searchCommentsByPhoneRequest(final String phonenumber){
        //请求地址
        String url = "http://39.106.154.68:8080/calligrapher/SearchCommentsByPhoneServlet";    //注①
        final String tag = "SearchCommentsByPhone";    //注②

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
                            Message message=Message.obtain();
                            message.what=GET_COM_SUCCESS;
                            message.obj=jsonObject;
                            handler.sendMessage(message);
                        } catch (JSONException e) {
                            //做自己的请求异常操作，如Toast提示（“无网络连接”等）
                            handler.sendEmptyMessage(GET_COM_FAIL);
                            Log.e("TAG", e.getMessage(), e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //做自己的响应错误操作，如Toast提示（“请稍后重试”等）
                handler.sendEmptyMessage(GET_COM_FAIL);
                Log.e("TAG", error.getMessage(), error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("phonenumber",phonenumber);
                return params;
            }
        };
        //设置Tag标签
        request.setTag(tag);
        //将请求添加到队列中
        requestQueue.add(request);
    }

    private void getComments(JSONObject jsonObject){
        try {
            int len=Integer.parseInt(jsonObject.getString("length"));
            Bitmap head= MainActivity.head_bitmap;
            String username=MainActivity.user.getUserName();
            for(int i=0;i<len;i++){
                int id=Integer.parseInt(jsonObject.getString("commentId"+i));       //评论id
                int dynamicId=Integer.parseInt(jsonObject.getString("dynamicId"+i));
                String context=jsonObject.getString("context"+i);                   //动态内容
                commentItems.add(new CommentItem(id,head,username,context,dynamicId));
            }
            mAdapter=new CommentListAdapter(getContext(),R.layout.message_comment_item,commentItems);
            listView.setAdapter(mAdapter);
            ((com.wang.avi.AVLoadingIndicatorView)view.findViewById(R.id.message_avi)).setVisibility(View.GONE);
        }catch (JSONException e){
            Log.e("TAG", e.getMessage(), e);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser && !hasLoaded) {
            hasLoaded=true;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    searchCommentsByPhoneRequest(MainActivity.user.getPhoneNumber());
                }
            }).start();
        }
        super.setUserVisibleHint(isVisibleToUser);
    }
}
