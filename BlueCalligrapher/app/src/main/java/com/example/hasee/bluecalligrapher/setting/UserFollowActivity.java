package com.example.hasee.bluecalligrapher.setting;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import com.example.hasee.bluecalligrapher.adapter.UserFollowListAdapter;
import com.example.hasee.bluecalligrapher.decodebase64.DecodeBase64;
import com.example.hasee.bluecalligrapher.item.FollowItem;
import com.example.hasee.bluecalligrapher.main.MainActivity;
import com.example.hasee.bluecalligrapher.utils.ImageUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by hasee on 2018/6/16.
 */

public class UserFollowActivity extends AppCompatActivity{
    private ListView listView;
    private UserFollowListAdapter mAdapter;
    public static List<FollowItem> followItems=new ArrayList<FollowItem>();
    private final int SEARCH_SUCCESS=1;
    private final int SEARCH_FAILED=2;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SEARCH_SUCCESS:
                    getFollowed((JSONObject)msg.obj);
                    break;
                case SEARCH_FAILED:
                    Toast.makeText(getApplicationContext(), "关注失败，请查看网络", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_follow);
        init();
    }

    private void init(){
        listView=(ListView)findViewById(R.id.listview);
        new Thread(new Runnable() {
            @Override
            public void run() {
                searchUserFollowed(MainActivity.user.getPhoneNumber());
            }
        }).start();
    }

    private void getFollowed(JSONObject jsonObject){
        try {
            followItems.clear();
            int len=Integer.parseInt(jsonObject.getString("len"));
            for(int i=len-1 ; i>=0 ; i--){
                String followed=jsonObject.getString("followed"+i);
                String head=jsonObject.getString("followed_head"+i);
                String username=jsonObject.getString("followed_name"+i);
                Bitmap head_img=null;
                if(!head.equals("")){
                    byte[] head_imgs = DecodeBase64.decodeBase(head);     //动态作者头像
                    head_img= ImageUtil.Bytes2Bimap(head_imgs);
                }else{
                    //得到该图片的id(name 是该图片的名字，"drawable" 是该图片存放的目录，appInfo.packageName是应用程序的包)
                    int resID = getResources().getIdentifier("main_head_1", "drawable", getApplicationInfo().packageName);
                    head_img = BitmapFactory.decodeResource(getResources(), resID);
                }
                FollowItem followItem=new FollowItem();
                followItem.setfollowed(followed);
                followItem.setFollower_name(username);
                followItem.setFollower_head(head_img);
                followItems.add(followItem);
            }
            mAdapter=new UserFollowListAdapter(this,R.layout.setting_follow_item,followItems);
            listView.setAdapter(mAdapter);
        }catch (JSONException e){
            Log.e("TAG", e.getMessage(), e);
        }
    }

    private void searchUserFollowed(final String phonenumber){
        //请求地址
        String url = "http://39.106.154.68:8080/calligrapher/SearchUserFollowedServlet";    //注①
        String tag = "SearchUserFollowed";    //注②
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
                            Message message=Message.obtain();
                            message.obj=jsonObject;
                            message.what=SEARCH_SUCCESS;
                            handler.sendMessage(message);
                        } catch (JSONException e) {
                            //做自己的请求异常操作，如Toast提示（“无网络连接”等）
                            Log.e("TAG", e.getMessage(), e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //做自己的响应错误操作，如Toast提示（“请稍后重试”等）
                handler.sendEmptyMessage(SEARCH_FAILED);
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
}
