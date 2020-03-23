package com.example.hasee.bluecalligrapher.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
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
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import com.example.hasee.bluecalligrapher.userinfo.UserInfoActivity;
import com.example.hasee.bluecalligrapher.userinfo.UserselfInfoActivity;
import com.example.hasee.bluecalligrapher.utils.CommomDialog;
import com.example.hasee.bluecalligrapher.utils.ImageUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import circleimageview.CircleImageView;

/**
 * Created by hasee on 2018/4/4.
 */

public class MainListAdapter extends ArrayAdapter<DynamicsItem>{
    private int resourceId;
    private final int THUNMBSUP=1;      //点赞
    private final int COMMENT=2;        //评论
    private final int GETFOCUSFAILED=3;        //点赞失败
    private final int REPORTSUCCESS=4;        //举报成功
    private final int REPORTFAILED=5;        //举报失败
    private boolean clickFocus=false;   //是否点击了点赞图标
    private int focusCount=0;           //当前点赞的动态的点赞数
    public static DynamicsItem com_dynamicsItem;
    private Dialog dialog = new Dialog(getContext(), R.style.Theme_AppCompat);
    private ImageView mImageView;
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
                    Toast.makeText(getContext(), "点赞失败，请刷新重试", Toast.LENGTH_LONG).show();
                    break;
                case REPORTSUCCESS:
                    Toast.makeText(getContext(), "举报成功，相关人员会及时处理", Toast.LENGTH_LONG).show();
                    break;
                case REPORTFAILED:
                    Toast.makeText(getContext(), "举报失败，请查看网络", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    public MainListAdapter(Context context, int textViewResourceId, List<DynamicsItem> objects){
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
            //viewHolder.time=(TextView)view.findViewById(R.id.main_item_date);
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
        //viewHolder.time.setText(dynamicsItem.getTime());
        viewHolder.img0.setImageBitmap(ImageUtil.resizeDynamicImage(dynamicsItem.getImg0()));
        viewHolder.img1.setImageBitmap(ImageUtil.resizeDynamicImage(dynamicsItem.getImg1()));
        viewHolder.img2.setImageBitmap(ImageUtil.resizeDynamicImage(dynamicsItem.getImg2()));
        viewHolder.img3.setImageBitmap(ImageUtil.resizeDynamicImage(dynamicsItem.getImg3()));
        viewHolder.img4.setImageBitmap(ImageUtil.resizeDynamicImage(dynamicsItem.getImg4()));
        viewHolder.img5.setImageBitmap(ImageUtil.resizeDynamicImage(dynamicsItem.getImg5()));
        viewHolder.focus_number.setText(dynamicsItem.getFocusCount()+"");
        viewHolder.count_number.setText(dynamicsItem.getCommentCount()+"");
        if(dynamicsItem.isFocus()){
            if(clickFocus){
                ((TextView)view.findViewById(R.id.main_item_good_number)).setText(focusCount+"");
                clickFocus=false;
            }else{
                ((TextView)view.findViewById(R.id.main_item_good_number)).setText(dynamicsItem.getFocusCount()+"");
            }
            ((ImageView)view.findViewById(R.id.main_item_good)).setImageResource(R.drawable.full_love);
        }else{
            if(clickFocus){
                ((TextView)view.findViewById(R.id.main_item_good_number)).setText(focusCount+"");
                clickFocus=false;
            }else{
                ((TextView)view.findViewById(R.id.main_item_good_number)).setText(dynamicsItem.getFocusCount()+"");
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
        ((ImageView)view.findViewById(R.id.more)).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                createSelectDialog(dynamicsItem.getDynamicId());
            }
        });
        ((CircleImageView)view.findViewById(R.id.main_item_head)).setOnClickListener(new View.OnClickListener(){      //点击头像
            @Override
            public void onClick(View v) {
                searchUserInfo(dynamicsItem);
            }
        });
        ((ImageView)view.findViewById(R.id.img0)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getBigImg(dynamicsItem.getImg0());
            }
        });
        ((ImageView)view.findViewById(R.id.img1)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                getBigImg(dynamicsItem.getImg1());
            }
        });
        ((ImageView)view.findViewById(R.id.img2)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getBigImg(dynamicsItem.getImg2());
            }
        });
        ((ImageView)view.findViewById(R.id.img3)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getBigImg(dynamicsItem.getImg3());
            }
        });
        ((ImageView)view.findViewById(R.id.img4)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getBigImg(dynamicsItem.getImg4());
            }
        });
        ((ImageView)view.findViewById(R.id.img5)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getBigImg(dynamicsItem.getImg5());
            }
        });
    }

    //得到大图
    private void getBigImg(Bitmap bitmap){
        if(null!=bitmap){
            mImageView= new ImageView(getContext());
            //宽高
            mImageView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            //设置Padding
            mImageView.setPadding(20,20,20,20);
            Drawable drawable = new BitmapDrawable(bitmap);
            mImageView.setImageDrawable(drawable);
            dialog.setContentView(mImageView);
            dialog.show();
            mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }
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
        this.com_dynamicsItem=dynamicsItem;
        getContext().startActivity(i);
    }

    //创建多选对话框
    private void createSelectDialog(final int dyId){
        final Dialog mCameraDialog = new Dialog(getContext(), R.style.ActionSheetDialogStyle);
        LinearLayout root = (LinearLayout) LayoutInflater.from(getContext()).inflate(
                R.layout.select_dynamic_dialog, null);
        root.findViewById(R.id.report).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(null==MainActivity.user)
                    Toast.makeText(getContext(), "您还未登录呢", Toast.LENGTH_LONG).show();
                else
                    createReportDialog(dyId);
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

    //创建多选对话框
    private void createReportDialog(final int dyId){
        final Dialog mCameraDialog = new Dialog(getContext(), R.style.ActionSheetDialogStyle);
        LinearLayout root = (LinearLayout) LayoutInflater.from(getContext()).inflate(
                R.layout.select_report_dialog, null);
        root.findViewById(R.id.report_0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createConfirmDialog("确认举报？",dyId , "恶意攻击谩骂");
                mCameraDialog.dismiss();
            }
        });
        root.findViewById(R.id.report_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createConfirmDialog("确认举报？",dyId , "营销广告");
                mCameraDialog.dismiss();
            }
        });
        root.findViewById(R.id.report_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createConfirmDialog("确认举报？",dyId , "淫秽色情");
                mCameraDialog.dismiss();
            }
        });
        root.findViewById(R.id.report_3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createConfirmDialog("确认举报？",dyId , "政治反动");
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
    private void createConfirmDialog(String title, final int dyid , final String context){
        new CommomDialog(getContext(),R.style.common_dialog ,title, new CommomDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog,boolean confirm) {
                if(confirm){    //点击确定
                    reportDynamic(dyid,context);
                    dialog.dismiss();
                }
            }
        }).setPositiveButton("确认").show();
    }

    private void reportDynamic(final int dyid, final String context){
        new Thread(new Runnable() {
            @Override
            public void run() {
                reportDyRequest(dyid,context,MainActivity.user.getPhoneNumber());
            }
        }).start();
    }

    //点击头像后查询用户信息
    private void searchUserInfo(DynamicsItem dynamicsItem){
        Intent i=null;
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        dynamicsItem.getHeadId().compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte [] bitmapByte =baos.toByteArray();
        //用户点击自己头像
        if(null!=MainActivity.user && dynamicsItem.getPhonenumber().equals(MainActivity.user.getPhoneNumber())){
            i=new Intent(getContext(), UserselfInfoActivity.class);
        }else{
            i=new Intent(getContext(), UserInfoActivity.class);
        }
        i.putExtra("head",bitmapByte);
        i.putExtra("username",dynamicsItem.getWriterName());
        i.putExtra("phonenumber",dynamicsItem.getPhonenumber());
        getContext().startActivity(i);
    }

    //点赞请求
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
                                    MainActivity.dynamicsItems.get(MainActivity.dynamicsItems.indexOf(dynamicsItem)).setFocus(false);
                                    MainActivity.dynamicsItems.get(MainActivity.dynamicsItems.indexOf(dynamicsItem)).setFocusCount(focusCount);
                                    //dynamicsItem.setFocus(false);
                                }else{
                                    MainActivity.dynamicsItems.get(MainActivity.dynamicsItems.indexOf(dynamicsItem)).setFocus(true);
                                    MainActivity.dynamicsItems.get(MainActivity.dynamicsItems.indexOf(dynamicsItem)).setFocusCount(focusCount);
                                    //dynamicsItem.setFocus(true);
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

    //发送举报请求
    private void reportDyRequest(final int dyId,final String context,final String phonenumber){
        //请求地址
        String url = "http://39.106.154.68:8080/calligrapher/ReportDynamicServlet";    //注①
        String tag = "ReportDynamic";    //注②
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
                            if (result.equals("success")) {  //举报成功
                                handler.sendEmptyMessage(REPORTSUCCESS);
                            } else {
                                handler.sendEmptyMessage(REPORTFAILED);
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
                params.put("dynamicId",dyId+"");
                params.put("context",context);
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
        //TextView time;          //发表时间
        ImageView img0;ImageView img1;ImageView img2;   //六张图片
        ImageView img3;ImageView img4;ImageView img5;
        TextView focus_number;  //点赞数
        TextView count_number;  //评论数
    }

    public void updataView(long id, ListView listView) {
        if (listView != null) {
            int start = listView.getFirstVisiblePosition();
            for (int i = start, j = listView.getLastVisiblePosition(); i <= j; i++)
                if (id == listView.getItemIdAtPosition(i)) {
                    View view = listView.getChildAt(i - start);
                    getView(i, view, listView);
                    break;
                }
        }
    }
}

