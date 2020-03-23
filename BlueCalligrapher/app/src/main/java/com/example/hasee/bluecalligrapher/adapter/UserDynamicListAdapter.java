package com.example.hasee.bluecalligrapher.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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
import com.example.hasee.bluecalligrapher.item.DynamicsItem;
import com.example.hasee.bluecalligrapher.main.CommentActivity;
import com.example.hasee.bluecalligrapher.main.MainActivity;
import com.example.hasee.bluecalligrapher.setting.UserDynamicActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import circleimageview.CircleImageView;

/**
 * Created by hasee on 2018/5/7.
 */

public class UserDynamicListAdapter extends ArrayAdapter<DynamicsItem> {
    private int resourceId;
    private final int THUNMBSUP=1;      //点赞
    private final int COMMENT=2;        //评论
    private final int GETFOCUSFAILED=3;        //点赞失败
    private final int DELETEFAILED=4;        //点赞失败
    private boolean clickFocus=false;   //是否点击了点赞图标
    private int focusCount=0;           //当前点赞的动态的点赞数
    public static DynamicsItem com_dynamicsItem;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case THUNMBSUP:
                    thumbs_up((DynamicsItem)msg.obj);
                    break;
                case COMMENT:
                    clickComment((DynamicsItem)msg.obj);
                    break;
                case GETFOCUSFAILED:
                    Toast.makeText(getContext(), "点赞失败，请查看网络", Toast.LENGTH_LONG).show();
                    break;
                case DELETEFAILED:
                    Toast.makeText(getContext(), "删除失败，请查看网络", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    public UserDynamicListAdapter(Context context, int textViewResourceId, List<DynamicsItem> objects){
        super(context,textViewResourceId,objects);
        resourceId=textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DynamicsItem dynamicsItem = getItem(position); // 获取当前项的index实例
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.head = (CircleImageView) view.findViewById (R.id.main_item_head);
            viewHolder.writer = (TextView) view.findViewById (R.id.main_item_writer);
            viewHolder.context=(TextView)view.findViewById(R.id.main_item_content);
            viewHolder.img0=(ImageView)view.findViewById(R.id.img0);
            viewHolder.img1=(ImageView)view.findViewById(R.id.img1);
            viewHolder.img2=(ImageView)view.findViewById(R.id.img2);
            viewHolder.img3=(ImageView)view.findViewById(R.id.img3);
            viewHolder.img4=(ImageView)view.findViewById(R.id.img4);
            viewHolder.img5=(ImageView)view.findViewById(R.id.img5);
            viewHolder.focus_number=(TextView)view.findViewById(R.id.main_item_good_number);
            viewHolder.count_number=(TextView)view.findViewById(R.id.main_item_preview_number);
            view.setTag(viewHolder); // 将ViewHolder存储在View中
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag(); // 重新获取ViewHolder
        }
        viewHolder.head.setImageBitmap(dynamicsItem.getHeadId());
        viewHolder.writer.setText(dynamicsItem.getWriterName());
        viewHolder.context.setText(dynamicsItem.getContent());
        viewHolder.img0.setImageBitmap(dynamicsItem.getImg0());
        viewHolder.img1.setImageBitmap(dynamicsItem.getImg1());
        viewHolder.img2.setImageBitmap(dynamicsItem.getImg2());
        viewHolder.img3.setImageBitmap(dynamicsItem.getImg3());
        viewHolder.img4.setImageBitmap(dynamicsItem.getImg4());
        viewHolder.img5.setImageBitmap(dynamicsItem.getImg5());
        viewHolder.focus_number.setText(dynamicsItem.getFocusCount()+"");
        viewHolder.count_number.setText(dynamicsItem.getCommentCount()+"");
        if(dynamicsItem.isFocus()){
            if(clickFocus){
                ((TextView)view.findViewById(R.id.main_item_good_number)).setText(focusCount+"");
                clickFocus=false;
            }
            ((ImageView)view.findViewById(R.id.main_item_good)).setImageResource(R.drawable.full_love);
        }else{
            if(clickFocus){
                ((TextView)view.findViewById(R.id.main_item_good_number)).setText(focusCount+"");
                clickFocus=false;
            }
            ((ImageView)view.findViewById(R.id.main_item_good)).setImageResource(R.drawable.love);
        }
        addListener(view,dynamicsItem);
        return view;
    }

    //监听组件
    private void addListener(View view,final DynamicsItem dynamicsItem){
        ((ImageView)view.findViewById(R.id.main_item_good)).setOnClickListener(     //点击点赞图标
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Message message=Message.obtain();
                        message.what=THUNMBSUP;
                        message.obj=dynamicsItem;
                        handler.sendMessage(message);
                    }
                });
        ((ImageView)view.findViewById(R.id.main_item_preview)).setOnClickListener(  //点击评论图标
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Message message=Message.obtain();
                        message.what=COMMENT;
                        message.obj=dynamicsItem;
                        handler.sendMessage(message);
                    }
                });
        ((ImageView)view.findViewById(R.id.main_item_delete)).setOnClickListener(  //点击评论图标
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                deleteDynamic(dynamicsItem);
                            }
                        }).start();
                    }
                });
    }

    private void thumbs_up(final DynamicsItem dynamicsItem){  //点击了点赞图标
        if(null== MainActivity.user){
            Toast.makeText(getContext(), "您还未登录呢", Toast.LENGTH_LONG).show();
        }
        else{
            clickFocus=true;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if(dynamicsItem.isFocus()){
                        focusDynamicRequest(MainActivity.user.getPhoneNumber(),0 ,dynamicsItem.getDynamicId() ,dynamicsItem);      //0代表取消点赞
                    }else{
                        focusDynamicRequest(MainActivity.user.getPhoneNumber(),1 ,dynamicsItem.getDynamicId() ,dynamicsItem);      //1代表点赞
                    }
                }
            }).start();
        }
    }

    private void clickComment(DynamicsItem dynamicsItem){        // 点击了评论图标
        Intent i=new Intent(getContext(), CommentActivity.class);
        i.putExtra("user_dynamic",true);
        this.com_dynamicsItem=dynamicsItem;
        getContext().startActivity(i);
    }

    private void focusDynamicRequest(final String phonenumber, final int choose ,final int dynamicId ,final DynamicsItem dynamicsItem){
        //请求地址
        String url = "http://39.106.154.68:8080/calligrapher/FocusDynamicServlet";    //注①
        String tag = "focusDynamic";    //注②
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
                            if (result.equals("success")) {  //点赞或取消点赞成功
                                focusCount=Integer.parseInt(jsonObject.getString("focuscount"));
                                if(dynamicsItem.isFocus()){
                                    dynamicsItem.setFocus(false);
                                }else{
                                    dynamicsItem.setFocus(true);
                                }
                                notifyDataSetChanged();
                            } else {
                                handler.sendEmptyMessage(GETFOCUSFAILED);
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
                params.put("phonenumber",phonenumber);
                params.put("choose",choose+"");
                params.put("dynamicId",dynamicId+"");
                return params;
            }
        };
        //设置Tag标签
        request.setTag(tag);
        //将请求添加到队列中
        requestQueue.add(request);
    }

    private void deleteDynamic(final DynamicsItem dynamicsItem){
        //请求地址
        String url = "http://39.106.154.68:8080/calligrapher/DeleteDynamicServlet";    //注①
        String tag = "DeleteDynamic";    //注②
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
                            if (result.equals("success")) {  //删除成功
                                UserDynamicActivity.dynamicsItems.remove(dynamicsItem);
                                notifyDataSetChanged();
                            } else {
                                handler.sendEmptyMessage(DELETEFAILED);
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
                params.put("dynamicId",dynamicsItem.getDynamicId()+"");
                return params;
            }
        };
        //设置Tag标签
        request.setTag(tag);
        //将请求添加到队列中
        requestQueue.add(request);
    }
    class ViewHolder{
        CircleImageView head;   //头像
        TextView writer;        //作者
        TextView context;       //动态内容
        ImageView img0;ImageView img1;ImageView img2;   //六张图片
        ImageView img3;ImageView img4;ImageView img5;
        TextView focus_number;  //点赞数
        TextView count_number;  //评论数
    }
}
