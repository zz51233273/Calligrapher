package com.example.hasee.bluecalligrapher.userinfo;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.example.hasee.bluecalligrapher.adapter.MyFragmentAdapterUserInfo;
import com.example.hasee.bluecalligrapher.encodebase64.EncodeBase64;
import com.example.hasee.bluecalligrapher.main.MainActivity;
import com.example.hasee.bluecalligrapher.utils.ImageUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import circleimageview.CircleImageView;

/**
 * Created by hasee on 2018/6/16.
 */

public class UserselfInfoActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener,
        ViewPager.OnPageChangeListener{
    private RadioButton rb1,rb2;
    private RadioGroup rg_tab_bar;
    private ViewPager vpager;
    private final int PAGE_ONE = 0;
    private final int PAGE_TWO = 1;
    private final int HAS_NOT_FOLLOWED=2;
    private final int NETWORK_ERROR=3;
    private final int SCAN_OPEN_PHONE =4;
    private final int PHONE_CROP=5;
    private final int CHANGE_PHOTO=7;
    private final int CHANGE_SUCCESS=8;
    private final int CHANGE_FAIL=9;
    private MyFragmentAdapterUserInfo mAdapter;
    private CircleImageView head;
    private TextView user_info;
    private String phonenumber= MainActivity.user.getPhoneNumber();
    private Uri mCutUri;                    //图片路径
    private Handler mhandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case HAS_NOT_FOLLOWED:
                    updateFollowText((JSONObject)msg.obj);
                    break;
                case NETWORK_ERROR:
                    Toast.makeText(getApplicationContext(), "关注失败，请查看网络", Toast.LENGTH_LONG).show();
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 1000);
                    break;
                case CHANGE_PHOTO:
                    Bitmap bitmap= ImageUtil.resizeImage((Bitmap)msg.obj,100,100);
                    bitmap=ImageUtil.getCircleBitmap(bitmap);
                    MainActivity.head_bitmap=bitmap;
                    final byte[] head_img=ImageUtil.getImgBytes(bitmap);
                    head.setImageBitmap(bitmap);
                    if(null!=head_img){
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                MainActivity.user.setHead_img(head_img);
                                String str_head_img= EncodeBase64.encodeBase(head_img);
                                sendHandPicRequest(MainActivity.user.getPhoneNumber(),str_head_img);
                            }
                        }).start();
                    }
                    break;
                case CHANGE_SUCCESS:       // 发送头像成功
                    Toast.makeText(getApplicationContext(), "更改成功", Toast.LENGTH_LONG).show();
                    break;
                case CHANGE_FAIL:
                    Toast.makeText(getApplicationContext(), "更改失败，请查看网络状态", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dynamic_userself_info);
        init();
    }

    private void init(){
        user_info=(TextView)findViewById(R.id.user_info);
        new Thread(new Runnable() {
            @Override
            public void run() {
                searchFollowInfoRequest();
            }
        }).start();
        rg_tab_bar=findViewById(R.id.rg_tab_bar);
        rb1=findViewById(R.id.rb_about);
        rb1.setTextColor(getResources().getColor(R.color.bilibili_red));
        rb2=findViewById(R.id.rb_dynamic);
        rg_tab_bar.setOnCheckedChangeListener(this);
        mAdapter = new MyFragmentAdapterUserInfo(getSupportFragmentManager());
        vpager=(ViewPager)findViewById(R.id.vpager);
        vpager.setAdapter(mAdapter);
        vpager.setCurrentItem(PAGE_ONE);
        vpager.addOnPageChangeListener(this);
        head=findViewById(R.id.h_head);
        ((TextView)findViewById(R.id.user_name)).setText(getIntent().getStringExtra("username"));
        getUserHead();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_about:
                vpager.setCurrentItem(PAGE_ONE);
                break;
            case R.id.rb_dynamic:
                vpager.setCurrentItem(PAGE_TWO);
                break;
        }
    }

    //重写ViewPager页面切换的处理方法
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        //state的状态有三个，0表示什么都没做，1正在滑动，2滑动完毕
        if (state == 2) {
            switch (vpager.getCurrentItem()) {
                case PAGE_ONE:
                    rb1.setChecked(true);
                    rb1.setTextColor(getResources().getColor(R.color.bilibili_red));
                    rb2.setTextColor(Color.BLACK);
                    break;
                case PAGE_TWO:
                    rb2.setChecked(true);
                    rb2.setTextColor(getResources().getColor(R.color.bilibili_red));
                    rb1.setTextColor(Color.BLACK);
                    break;
            }
        }
    }

    private void getUserHead(){
        byte[] bitmapByte=getIntent().getByteArrayExtra("head");
        if(null!=bitmapByte){
            head.setImageBitmap(BitmapFactory.decodeByteArray(bitmapByte, 0, bitmapByte.length));
        }else{
            head.setImageResource(R.drawable.main_head_1);
        }
        head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createPhotoDialog();
            }
        });
    }

    //创建选择图片对话框
    private void createPhotoDialog(){
        final Dialog mCameraDialog = new Dialog(this, R.style.ActionSheetDialogStyle);
        LinearLayout root = (LinearLayout) LayoutInflater.from(this).inflate(
                R.layout.select_photo_dialog, null);
        root.findViewById(R.id.choosePhoto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestWritePermission();
                //打开相册
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, SCAN_OPEN_PHONE);
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
        lp.width = (int) getResources().getDisplayMetrics().widthPixels; // 宽度
        root.measure(0, 0);
        lp.height = root.getMeasuredHeight();
        lp.alpha = 9f; // 透明度
        dialogWindow.setAttributes(lp);
        mCameraDialog.show();
    }

    /*图片裁剪*/
    @NonNull
    private Intent CutForPhoto(Uri uri) {
        //直接裁剪
        Intent intent = new Intent("com.android.camera.action.CROP");
        //设置裁剪之后的图片路径文件
        File cutfile = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),
                "cutcamera.png"); //随便命名一个
/*            if (cutfile.exists()){ //如果已经存在，则先删除,这里应该是上传到服务器，然后再删除本地的，没服务器，只能这样了
            cutfile.delete();
        }
        cutfile.createNewFile();*/
        //初始化 uri
        Uri imageUri = uri; //返回来的 uri
        Uri outputUri = null; //真实的 uri
        outputUri = Uri.fromFile(cutfile);
        mCutUri = outputUri;
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop",true);
        // aspectX,aspectY 是宽高的比例，这里设置正方形
        intent.putExtra("aspectX",1);
        intent.putExtra("aspectY",1);
        //设置要裁剪的宽高
        intent.putExtra("outputX", 200); //200dp
        intent.putExtra("outputY",200);
        intent.putExtra("scale",true);
        //如果图片过大，会导致oom，这里设置为false
        intent.putExtra("return-data",false);
        if (imageUri != null) {
            intent.setDataAndType(imageUri, "image/*");
        }
        if (outputUri != null) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
        }
        intent.putExtra("noFaceDetection", true);
        //压缩图片
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
        return intent;
    }

    //回调
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            switch (requestCode){
                case SCAN_OPEN_PHONE: //从相册图片后返回的uri
                    //启动裁剪
                    startActivityForResult(CutForPhoto(data.getData()),PHONE_CROP);
                    break;
                case PHONE_CROP:
                    try {
                        //获取裁剪后的图片，并显示出来
                        Bitmap bitmap = BitmapFactory.decodeStream(
                                getContentResolver().openInputStream(mCutUri));
                        Message message=Message.obtain();
                        message.obj=bitmap;
                        message.what=CHANGE_PHOTO;
                        mhandler.sendMessage(message);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    private void requestWritePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            int REQUEST_CODE_CONTACT = 101;
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            //验证是否许可权限
            for (String str : permissions) {
                if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
                    return;
                }
            }
        }
    }

    //查询关注信息
    private void searchFollowInfoRequest(){
        //请求地址
        String url = "http://39.106.154.68:8080/calligrapher/SearchFollowInfoServlet";    //注①
        String tag = "SearchFollowInfo";    //注②

        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
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
                            if (result.equals("failed")) {  //登录成功后
                                Message message = Message.obtain();
                                message.obj = jsonObject;
                                message.what = HAS_NOT_FOLLOWED;
                                mhandler.sendMessage(message);
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
                mhandler.sendEmptyMessage(NETWORK_ERROR);
                Log.e("TAG", error.getMessage(), error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("phonenumber", phonenumber);  //注⑥
                params.put("selfphonenumber","");
                return params;
            }
        };
        //设置Tag标签
        request.setTag(tag);
        //将请求添加到队列中
        requestQueue.add(request);
    }
    private void sendHandPicRequest(final String phonenumber,final String head_img){
        //请求地址
        String url = "http://39.106.154.68:8080/calligrapher/SendUserHeadServlet";    //注①
        String tag = "SendUserHead";    //注②

        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
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
                            if (result.equals("success")) {  //发表动态成功
                                mhandler.sendEmptyMessage(CHANGE_SUCCESS);
                            } else {
                                mhandler.sendEmptyMessage(CHANGE_FAIL);
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
                params.put("head_img",head_img);
                params.put("phonenumber",phonenumber);
                return params;
            }
        };

        //设置Tag标签
        request.setTag(tag);

        //将请求添加到队列中
        requestQueue.add(request);
    }

    private void updateFollowText(JSONObject jsonObject){
        int followed=0;     //关注人数
        int follower=0;     //粉丝人数
        try {
            followed=Integer.parseInt(jsonObject.getString("followed"));
            follower=Integer.parseInt(jsonObject.getString("follower"));
        }catch (JSONException e){
            Log.e("TAG", e.getMessage(), e);
        }
        user_info.setText("关注 "+followed+" | 粉丝 "+follower);
    }
}
