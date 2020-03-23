package com.example.hasee.bluecalligrapher.fragment;

import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
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
import com.example.hasee.bluecalligrapher.adapter.MyFragmentPagerAdapterTracing;
import com.example.hasee.bluecalligrapher.adapter.TracingOcrListAdapter;
import com.example.hasee.bluecalligrapher.bean.Tracing;
import com.example.hasee.bluecalligrapher.decodebase64.DecodeBase64;
import com.example.hasee.bluecalligrapher.main.MainActivity;
import com.example.hasee.bluecalligrapher.utils.MyHorizontalListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hasee on 2018/3/30.
 */

public class Fragment2 extends Fragment implements RadioGroup.OnCheckedChangeListener,
        ViewPager.OnPageChangeListener{

    public static Tracing[] tracings=new Tracing[4];
    private final int UPDATE_PICTURE=1;
    private final int RECOGNIZE_FAIL=2;
    private final int START_ANIM=3;
    private final int KEYBOARD_SEARCH=4;
    private final int SEND_FAILED=5;    //搜索失败
    private final int LISTEN_START_CONNECT=6;
    private final int LISTEN_END_CONNECT=7;
    private RadioButton rb1,rb2,rb3,rb4;
    private RadioGroup rg_tab_bar;
    private ViewPager vpager;
    private EditText search_text;   //搜索内容
    private MyFragmentPagerAdapterTracing mAdapter;
    private View view;
    public static MyHorizontalListView listView;      //横向滑动的listview
    public static TracingOcrListAdapter tracingOcrAdapter;
    public static List<String> characters=new ArrayList<String>();
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what){
                case UPDATE_PICTURE:
                    updatePicture();
                    break;
                case SEND_FAILED:
                    Toast.makeText(getContext(), "抱歉，未搜索到此字\n点击左上角图标可查看字库", Toast.LENGTH_LONG).show();
                    break;
                case RECOGNIZE_FAIL:
                    Toast.makeText(getContext(), "抱歉，没有识别出您说的话", Toast.LENGTH_LONG).show();
                    break;
                case START_ANIM:        //播放语音动画
                    //startSpeechAnimation();
                    break;
                case KEYBOARD_SEARCH:
                    ((com.wang.avi.AVLoadingIndicatorView)view.findViewById(R.id.poetry_avi)).setVisibility(View.VISIBLE);
                    searchRequest((String)msg.obj);
                    break;
                case LISTEN_START_CONNECT: //使用语言开始远程访问服务器
                    search_text.setText((String)msg.obj);
                    MediaPlayer.create(getContext(), R.raw.bdspeech_recognition_success).start();
                    ((com.wang.avi.AVLoadingIndicatorView)view.findViewById(R.id.poetry_avi)).setVisibility(View.VISIBLE);
                    break;
                case LISTEN_END_CONNECT:   //使用语音访问服务器完毕
                    ((com.wang.avi.AVLoadingIndicatorView)view.findViewById(R.id.poetry_avi)).setVisibility(View.GONE);
                    break;
            }
        }
    };
    public Fragment2(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view=inflater.inflate(R.layout.fg_tracing,container, false);
        init(view);
        return view;
    }

    void init(View view){
        //myEventListener=new MyEventListener(getContext(),getActivity());
/*        speech=(ImageView)view.findViewById(R.id.char_speech);
        speech.setOnClickListener(this);
        speech.setOnTouchListener(this);
        speech_anim=(ImageView)view.findViewById(R.id.speech_animation);*/
        rg_tab_bar=(RadioGroup)view.findViewById(R.id.rg_tracing_bar) ;
        rb1=(RadioButton)view.findViewById(R.id.rb_jian);
        rb2=(RadioButton)view.findViewById(R.id.rb_fan);
        rb3=(RadioButton)view.findViewById(R.id.rb_xing);
        rb4=(RadioButton)view.findViewById(R.id.rb_cao);
        rg_tab_bar.setOnCheckedChangeListener(this);
        AssetManager mgr=getContext().getAssets();   //得到AssetManager
        Typeface typeface=Typeface.createFromAsset(mgr,"fonts/simhei.ttf");//改变字体
        rb1.setTypeface(typeface);
        rb2.setTypeface(typeface);
        rb3.setTypeface(typeface);
        rb4.setTypeface(typeface);
        if(null==mAdapter) mAdapter=new MyFragmentPagerAdapterTracing(getChildFragmentManager());
        vpager=(ViewPager)view.findViewById(R.id.vpager2);
        vpager.setAdapter(mAdapter);
        vpager.addOnPageChangeListener(this);
        search_text=(EditText)view.findViewById(R.id.char_search_text);

        //对键盘搜索键进行监听
        search_text.setOnEditorActionListener(new EditText.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH){
                    String text=search_text.getText().toString();
                    if(!text.equals("")){
                        text=text.substring(0,1);
                        Message message = new Message();
                        message.what=KEYBOARD_SEARCH;
                        message.obj=text;
                        mHandler.sendMessage(message);
                    }
                    return true;
                }
                return false;
            }
        });
        listView=(MyHorizontalListView)view.findViewById(R.id.char_list);
        tracingOcrAdapter=new TracingOcrListAdapter(getContext(),R.layout.tracing_ocr_item,characters);
        listView.setAdapter(tracingOcrAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                final String character=characters.get(arg2);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        searchRequest(character);
                    }
                }).start();
            }
        });
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_jian:
                vpager.setCurrentItem(MainActivity.PAGE_ONE);
                break;
            case R.id.rb_fan:
                vpager.setCurrentItem(MainActivity.PAGE_TWO);
                break;
            case R.id.rb_xing:
                vpager.setCurrentItem(MainActivity.PAGE_THREE);
                break;
            case R.id.rb_cao:
                vpager.setCurrentItem(MainActivity.PAGE_FOUR);
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
                case MainActivity.PAGE_ONE:
                    rb1.setChecked(true);
                    break;
                case MainActivity.PAGE_TWO:
                    rb2.setChecked(true);
                    break;
                case MainActivity.PAGE_THREE:
                    rb3.setChecked(true);
                    break;
                case MainActivity.PAGE_FOUR:
                    rb4.setChecked(true);
                    break;
            }
        }
    }

    private void searchRequest(final String text){
        //请求地址
        String url = "http://39.106.154.68:8080/calligrapher/SearchServlet";    //注①
        String tag = "Search";    //注②
        RequestQueue requestQueue= Volley.newRequestQueue(getContext());
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
                            mHandler.sendEmptyMessage(LISTEN_END_CONNECT);
                            if (result.equals("success")) {  //注⑤
                                initTracing(jsonObject,text);
                                //做自己的成功操作，如页面跳转
                            } else {
                                mHandler.sendEmptyMessage(SEND_FAILED);
                                //做自己的失败操作
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
                params.put("search_text",text);  //注⑥
                return params;
            }
        };
        //设置Tag标签
        request.setTag(tag);
        //将请求添加到队列中
        requestQueue.add(request);
    }

    private void initTracing(JSONObject jsonObject,String character){
        for(int i=0;i<4;i++){
            tracings[i]=new Tracing();
            tracings[i].setCharacter(character);
        }
        try{
            tracings[0].setPicture(DecodeBase64.decodeBase(jsonObject.getString("style_jian")));
            tracings[1].setPicture(DecodeBase64.decodeBase(jsonObject.getString("style_fan")));
            tracings[2].setPicture(DecodeBase64.decodeBase(jsonObject.getString("style_xing")));
            tracings[3].setPicture(DecodeBase64.decodeBase(jsonObject.getString("style_cao")));
            tracings[0].setId(jsonObject.getString("style_jian_id"));
            tracings[1].setId(jsonObject.getString("style_fan_id"));
            tracings[2].setId(jsonObject.getString("style_xing_id"));
            tracings[3].setId(jsonObject.getString("style_cao_id"));
            mHandler.sendEmptyMessage(UPDATE_PICTURE);
        }catch (JSONException e){
            Log.e("TAG", e.getMessage(), e);
        }
    }

    private void updatePicture(){
        vpager.setCurrentItem(0);
        rb1.setChecked(true);
        FragmentTracing1.setImage(tracings[0].getPicture());
        FragmentTracing2.setImage(tracings[1].getPicture());
    }
}
