package com.example.hasee.bluecalligrapher.write_field_character;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.hasee.bluecalligrapher.R;
import com.example.hasee.bluecalligrapher.fragment.Fragment2;
import com.example.hasee.bluecalligrapher.main.MainActivity;
import com.example.hasee.bluecalligrapher.tracing.CharCompareActivity;
import com.example.hasee.bluecalligrapher.utils.ImageUtil;
import com.example.hasee.bluecalligrapher.write_draw_pen.NewDrawPenView;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class FieldCharacterShapeActivity extends AppCompatActivity implements DrawViewLayout.IActionCallback, View.OnClickListener {
    private DrawViewLayout mDrawViewLayout;
    private Bitmap mBitmap;
    private Bitmap mBitmapResize;
    private HandRichTextEditor mRetContent;
    private long mOldTime;
    private String tracing_char;
    private boolean isMissionChar;
    private final int UPDATE_MISSION_FAIL=1;
    /**
     * 数据库Id
     */
    private long draftId = 0L;
    /**
     * 图片命名
     */
    private static String full_name = "";
    private static final String LAST_NAME = "word_";
    /**
     * 文件保存的路径
     */
    private String mPath = null;
    private boolean mIsCreateBitmap = false;
    private Bitmap mCreatBimap;
    /**
     * 自动保存Timer
     */
    private Timer mTimerSave;
    /**
     * add shiming  手写体的生成图片的时间
     */
    public static final int HADN_DRAW_TIME = 700;
    public static final String FONT_NAME_HEAD = "[font]";
    public static final String FONT_NAME_TAIL = "[/font]";
    public static int mAllHandDrawSize;
    public static int mEmotionSize;
    //private Button mChangePen;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_activity_field_character_shape_layout);
        //这里两个值关系到手写的所有的一切
        DisplayMetrics dm = this.getResources().getDisplayMetrics();
        mAllHandDrawSize = (int) (37.0 * dm.density);
        mEmotionSize = (int)(dm.density * 27.0);
        findViews();
        initData();
        audioSave();

    }


    private void initData() {
        try {
            mPath = getHandPath(draftId);
            mRetContent.setPath(mPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static final String ROOT_PATH =  File.separator+"cn.shiming.fieldcharactershap";
    /**当前操作文件的保存路径*/
    public  String getHandPath(long draftId){
        String path = Environment.getExternalStorageDirectory().getPath()  + ROOT_PATH  + File.separator + "handdraw" + File.separator +"shiming" + File.separator + draftId + File.separator;
        return path;
    }
    public void audioSave() {
        mTimerSave = new Timer();
        mTimerSave.schedule(task, 60000, 20000);
    }

    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            Message msg = mHandler.obtainMessage();
            msg.obj = false;
            mHandler.sendMessage(msg);
        }
    };
    final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            long l1 = System.currentTimeMillis();
            if ((l1 - mOldTime) > HADN_DRAW_TIME) {
                mHandler.removeCallbacks(runnable);
                Message msg = mHandler.obtainMessage();
                msg.obj = true;
                msg.what = 0x123;
                mHandler.sendMessage(msg);
            } else {
                mHandler.postDelayed(this, 100);
            }

        }
    };
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                case 0x123:
                    try {
                        boolean obj = (boolean) msg.obj;
                        if (obj) {
                            NewDrawPenView view = mDrawViewLayout.getSaveBitmap();
                            if (view != null) {
                                //边距强行扫描
                                mBitmap = view.clearBlank(100);
                                mHandler.post(runnableUi);
                            }
                        }
                    } catch (Exception e) {

                    } finally {
                        mHandler.removeCallbacks(runnable);
                    }
                    break;
                case 0x124:
                    mRetContent.setVisibilityEdit(View.VISIBLE);
                    mRetContent.setVisibilityClose(View.VISIBLE);
                    mRetContent.getLastFocusEdit().setCursorVisible(true);
                    mRetContent.getLastFocusEdit().requestFocus();
                    break;
                case 0x125:
                    break;
                case UPDATE_MISSION_FAIL:
                    Toast.makeText(getApplicationContext(), "网络错误，活动任务无法进行", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    private void findViews() {
        mRetContent = (HandRichTextEditor)findViewById(R.id.et_handdraw_content);
        mDrawViewLayout = (DrawViewLayout)findViewById(R.id.brush_weight);
        ((ImageView)findViewById(R.id.back)).setOnClickListener(this);
        mRetContent.setOnHandRichEditTextHasFocus(new HandRichTextEditor.onHandRichEditTextHasFocus() {
            @Override
            public void hasFocus(View view) {
                mDrawViewLayout.showBk();
            }

            @Override
            public void onClickChange(View v) {
                mDrawViewLayout.showBk();
            }
        });
        mDrawViewLayout.setActionCallback(this);
        mDrawViewLayout.showBk();
        ((ImageView)findViewById(R.id.back)).setOnClickListener(this);
        if(null!= Fragment2.tracings[0]){
            tracing_char=Fragment2.tracings[0].getCharacter();
            if(tracing_char.equals(MainActivity.mission_char)){
                isMissionChar=true;
            }else{
                isMissionChar=false;
            }
        }
    }

    Runnable runnableUi = new Runnable() {
        @Override
        public void run() {
            if (mIsCreateBitmap) {
                //110
                mBitmapResize = BitmapDrawUtils.resizeImage(mCreatBimap, mAllHandDrawSize, mAllHandDrawSize);
                mIsCreateBitmap = false;
            } else {
                mBitmapResize = BitmapDrawUtils.resizeImage(mBitmap, mAllHandDrawSize, mAllHandDrawSize);
            }
        }

    };

    @Override
    protected void onResume() {
        super.onResume();
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                // Permission Denied
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void getUptime(long l) {
        mOldTime = l;
        mHandler.postDelayed(runnable, 100);
    }

    @Override
    public void stopTime() {
        mHandler.removeCallbacks(runnable);
    }

    //文字比对
    @Override
    public void compareChar(Bitmap bitmap,int color){
        if(null!=mBitmapResize){
            Intent i=new Intent(this, CharCompareActivity.class);
            i.putExtra("tracing_img", ImageUtil.getImgBytes(bitmap));
            i.putExtra("img",ImageUtil.getImgBytes(mBitmapResize));
            i.putExtra("color",color);
            this.startActivity(i);
        }
    }

    @Override
    public void submit(){
        //根据Bitmap对象创建ImageSpan对象
        ImageSpan imageSpan = new ImageSpan(FieldCharacterShapeActivity.this, mBitmapResize);
        //创建一个SpannableString对象，以便插入用ImageSpan对象封装的图像
        full_name = LAST_NAME + System.currentTimeMillis();
        String s =FONT_NAME_HEAD + full_name + FONT_NAME_TAIL;
        SpannableString spannableString = new SpannableString(s);
        //  用ImageSpan对象替换face
        spannableString.setSpan(imageSpan, 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        //将选择的图片追加到EditText中光标所在位置
        EditText ed = mRetContent.getLastFocusEdit();
        int index = ed.getSelectionStart(); //获取光标所在位置
        Editable edit_text = ed.getEditableText();
        if (index < 0 || index >= edit_text.length()) {
            edit_text.append(spannableString);
        } else {
            edit_text.insert(index, spannableString);
        }
        if(MainActivity.user!=null && isMissionChar && MainActivity.user.getMission2()<15){
            updateMissionRequest(MainActivity.user.getPhoneNumber(),2);
        }
    }


    /**
     * @param flag 下面的键盘是否在显示了
     */
    @Override
    public void showkeyB(boolean flag) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mHandler) {
            mHandler = null;
        }
        mTimerSave.cancel();
    }

    @Override
    public void onClick(View v) {
        int penConfig = mDrawViewLayout.getPenConfig();
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
        }
    }

    private void updateMissionRequest(final String phone,final int which){
        //请求地址
        String url = "http://39.106.154.68:8080/calligrapher/UpdateMissionServlet";    //注①
        String tag = "UpdateMission";    //注②

        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        //取得请求队列
        //防止重复请求，所以先取消tag标识的请求队列
        requestQueue.cancelAll(tag);

        //创建StringRequest，定义字符串请求的请求方式为POST(省略第一个参数会默认为GET方式)
        final StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        MainActivity.user.setMission2(MainActivity.user.getMission2()+1);
                        MainActivity.user.setDay_score(MainActivity.user.getDay_score()+2);
                        MainActivity.user.setDay_score(MainActivity.user.getWeek_score()+2);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //做自己的响应错误操作，如Toast提示（“请稍后重试”等）
                Log.e("TAG", error.getMessage(), error);
                mHandler.sendEmptyMessage(UPDATE_MISSION_FAIL);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("phonenumber", phone);  //注⑥
                params.put("which", which+"");
                return params;
            }
        };
        //设置Tag标签
        request.setTag(tag);
        //将请求添加到队列中
        requestQueue.add(request);
    }
}
