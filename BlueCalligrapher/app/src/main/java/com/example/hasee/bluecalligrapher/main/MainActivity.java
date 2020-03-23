package com.example.hasee.bluecalligrapher.main;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.design.internal.NavigationMenuPresenter;
import android.support.design.internal.NavigationMenuView;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
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
import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.example.hasee.bluecalligrapher.R;
import com.example.hasee.bluecalligrapher.adapter.MyFragmentPagerAdapter;
import com.example.hasee.bluecalligrapher.bean.Store;
import com.example.hasee.bluecalligrapher.bean.User;
import com.example.hasee.bluecalligrapher.item.DynamicsItem;
import com.example.hasee.bluecalligrapher.register.LoginActivity;
import com.example.hasee.bluecalligrapher.setting.SettingAboutActivity;
import com.example.hasee.bluecalligrapher.setting.TodayActionActivity;
import com.example.hasee.bluecalligrapher.setting.UserCleanCacheActivity;
import com.example.hasee.bluecalligrapher.setting.UserDynamicActivity;
import com.example.hasee.bluecalligrapher.setting.UserFollowActivity;
import com.example.hasee.bluecalligrapher.setting.UserMessageActivity;
import com.example.hasee.bluecalligrapher.setting.UserStoreActivity;
import com.example.hasee.bluecalligrapher.userinfo.UserInfoActivity;
import com.example.hasee.bluecalligrapher.userinfo.UserselfInfoActivity;
import com.example.hasee.bluecalligrapher.utils.CommomDialog;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,RadioGroup.OnCheckedChangeListener,
        ViewPager.OnPageChangeListener,View.OnClickListener{
    public static User user=null;
    public static String storeCharacter="";
    public static String storeStyle="";
    public static Store[] store=new Store[40];
    public static Bitmap head_bitmap=null;
    public static String mission_char="万";
    private RadioButton rb1,rb2,rb3,rb4;
    private RadioGroup rg_tab_bar;
    public static boolean hasGotToken = false;
    public static List<DynamicsItem> dynamicsItems=new ArrayList<DynamicsItem>();    // 动态列表
    //几个代表页面的常量
    public static final int PAGE_ONE = 0;
    public static final int PAGE_TWO = 1;
    public static final int PAGE_THREE = 2;
    public static final int PAGE_FOUR = 3;
    private final int CHANGE_FAIL=6;
    private final int UPDATE_CHECKIN_SUCCESS=7;
    private final int UPDATE_MISSION_FAIL=8;
    private final int RESUME_PHOTO=9;
    private ViewPager vpager;
    private MyFragmentPagerAdapter mAdapter;
    private View navHeaderView;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private CircleImageView head_photo;     //头像
    private Button check_in,level_text;   //签到按钮,等级显示
    private ProgressBar progesss;
    private TextView username;
    private boolean login=false;    //是否已经登录，在onResume中使用
    //十个等级，每个等级升级需要的经验值
    private final int levels[]={100,200,400,800,1600,3200,6400,12800,25600,51200};
    private Drawable check_in_drawLeft;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what){
                case CHANGE_FAIL:
                    Toast.makeText(getApplicationContext(), "更改失败，请查看网络状态", Toast.LENGTH_LONG).show();
                    break;
                case UPDATE_CHECKIN_SUCCESS:
                    MainActivity.user.setCheck_in(true);
                    setCheckInText();
                    Toast.makeText(getApplicationContext(), "签到成功，+2经验值", Toast.LENGTH_LONG).show();
                    break;
                case RESUME_PHOTO:
                    head_photo.setImageBitmap(MainActivity.head_bitmap);
                    setName();
                    break;
                case UPDATE_MISSION_FAIL:
                    Toast.makeText(getApplicationContext(), "网络错误，活动任务无法进行", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("马永先硬笔书法");
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);
        setNavigationMenuLineStyle(navigationView,Color.WHITE,2);
        navHeaderView = navigationView.getHeaderView(0);
        init();
        initAccessToken();
    }
    void init(){
        navigationView.getMenu().getItem(8).setVisible(false);  //退出是第8个item
        head_photo=navHeaderView.findViewById(R.id.h_head);
        check_in=(Button)navHeaderView.findViewById(R.id.check_in);
        level_text=(Button)navHeaderView.findViewById(R.id.level);
        username=(TextView)navHeaderView.findViewById(R.id.username);
        progesss = (ProgressBar) navHeaderView.findViewById(R.id.progesss1);
        head_photo.setOnClickListener(this);
        check_in.setOnClickListener(this);
        rg_tab_bar=(RadioGroup)findViewById(R.id.rg_tab_bar);
        rb1=(RadioButton)findViewById(R.id.rb_lesson);
        rb2=(RadioButton)findViewById(R.id.rb_tracing);
        rb3=(RadioButton)findViewById(R.id.rb_main);
        rb4=(RadioButton)findViewById(R.id.rb_poetry);
        AssetManager mgr=getAssets();   //得到AssetManager
        Typeface typeface=Typeface.createFromAsset(mgr,"fonts/simhei.ttf");//改变字体
        rb1.setTypeface(typeface);
        rb2.setTypeface(typeface);
        rb3.setTypeface(typeface);
        rb4.setTypeface(typeface);
        rg_tab_bar.setOnCheckedChangeListener(this);
        mAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        vpager=(ViewPager)findViewById(R.id.vpager);
        vpager.setAdapter(mAdapter);
        vpager.setCurrentItem(PAGE_THREE);
        vpager.addOnPageChangeListener(this);
        setDrawableSize();
    }

    private void setDrawableSize(){
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        int rbRight=(int)(50*(width/720f)),rbLeft=(int)(50*(height/1280f));
        check_in_drawLeft=getResources().getDrawable(R.drawable.check_in);
        check_in_drawLeft.setBounds(0,0,70,70);
        check_in.setCompoundDrawables(check_in_drawLeft,null,null,null);
        Drawable drawableTop=getResources().getDrawable(R.mipmap.tab_lesson_pressed);
        drawableTop.setBounds(0,0,rbRight,rbLeft);
        rb1.setCompoundDrawables(null,drawableTop,null,null);
        drawableTop=getResources().getDrawable(R.mipmap.tab_tracing_pressed);
        drawableTop.setBounds(0,0,rbRight,rbLeft);
        rb2.setCompoundDrawables(null,drawableTop,null,null);
        drawableTop=getResources().getDrawable(R.mipmap.tab_main_pressed);
        drawableTop.setBounds(0,0,rbRight,rbLeft);
        rb3.setCompoundDrawables(null,drawableTop,null,null);
        drawableTop=getResources().getDrawable(R.mipmap.tab_poetry_pressed);
        drawableTop.setBounds(0,0,rbRight,rbLeft);
        rb4.setCompoundDrawables(null,drawableTop,null,null);
    }

    @Override
    public void onClick(View v){
        int id=v.getId();
        switch (id){
            case R.id.h_head:
                if(null==user) {
                    startActivity(new Intent(this, LoginActivity.class));
                }else{
                    Intent i=new Intent(this, UserselfInfoActivity.class);
                    ByteArrayOutputStream baos=new ByteArrayOutputStream();
                    i.putExtra("head",user.getHead_img());
                    i.putExtra("username",user.getUserName());
                    i.putExtra("phonenumber",user.getPhoneNumber());
                    startActivity(i);
                }
                break;
            case R.id.check_in: //点击签到
                if(null==MainActivity.user) {
                    startActivity(new Intent(this, LoginActivity.class));
                }else if(!MainActivity.user.isCheck_in()){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            updateCheckIn(MainActivity.user.getPhoneNumber());
                        }
                    }).start();
                    if(MainActivity.user.getMission1()==0){
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                    updateMissionRequest(MainActivity.user.getPhoneNumber(),1);
                            }
                        }).start();
                    }
                }
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_lesson:
                vpager.setCurrentItem(PAGE_ONE);
                toolbar.setTitle("课堂");
                break;
            case R.id.rb_tracing:
                vpager.setCurrentItem(PAGE_TWO);
                toolbar.setTitle("临摹");
                break;
            case R.id.rb_main:
                vpager.setCurrentItem(PAGE_THREE);
                toolbar.setTitle("马永先硬笔书法");
                break;
            case R.id.rb_poetry:
                toolbar.setTitle("赏析");
                vpager.setCurrentItem(PAGE_FOUR);
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
                    break;
                case PAGE_TWO:
                    rb2.setChecked(true);
                    break;
                case PAGE_THREE:
                    rb3.setChecked(true);
                    break;
                case PAGE_FOUR:
                    rb4.setChecked(true);
                    break;
            }
        }
    }

    private void initAccessToken() {
        OCR.getInstance().initAccessToken(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken accessToken) {
                String token = accessToken.getAccessToken();
                hasGotToken = true;
            }

            @Override
            public void onError(OCRError error) {
                //Toast.makeText(MainActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
                error.printStackTrace();
            }
        }, getApplicationContext());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initAccessToken();
        } else {
            Toast.makeText(MainActivity.this, "需要android.permission.READ_PHONE_STATE", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 释放内存资源
        OCR.getInstance().release();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        moveTaskToBack(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_store) {     //我的收藏
            if(null==MainActivity.user){
                startActivity(new Intent(this,LoginActivity.class));
            }else{
                startActivity(new Intent(this,UserStoreActivity.class));
            }
        } else if (id == R.id.nav_dynamic) {    //我的动态
            if(null==MainActivity.user){
                startActivity(new Intent(this,LoginActivity.class));
            }else{
                startActivity(new Intent(this,UserDynamicActivity.class));
            }
        } else if (id == R.id.nav_follow){     //我的关注
            if(null==MainActivity.user){
                startActivity(new Intent(this,LoginActivity.class));
            }else{
                startActivity(new Intent(this,UserFollowActivity.class));
            }
        } else if (id == R.id.nav_activity){   //今日活动
            startActivity(new Intent(this, TodayActionActivity.class));
        } else if (id == R.id.nav_message){    //我的消息
            if(null==MainActivity.user){
                startActivity(new Intent(this,LoginActivity.class));
            }else{
                startActivity(new Intent(this,UserMessageActivity.class));
            }
        } else if (id == R.id.nav_clear_data){ //缓存管理
            startActivity(new Intent(this, UserCleanCacheActivity.class));
        }else if (id == R.id.nav_share){      //分享光名

        }else if (id == R.id.nav_about){      //关于我们
            startActivity(new Intent(this, SettingAboutActivity.class));
        }else if(id == R.id.nav_quit){
            createConfirmDialog("确认要退出？");
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void createConfirmDialog(String title){
        new CommomDialog(this,R.style.common_dialog ,title, new CommomDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog,boolean confirm) {
                if(confirm){    //点击确定
                    clearUser();
                    dialog.dismiss();
                    head_photo.setImageResource(R.drawable.personal_photo);
                }
            }
        }).setPositiveButton("确认").setNegativeButton("取消").show();
    }

    private void setCheckInText(){
        check_in.setText("已签到");
        check_in.setCompoundDrawables(null,null,null,null);
        MainActivity.user.setExp(MainActivity.user.getExp()+2);
        setExp();
    }

    //设置用户名
    private void setName(){
        username.setVisibility(View.VISIBLE);
        username.setText(MainActivity.user.getUserName());
        setExp();
    }

    //设置经验值
    private void setExp(){
        level_text.setVisibility(View.VISIBLE);
        progesss.setVisibility(View.VISIBLE);
        int exp=MainActivity.user.getExp(),level=0;
        while(level<levels.length && exp>=levels[level]){
            exp=exp-levels[level];
            level++;
        }
        level=level<levels.length?level+1:level;
        level_text.setText("Lv"+level);
        progesss.setMax(levels[level]);
        progesss.setProgress(exp);
    }

    private void setNotCheckInText(){
        check_in.setText("签到");
        check_in.setCompoundDrawables(check_in_drawLeft,null,null,null);
    }

    private void updateCheckIn(final String phonenumber){
        //请求地址
        String url = "http://39.106.154.68:8080/calligrapher/UpdateCheckInServlet";    //注①
        String tag = "UpdateCheckIn";    //注②

        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        //取得请求队列
        //防止重复请求，所以先取消tag标识的请求队列
        requestQueue.cancelAll(tag);

        //创建StringRequest，定义字符串请求的请求方式为POST(省略第一个参数会默认为GET方式)
        final StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        mHandler.sendEmptyMessage(UPDATE_CHECKIN_SUCCESS);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //做自己的响应错误操作，如Toast提示（“请稍后重试”等）
                Log.e("TAG", error.getMessage(), error);
                mHandler.sendEmptyMessage(CHANGE_FAIL);
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
                        MainActivity.user.setMission1(1);
                        MainActivity.user.setDay_score(MainActivity.user.getDay_score()+5);
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

    private void changeLoginMessage(){
        if(!login){
            navigationView.getMenu().getItem(8).setVisible(true);
            if(MainActivity.user.isCheck_in()){ //改变签到信息
                setCheckInText();
            }else{
                setNotCheckInText();
            }
            login=true;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(null!=MainActivity.user && null!=MainActivity.user.getHead_img()){
            mHandler.sendEmptyMessage(RESUME_PHOTO);
            changeLoginMessage();
        }else if(null!=MainActivity.user){
            changeLoginMessage();
        }else{
            head_photo.setImageResource(R.drawable.personal_photo);
        }
    }

    private void clearUser(){
        level_text.setVisibility(View.GONE);
        progesss.setVisibility(View.GONE);
        username.setVisibility(View.GONE);
        navigationView.getMenu().getItem(8).setVisible(false);
        MainActivity.user=null;
        MainActivity.store=new Store[40];
        MainActivity.storeCharacter="";
        MainActivity.storeStyle="";
        MainActivity.head_bitmap=null;
    }

    public void setNavigationMenuLineStyle(NavigationView navigationView, @ColorInt final int color, final int height) {
        try {
            Field fieldByPressenter = navigationView.getClass().getDeclaredField("mPresenter");
            fieldByPressenter.setAccessible(true);
            NavigationMenuPresenter menuPresenter = (NavigationMenuPresenter) fieldByPressenter.get(navigationView);
            Field fieldByMenuView = menuPresenter.getClass().getDeclaredField("mMenuView");
            fieldByMenuView.setAccessible(true);
            final NavigationMenuView mMenuView = (NavigationMenuView) fieldByMenuView.get(menuPresenter);
            mMenuView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
                @Override
                public void onChildViewAttachedToWindow(View view) {
                    RecyclerView.ViewHolder viewHolder = mMenuView.getChildViewHolder(view);
                    if (viewHolder != null && "SeparatorViewHolder".equals(viewHolder.getClass().getSimpleName()) && viewHolder.itemView != null) {
                        if (viewHolder.itemView instanceof FrameLayout) {
                            FrameLayout frameLayout = (FrameLayout) viewHolder.itemView;
                            View line = frameLayout.getChildAt(0);
                            line.setBackgroundColor(color);
                            line.getLayoutParams().height = height;
                            line.setLayoutParams(line.getLayoutParams());
                        }
                    }
                }
                @Override
                public void onChildViewDetachedFromWindow(View view) {
                }
            });
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
