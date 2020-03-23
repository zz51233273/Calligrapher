package com.example.hasee.bluecalligrapher.letter;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
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
import com.example.hasee.bluecalligrapher.encodebase64.EncodeBase64;
import com.example.hasee.bluecalligrapher.main.MainActivity;
import com.example.hasee.bluecalligrapher.utils.CommomDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import me.nereo.multi_image_selector.MultiImageSelector;

/**
 * Created by hasee on 2018/6/18.
 */

public class SendLetterActivity extends AppCompatActivity implements View.OnClickListener,View.OnTouchListener{
    private TextView back;      //返回
    private TextView send;     //发送
    private boolean isSend =false;    //是否正在编写
    private ImageView write_dynamic_img;
    private ImageView img;       //图片
    private ImageView imgdele;   //删除图片的图标
    private Dialog dialog;
    private ImageView mImageView;   //大图显示
    private String followed;
    private static final int MAX_SELECT_COUNT = 1;      //最多选择6张图
    private static final int REQUEST_IMAGE = 2;
    protected static final int REQUEST_STORAGE_READ_ACCESS_PERMISSION = 101;
    protected static final int REQUEST_STORAGE_WRITE_ACCESS_PERMISSION = 102;
    private final int SEND_SUCCESS=3;
    private final int SEND_FAILED=4;
    private ArrayList<String> paths;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what){
                case SEND_SUCCESS:
                    ((com.wang.avi.AVLoadingIndicatorView)findViewById(R.id.send_letter_avi)).setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "发送成功", Toast.LENGTH_LONG).show();
                    backUserFollowActivity();
                    break;
                case SEND_FAILED:
                    Toast.makeText(getApplicationContext(), "发送失败，请查看网络状态", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_letter_layout);
        init();
    }
    void init(){
        followed=getIntent().getStringExtra("followed");
        back=(TextView)findViewById(R.id.back);
        back.setOnClickListener(this);
        back.setOnTouchListener(this);
        send=(TextView)findViewById(R.id.send);
        send.setOnClickListener(this);
        write_dynamic_img=(ImageView)findViewById(R.id.send_letter_img);
        write_dynamic_img.setOnClickListener(this);
        dialog = new Dialog(this,R.style.Theme_AppCompat);
        img=(ImageView)findViewById(R.id.img);
        imgdele=(ImageView)findViewById(R.id.imgdele);
        img.setOnClickListener(this);
        imgdele.setOnClickListener(this);
        dialog.setTitle("正在发送");
        findViewById(R.id.send_letter).setOnClickListener(this);
        findViewById(R.id.send_letter_content).setOnClickListener(this);
    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(v.getId() == R.id.back){
            if(event.getAction() == MotionEvent.ACTION_UP){     //抬起
                if(isEditing())
                    createCancelDialog("退出这次编辑？");
                else
                    finish();
            }
            if(event.getAction() == MotionEvent.ACTION_DOWN){   //按下
                back.setTextColor(getResources().getColor(R.color.pink));
            }
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_IMAGE){
            if(resultCode == RESULT_OK){
                showContent(data);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //显示从相册中返回的图片
    private void showContent(Intent data) {
        paths = data.getStringArrayListExtra(MultiImageSelector.EXTRA_RESULT);
        if (paths.isEmpty()) {
            return;
        }
        for(String path : paths) {
            //Uri uri = Uri.parse(path);
            setPic(img,path);
            imgdele.setVisibility(View.VISIBLE);
        }

    }

    //选择图片
    public void selectImg() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN // Permission was added in API Level 16
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
                    getString(R.string.mis_permission_rationale),
                    REQUEST_STORAGE_READ_ACCESS_PERMISSION);
        }else {
            boolean showCamera = false;
            int maxNum = MAX_SELECT_COUNT;
            MultiImageSelector selector = MultiImageSelector.create(SendLetterActivity.this);
            selector.showCamera(showCamera);
            selector.count(maxNum);
            selector.multi();
            selector.origin(paths);
            selector.start(SendLetterActivity.this, REQUEST_IMAGE);
        }
    }

    //动态的ImageView
    private void getImageView(ImageView imageView){
        mImageView= new ImageView(this);
        //宽高
        mImageView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        //设置Padding
        mImageView.setPadding(20,20,20,20);
        mImageView.setImageDrawable(imageView.getDrawable());
        dialog.setContentView(mImageView);
        dialog.show();
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onClick(View v){
        int id=v.getId();
        switch (id){
            case R.id.send:                   //发送动态给服务器
                if(!((TextView)findViewById(R.id.send_letter_content)).getText().toString().equals("")){
                    new SendLetterActivity.WriteTask().execute();
                }else{
                    Toast.makeText(getApplicationContext(), "内容不能为空", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.send_letter_img:    //选择图片
                selectImg();
                break;
            case R.id.img:
                addImgOrGetImg();
                break;
            case R.id.send_letter_content:
                hideKeyboard(v);
                isSend =!isSend;
                break;
            case R.id.send_letter:
                hideKeyboard(v);
                if(isSend) isSend =false;   //只能在编写状态下改变是否编写
                break;
            case R.id.imgdele:
                deleteImg();
                break;
        }
    }

    private void addImgOrGetImg(){
        getImageView(img);
    }

    //隐藏键盘
    private void  hideKeyboard(View v){
        if(isSend){
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    private void createCancelDialog(String title){
        new CommomDialog(this,R.style.common_dialog ,title, new CommomDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog,boolean confirm) {
                if(confirm){    //点击确定
                    dialog.dismiss();
                    finish();
                }

            }
        }).show();
    }

    private boolean isEditing(){
        if(!((TextView)findViewById(R.id.send_letter_content)).getText().toString().equals("")){
            return true;
        }
        return false;
    }

    private void deleteImg(){     //删除在pos位置的图片
        img.setImageResource(0);
        imgdele.setVisibility(View.GONE);
    }

    //动态写入数据库请求
    private void sendLetterRequest(final String baseImg){
        //请求地址
        String url = "http://39.106.154.68:8080/calligrapher/SendLetterServlet";    //注①
        final String tag = "SendLetter";    //注②

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
                            if (result.equals("success")) {  //发表动态成功
                                handler.sendEmptyMessage(SEND_SUCCESS);
                            } else {
                                handler.sendEmptyMessage(SEND_FAILED);
                            }
                        } catch (JSONException e) {
                            //做自己的请求异常操作，如Toast提示（“无网络连接”等）
                            handler.sendEmptyMessage(SEND_FAILED);
                            Log.e("TAG", e.getMessage(), e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //做自己的响应错误操作，如Toast提示（“请稍后重试”等）
                handler.sendEmptyMessage(SEND_FAILED);
                Log.e("TAG", error.getMessage(), error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("img", baseImg);  //注⑥
                params.put("phonenumber", followed);
                params.put("selfphonenumber", MainActivity.user.getPhoneNumber());
                params.put("context",((TextView)findViewById(R.id.send_letter_content)).getText().toString());
                return params;
            }
        };
        //设置Tag标签
        request.setTag(tag);

        //将请求添加到队列中
        requestQueue.add(request);
    }
    private byte[] getImgBytes(Drawable drawable){
        Bitmap bitmap=drawable2Bitmap(drawable);
        ByteArrayOutputStream baos = new ByteArrayOutputStream(); //设置位图的压缩格式，质量为100%，并放入字节数组输出流中
        bitmap.compress(Bitmap.CompressFormat.PNG,100,baos);
        return baos.toByteArray();
    }

    public class WriteTask extends AsyncTask<String, Void, Bitmap> {
        //上面的方法中，第二个参数的包装类：进度的刻度，第三个参数：任务执行的返回结果

        @Override
        //在界面上显示进度条
        protected void onPreExecute() {
            ((com.wang.avi.AVLoadingIndicatorView)findViewById(R.id.send_letter_avi)).setVisibility(View.VISIBLE);
        };
        protected Bitmap doInBackground(String... params) {  //三个点，代表可变参数
            //得到图片字节数组编码后的字符串
            byte[] imgBytes;
            String baseImg="";
            if(null!=img.getDrawable()){
                imgBytes=getImgBytes(img.getDrawable());
                baseImg = EncodeBase64.encodeBase(imgBytes);
            }
            sendLetterRequest(baseImg);
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result){
            //super.onPostExecute(result);
        }
    }

    private void requestPermission(final String permission, String rationale, final int requestCode){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, permission)){
            new AlertDialog.Builder(this)
                    .setTitle(R.string.mis_permission_dialog_title)
                    .setMessage(rationale)
                    .setPositiveButton(R.string.mis_permission_dialog_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(SendLetterActivity.this, new String[]{permission}, requestCode);
                        }
                    })
                    .setNegativeButton(R.string.mis_permission_dialog_cancel, null)
                    .create().show();
        }else{
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
        }
    }

    // Drawable转换成Bitmap
    public Bitmap drawable2Bitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap
                .createBitmap(
                        drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight(),
                        drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    private void setPic(ImageView imageView, String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds=true;
        BitmapFactory.decodeFile(path, options);
        //计算图片的缩放比例
        options.inSampleSize = calculateInSampleSize(options, imageView.getWidth(),imageView.getHeight());
        options.inJustDecodeBounds = false;
        Bitmap bitmap= BitmapFactory.decodeFile(path, options);
        imageView.setImageBitmap(bitmap);
    }
    public int calculateInSampleSize(BitmapFactory.Options options,
                                     int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }
        // Log.d("test",inSampleSize+"");
        return inSampleSize;
    }

    private void backUserFollowActivity(){
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                finish();
            }
        },1000);
    }
}
