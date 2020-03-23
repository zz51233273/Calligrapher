package com.example.hasee.bluecalligrapher.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.example.hasee.bluecalligrapher.item.FollowItem;
import com.example.hasee.bluecalligrapher.letter.SendLetterActivity;
import com.example.hasee.bluecalligrapher.main.MainActivity;
import com.example.hasee.bluecalligrapher.setting.UserFollowActivity;
import com.example.hasee.bluecalligrapher.userinfo.UserInfoActivity;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import circleimageview.CircleImageView;

/**
 * Created by hasee on 2018/6/16.
 */

public class UserFollowListAdapter extends ArrayAdapter<FollowItem>{
    private int resourceId;
    private FollowItem pubFollowItem;
    private final int CANCEL_SUCCESS=1;
    private final int CANCEL_FAIL=2;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what){
                case CANCEL_SUCCESS:
                    UserFollowActivity.followItems.remove(pubFollowItem);
                    notifyDataSetChanged();
                    Toast.makeText(getContext(), "取消成功", Toast.LENGTH_LONG).show();
                    break;
                case CANCEL_FAIL:
                    Toast.makeText(getContext(), "取消失败，请查看网络", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };
    public UserFollowListAdapter(Context context, int textViewResourceId, List<FollowItem> objects){
        super(context,textViewResourceId,objects);
        resourceId=textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FollowItem followItem = getItem(position); // 获取当前项的index实例
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.followed_head = (CircleImageView) view.findViewById (R.id.setting_follow_head);
            viewHolder.followed_name = (TextView) view.findViewById (R.id.setting_follow_name);
            view.setTag(viewHolder); // 将ViewHolder存储在View中
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag(); // 重新获取ViewHolder
        }
        viewHolder.followed_head.setImageBitmap(followItem.getFollower_head());
        viewHolder.followed_name.setText(followItem.getFollower_name());
        addListener(view,followItem);
        return view;
    }

    private void addListener(View view,final FollowItem followItem){
        ((CircleImageView) view.findViewById (R.id.setting_follow_head)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchUserInfo(followItem);
            }
        });
        ((ImageView)view.findViewById(R.id.more)).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                pubFollowItem=followItem;
                createSelectDialog(followItem.getfollowed());
            }
        });
    }

    //创建多选对话框
    private void createSelectDialog(final String followed){
        final Dialog mCameraDialog = new Dialog(getContext(), R.style.ActionSheetDialogStyle);
        LinearLayout root = (LinearLayout) LayoutInflater.from(getContext()).inflate(
                R.layout.select_user_follow_dialog, null);
        root.findViewById(R.id.send_letter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getContext(), SendLetterActivity.class);
                i.putExtra("followed",followed);
                getContext().startActivity(i);
                mCameraDialog.dismiss();
            }
        });
        root.findViewById(R.id.cancel_follow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        cancelFollowRequest(followed);
                    }
                }).start();
                mCameraDialog.dismiss();
            }
        });
        root.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCameraDialog.dismiss();
            }
        });
        mCameraDialog.setContentView(root);
        Window dialogWindow = mCameraDialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        lp.x = 0; // 新位置X坐标
        lp.y = -20; // 新位置Y坐标
        lp.width = (int) getContext().getResources().getDisplayMetrics().widthPixels; // 宽度
        root.measure(0, 0);
        lp.height = root.getMeasuredHeight();
        lp.alpha = 9f; // 透明度
        dialogWindow.setAttributes(lp);
        mCameraDialog.show();
    }

    //点击头像后查询用户信息
    private void searchUserInfo(FollowItem followItem){
        Intent i=null;
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        followItem.getFollower_head().compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte [] bitmapByte =baos.toByteArray();
        i=new Intent(getContext(), UserInfoActivity.class);
        i.putExtra("head",bitmapByte);
        i.putExtra("username",followItem.getFollower_name());
        i.putExtra("phonenumber",followItem.getfollowed());
        getContext().startActivity(i);
    }

    private void cancelFollowRequest(final String followed){
        //请求地址
        String url = "http://39.106.154.68:8080/calligrapher/ChangeFollowServlet";    //注①
        String tag = "ChangeFollow";    //注②

        RequestQueue requestQueue= Volley.newRequestQueue(getContext().getApplicationContext());
        //取得请求队列
        //防止重复请求，所以先取消tag标识的请求队列
        requestQueue.cancelAll(tag);

        //创建StringRequest，定义字符串请求的请求方式为POST(省略第一个参数会默认为GET方式)
        final StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        handler.sendEmptyMessage(CANCEL_SUCCESS);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //做自己的响应错误操作，如Toast提示（“请稍后重试”等）
                handler.sendEmptyMessage(CANCEL_FAIL);
                Log.e("TAG", error.getMessage(), error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("phonenumber", followed);  //注⑥
                params.put("selfphonenumber", MainActivity.user.getPhoneNumber());
                params.put("followed","1");
                return params;
            }
        };
        //设置Tag标签
        request.setTag(tag);

        //将请求添加到队列中
        requestQueue.add(request);
    }

    class ViewHolder{
        CircleImageView followed_head;       //关注人头像
        TextView followed_name;        //关注人姓名
    }
}
