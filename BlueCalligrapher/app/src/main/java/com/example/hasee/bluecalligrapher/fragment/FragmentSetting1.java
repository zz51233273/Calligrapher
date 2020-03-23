package com.example.hasee.bluecalligrapher.fragment;

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
import com.example.hasee.bluecalligrapher.adapter.MessageLetterListAdapter;
import com.example.hasee.bluecalligrapher.decodebase64.DecodeBase64;
import com.example.hasee.bluecalligrapher.item.LetterItem;
import com.example.hasee.bluecalligrapher.main.MainActivity;
import com.example.hasee.bluecalligrapher.utils.ImageUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hasee on 2018/5/29.
 */

public class FragmentSetting1 extends Fragment{
    private final int GET_LETTER_SUCCESS=1;
    private final int GET_LETTER_FAIL=2;
    private final int GET_LETTER_NULL=3;
    private boolean hasLoaded=false;
    private List<LetterItem> letterItems=new ArrayList<LetterItem>();
    private MessageLetterListAdapter mAdapter;
    private ListView listView;
    private View view;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case GET_LETTER_SUCCESS:
                    getLetters((JSONObject)msg.obj);
                    break;
                case GET_LETTER_FAIL:
                    Toast.makeText(getContext(), "服务器繁忙，请稍后重试", Toast.LENGTH_LONG).show();
                    break;
                case GET_LETTER_NULL:
                    ((com.wang.avi.AVLoadingIndicatorView)view.findViewById(R.id.message_avi)).setVisibility(View.GONE);
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view=inflater.inflate(R.layout.fg_message_letter,container, false);
        init();
        return view;
    }

    private void init(){
        listView=(ListView)view.findViewById(R.id.listview);
        if(hasLoaded){
            ((com.wang.avi.AVLoadingIndicatorView)view.findViewById(R.id.message_avi)).setVisibility(View.GONE);
            listView.setAdapter(mAdapter);
        }
    }

    private void getLetters(JSONObject jsonObject){
        try {
            int len=Integer.parseInt(jsonObject.getString("len"));
            LetterItem letterItem;
            for(int i=0;i<len;i++){
                letterItem=new LetterItem();
                String head=jsonObject.getString("head"+i);
                Bitmap head_img=initHead(head);
                letterItem.setHead(head_img);                                       //发送人头像
                letterItem.setSender(jsonObject.getString("sender"+i));     //发送人
                letterItem.setSender_name(jsonObject.getString("sender_name"+i));     //发送人
                letterItem.setContext(jsonObject.getString("context"+i));   //内容
                String time=jsonObject.getString("time"+i);                 //发送时间
                if(time.length()-5>0)
                    time=time.substring(5,time.length()-5);
                letterItem.setTime(time);
                letterItem.setImg(jsonObject.getString("img"+i));           //发送图片
                letterItems.add(letterItem);
            }
            mAdapter=new MessageLetterListAdapter(getContext(),R.layout.message_letter_item,letterItems);
            listView.setAdapter(mAdapter);
            ((com.wang.avi.AVLoadingIndicatorView)view.findViewById(R.id.message_avi)).setVisibility(View.GONE);
        }catch (JSONException e){
            Log.e("TAG", e.getMessage(), e);
        }
    }

    private Bitmap initHead(String img){
        if(!"".equals(img)){
            byte[] imgs= DecodeBase64.decodeBase(img);
            return ImageUtil.Bytes2Bimap(imgs);
        }else{
            //得到该图片的id(name 是该图片的名字，"drawable" 是该图片存放的目录，appInfo.packageName是应用程序的包)
            int resID = getResources().getIdentifier("main_head_1", "drawable", getContext().getApplicationInfo().packageName);
            return BitmapFactory.decodeResource(getResources(), resID);
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
                    searchLettersByReceiverRequest(MainActivity.user.getPhoneNumber());
                }
            }).start();
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    private void searchLettersByReceiverRequest(final String receiver){
        //请求地址
        String url = "http://39.106.154.68:8080/calligrapher/SearchReceiverLettersServlet";    //注①
        final String tag = "SearchReceiverLetters";    //注②

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
                            if (result.equals("success")) {
                                Message message=Message.obtain();
                                message.what=GET_LETTER_SUCCESS;
                                message.obj=jsonObject;
                                handler.sendMessage(message);
                            } else {
                                handler.sendEmptyMessage(GET_LETTER_NULL);
                            }
                        } catch (JSONException e) {
                            //做自己的请求异常操作，如Toast提示（“无网络连接”等）
                            handler.sendEmptyMessage(GET_LETTER_FAIL);
                            Log.e("TAG", e.getMessage(), e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //做自己的响应错误操作，如Toast提示（“请稍后重试”等）
                handler.sendEmptyMessage(GET_LETTER_FAIL);
                Log.e("TAG", error.getMessage(), error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("receiver",receiver);
                return params;
            }
        };
        //设置Tag标签
        request.setTag(tag);
        //将请求添加到队列中
        requestQueue.add(request);
    }
}
