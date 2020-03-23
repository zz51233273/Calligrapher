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
import com.example.hasee.bluecalligrapher.adapter.MessageInformListAdapter;
import com.example.hasee.bluecalligrapher.decodebase64.DecodeBase64;
import com.example.hasee.bluecalligrapher.item.InformItem;
import com.example.hasee.bluecalligrapher.main.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hasee on 2018/5/29.
 */

public class FragmentSetting3 extends Fragment{
    private MessageInformListAdapter mAdapter;
    private List<InformItem> informItems=new ArrayList<InformItem>();
    private final int GET_INF_SUCCESS=1;
    private final int GET_INF_NULL=3;
    private final int GET_INF_FAIL=2;
    private ListView listView;
    private boolean hasLoaded=false;
    private View view;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case GET_INF_SUCCESS:
                    getInforms((JSONObject)msg.obj);
                    break;
                case GET_INF_FAIL:
                    Toast.makeText(getContext(), "服务器繁忙，请稍后重试", Toast.LENGTH_LONG).show();
                    break;
                case GET_INF_NULL:
                    ((com.wang.avi.AVLoadingIndicatorView)view.findViewById(R.id.message_avi)).setVisibility(View.GONE);
                    break;
            }
        }
    };
    public FragmentSetting3(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view=inflater.inflate(R.layout.fg_message_inform,container, false);
        init();
        return view;
    }

    private void init(){
        listView=(ListView)view.findViewById(R.id.inform_list);
        if(hasLoaded){
            ((com.wang.avi.AVLoadingIndicatorView)view.findViewById(R.id.message_avi)).setVisibility(View.GONE);
            listView.setAdapter(mAdapter);
        }
    }


    //查询通知请求
    private void searchInformRequest(final String phonenumber){
        //请求地址
        String url = "http://39.106.154.68:8080/calligrapher/SearchInformsServlet";    //注①
        final String tag = "SearchInforms";    //注②

        RequestQueue requestQueue= Volley.newRequestQueue(getContext().getApplicationContext());
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
                            if (result.equals("success")) {  //刷新评论成功
                                Message message=Message.obtain();
                                message.what=GET_INF_SUCCESS;
                                message.obj=jsonObject;
                                handler.sendMessage(message);
                            } else {
                                handler.sendEmptyMessage(GET_INF_NULL);
                            }
                        } catch (JSONException e) {
                            //做自己的请求异常操作，如Toast提示（“无网络连接”等）
                            handler.sendEmptyMessage(GET_INF_FAIL);
                            Log.e("TAG", e.getMessage(), e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //做自己的响应错误操作，如Toast提示（“请稍后重试”等）
                handler.sendEmptyMessage(GET_INF_FAIL);
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

    //得到通知并添加到列表中
    private void getInforms(JSONObject jsonObject){
        try {
            int len=Integer.parseInt(jsonObject.getString("length"));
            for(int i=len-1;i>=0;i--){
                int dyid=Integer.parseInt(jsonObject.getString("dynamicId"+i));     //操作的动态id
                String phonenumber=jsonObject.getString("phonenumber"+i);            //通知人手机号
                String username=jsonObject.getString("username"+i);                 //通知人用户名
                String context=jsonObject.getString("context"+i);                   //通知内容
                String time=jsonObject.getString("time"+i);
                if(time.length()-5>0)
                    time=time.substring(5,time.length()-5);
                int type=Integer.parseInt(jsonObject.getString("type"+i));           //0代表点赞，1代表评论
                Bitmap head_img;
                if(!(jsonObject.getString("head_img"+i)).equals("")){               //通知人头像
                    byte[] img= DecodeBase64.decodeBase(jsonObject.getString("head_img"+i));
                    head_img=Bytes2Bimap(img);
                }else{
                    //得到该图片的id(name 是该图片的名字，"drawable" 是该图片存放的目录，appInfo.packageName是应用程序的包)
                    int resID = getResources().getIdentifier("main_head_1", "drawable", getContext().getApplicationInfo().packageName);
                    head_img = BitmapFactory.decodeResource(getResources(), resID);
                }
                InformItem informItem;
                if(type==1)
                    informItem=new InformItem(phonenumber,head_img,username,"评论了你动态",dyid,context,time);
                else
                    informItem=new InformItem(phonenumber,head_img,username,"赞了你的动态",dyid,context,time);
                informItems.add(informItem);
            }
            mAdapter=new MessageInformListAdapter(getContext(),R.layout.message_inform_item,informItems);
            listView.setAdapter(mAdapter);
            ((com.wang.avi.AVLoadingIndicatorView)view.findViewById(R.id.message_avi)).setVisibility(View.GONE);
        }catch (JSONException e){
            Log.e("TAG", e.getMessage(), e);
        }
    }

    //byte[]转Bitmap
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

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser && !hasLoaded) {
            hasLoaded=true;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    searchInformRequest(MainActivity.user.getPhoneNumber());
                }
            }).start();
        }
        super.setUserVisibleHint(isVisibleToUser);
    }
}
