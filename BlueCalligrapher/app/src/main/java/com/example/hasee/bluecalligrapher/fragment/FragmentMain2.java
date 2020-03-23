package com.example.hasee.bluecalligrapher.fragment;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
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
import com.example.hasee.bluecalligrapher.adapter.MainListAdapter;
import com.example.hasee.bluecalligrapher.decodebase64.DecodeBase64;
import com.example.hasee.bluecalligrapher.dynamic.WriteDynamicActivity;
import com.example.hasee.bluecalligrapher.item.DynamicsItem;
import com.example.hasee.bluecalligrapher.main.MainActivity;
import com.example.hasee.bluecalligrapher.utils.ImageUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hasee on 2018/4/12.
 */

public class FragmentMain2 extends Fragment implements View.OnClickListener{
    private MainListAdapter mainListAdapter;
    private final int REFRESH_FAIL=2;           //刷新失败
    private final int SEARCH_MORE_SUCCESS=3;        //刷新成功
    private final int SEARCH_MORE_FAIL=4;           //刷新失败
    private final int REFRESH_DYNAMIC=5;           //刷新失败
    private ListView listView;
    private LinearLayout main_write_dynamic;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView footTextView=null;
    private ProgressBar footProgressBar=null;
    private boolean isLoading = false;            //正在加载
    private static boolean hasLoginLoad = false;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case REFRESH_FAIL:
                    swipeRefreshLayout.setRefreshing(false);//设置不刷新
                    Toast.makeText(getContext(), "刷新失败", Toast.LENGTH_LONG).show();
                    break;
                case SEARCH_MORE_SUCCESS:
                    refreshMoreDynamic((JSONObject) msg.obj);
                    break;
                case SEARCH_MORE_FAIL:
                    if(null!=footTextView && null!=footProgressBar){
                        footTextView.setText(R.string.load_finish);
                        footProgressBar.setVisibility(View.GONE);
                    }
                    isLoading=true;
                    break;
                case REFRESH_DYNAMIC:
                    swipeRefreshLayout.setRefreshing(false);//设置不刷新
                    refreshDynamic((JSONObject) msg.obj);
                    break;
            }
        }
    };
    public FragmentMain2(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view=inflater.inflate(R.layout.fg_main_dynamic,container, false);
        init(view);
        return view;
    }

    private void init(View view){
        AssetManager mgr=getContext().getAssets();   //得到AssetManager
        Typeface typeface=Typeface.createFromAsset(mgr,"fonts/STXINWEI.TTF");//改变字体
        ((TextView)view.findViewById(R.id.main_comment_text)).setTypeface(typeface);
        main_write_dynamic=view.findViewById(R.id.main_write_dynamic);
        main_write_dynamic.setOnClickListener(this);
        mainListAdapter=new MainListAdapter(getContext(),R.layout.main_item, MainActivity.dynamicsItems);
        listView=(ListView)view.findViewById(R.id.main_list_dynamic);
        addListViewFooterView();
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // 当不滚动时
                if (scrollState == SCROLL_STATE_IDLE) {
                    //判断是否滚动到底部
                    if (!isLoading && view.getLastVisiblePosition() == view.getCount() - 1) {
                        isLoading = true;
                        getMoreData();
                        return;
                    }
                }
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
        listView.setAdapter(mainListAdapter);

        if(MainActivity.dynamicsItems.isEmpty()){
            mainListAdapter.notifyDataSetChanged();
            sendRefreshDynamic();
        }
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.dynamic_refresh);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                sendRefreshDynamic();
            }
        });
    }
    private void addListViewFooterView() {
        AssetManager mgr=getContext().getAssets();   //得到AssetManager
        Typeface typeface=Typeface.createFromAsset(mgr,"fonts/simhei.ttf");//改变字体
        View footer = getActivity().getLayoutInflater().inflate(R.layout.listview_footerview, null);
        footProgressBar = (ProgressBar) footer.findViewById(R.id.listview_footview_progressBar);
        footTextView = (TextView) footer.findViewById(R.id.listview_footview_textview);
        footTextView.setTypeface(typeface);
        footTextView.setTextSize(15f);
        listView.addFooterView(footer);
        footTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (footTextView.getText().equals(R.string.load_error)&&!MainActivity.dynamicsItems.isEmpty()){ //加载数据
                    getMoreData();
                    isLoading=true;
                    footTextView.setText(R.string.loading);
                    footProgressBar.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onClick(View v){
        int id=v.getId();
        switch (id){
            case R.id.main_write_dynamic:
                if(null!=MainActivity.user){
                    startActivity(new Intent(getContext(), WriteDynamicActivity.class));
                }else{
                    Toast.makeText(getContext(), "您还未登录呢", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    //下拉得到更多的动态
    private void getMoreData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(MainActivity.dynamicsItems.size()>=1){
                    searchMoreDynamic(MainActivity.dynamicsItems.get(MainActivity.dynamicsItems.size()-1).getDynamicId());
                }
            }
        }).start();
    }
    private void sendRefreshDynamic(){ //发送刷新动态给Handler
        new Thread(new Runnable() {
            @Override
            public void run() {
                searchDynamic();
            }
        }).start();
    }

    //查询最新的十条动态
    private void searchDynamic(){
        //请求地址
        String url = "http://39.106.154.68:8080/calligrapher/SearchDynamicServlet";    //注①
        String tag = "SearchDynamic";    //注②

        RequestQueue requestQueue= Volley.newRequestQueue(getContext().getApplicationContext());
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
                            if (result.equals("success")) {  //刷新动态成功
                                Message message=Message.obtain();
                                message.obj=jsonObject;
                                message.what=REFRESH_DYNAMIC;
                                handler.sendMessage(message);
                                //refreshDynamic(jsonObject);
                            } else {
                                handler.sendEmptyMessage(REFRESH_FAIL);
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
                if(null!=MainActivity.user)
                    params.put("phonenumber",MainActivity.user.getPhoneNumber());
                else
                    params.put("phonenumber","");
                return params;
            }
        };
        //设置Tag标签
        request.setTag(tag);
        //将请求添加到队列中
        requestQueue.add(request);
    }

    //查询更多的十条动态
    private void searchMoreDynamic(final int dyId){
        //请求地址
        String url = "http://39.106.154.68:8080/calligrapher/SearchMoreDynamicServlet";    //注①
        String tag = "SearchMoreDynamic";    //注②

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
                            if (result.equals("success")) {  //刷新动态成功
                                Message message=Message.obtain();
                                message.what=SEARCH_MORE_SUCCESS;
                                message.obj=jsonObject;
                                handler.sendMessage(message);
                            } else {
                                handler.sendEmptyMessage(SEARCH_MORE_FAIL);
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
                if(null!=MainActivity.user)
                    params.put("phonenumber",MainActivity.user.getPhoneNumber());
                else
                    params.put("phonenumber","");
                params.put("dynamicId",dyId+"");
                return params;
            }
        };
        //设置Tag标签
        request.setTag(tag);
        //将请求添加到队列中
        requestQueue.add(request);
    }

    //刷新动态
    private void refreshDynamic(JSONObject jsonObject) {
        try{
            int len = Integer.parseInt(jsonObject.getString("length"));
            if (len != 0) {
                int pos = 0;
                MainActivity.dynamicsItems.clear();
                while (pos < len) {
                    String phonenumber=jsonObject.getString("phonenumber"+pos); //作者手机号
                    String username = jsonObject.getString("username" + pos);   //动态作者名
                    String context = jsonObject.getString("context" + pos);     //动态内容
                    String focus1 = jsonObject.getString("focus" + pos);        //当前用户是否已点赞
                    String time = jsonObject.getString("time" + pos);        //动态发表时间
                    Bitmap head_img=null;
                    Log.d("test",time);
                    if(!(jsonObject.getString("head_img" + pos).equals(""))){
                        byte[] head_imgs = DecodeBase64.decodeBase(jsonObject.getString("head_img" + pos));     //动态作者头像
                        head_img= ImageUtil.Bytes2Bimap(head_imgs);
                    }else{
                        //得到该图片的id(name 是该图片的名字，"drawable" 是该图片存放的目录，appInfo.packageName是应用程序的包)
                        int resID = getResources().getIdentifier("main_head_1", "drawable", getContext().getApplicationInfo().packageName);
                        head_img = BitmapFactory.decodeResource(getResources(), resID);
                    }
                    int dynamicCount = Integer.parseInt(jsonObject.getString("focuscount" + pos)); //动态点赞数
                    int commentCount = Integer.parseInt(jsonObject.getString("commentcount" + pos)); //动态评论数
                    boolean focus = false;
                    if (focus1.equals("1"))
                        focus = true;
                    else
                        focus = false;
                    int dynamicId = Integer.parseInt(jsonObject.getString("dynamicId" + pos));
                    int img_len = Integer.parseInt(jsonObject.getString("img_len" + pos));
                    Bitmap[] bitmaps = new Bitmap[10];
                    for (int i = 0; i < img_len; i++) {
                        String img_text = jsonObject.getString("img" + i + pos);
                        byte[] img_byte = DecodeBase64.decodeBase(img_text);
                        bitmaps[i] = ImageUtil.Bytes2Bimap(img_byte);
                    }
                    for (int i = img_len; i < 6; i++) {
                        bitmaps[i] = null;
                    }
                    DynamicsItem dynamicsItem = new DynamicsItem(dynamicId ,head_img, username, context, time, bitmaps[0], bitmaps[1], bitmaps[2], bitmaps[3], bitmaps[4], bitmaps[5]);
                    dynamicsItem.setPhonenumber(phonenumber);
                    dynamicsItem.setFocus(focus);
                    dynamicsItem.setFocusCount(dynamicCount);
                    dynamicsItem.setCommentCount(commentCount);
                    MainActivity.dynamicsItems.add(dynamicsItem);
                    pos++;
                }
                if(null == mainListAdapter){
                    mainListAdapter = new MainListAdapter(getContext(), R.layout.main_item, MainActivity.dynamicsItems);
                    listView.setAdapter(mainListAdapter);
                }
                if(null!=footTextView && null!=footProgressBar){
                    footTextView.setText(R.string.loading);
                    footProgressBar.setVisibility(View.VISIBLE);
                    isLoading=false;
                }
            }
            mainListAdapter.notifyDataSetChanged();
        }catch (JSONException e) {
            //做自己的请求异常操作，如Toast提示（“无网络连接”等）
            Log.e("TAG", e.getMessage(), e);
        }
    }

    //刷新动态
    private void refreshMoreDynamic(JSONObject jsonObject) {
        try{
            int len = Integer.parseInt(jsonObject.getString("length"));
            if (len != 0) {
                int pos = 0;
                while (pos < len) {
                    String phonenumber=jsonObject.getString("phonenumber"+pos); //作者手机号
                    String username = jsonObject.getString("username" + pos);   //动态作者名
                    String context = jsonObject.getString("context" + pos);     //动态内容
                    String focus1 = jsonObject.getString("focus" + pos);        //当前用户是否已点赞
                    String time = jsonObject.getString("time" + pos);        //动态发表时间
                    if(time.length()-5>0)
                        time=time.substring(5,time.length()-5);
                    Bitmap head_img=null;
                    if(!(jsonObject.getString("head_img" + pos).equals(""))){
                        byte[] head_imgs = DecodeBase64.decodeBase(jsonObject.getString("head_img" + pos));     //动态作者头像
                        head_img= ImageUtil.Bytes2Bimap(head_imgs);
                    }else{
                        //得到该图片的id(name 是该图片的名字，"drawable" 是该图片存放的目录，appInfo.packageName是应用程序的包)
                        int resID = getResources().getIdentifier("main_head_1", "drawable", getContext().getApplicationInfo().packageName);
                        head_img = BitmapFactory.decodeResource(getResources(), resID);
                    }
                    int dynamicCount = Integer.parseInt(jsonObject.getString("focuscount" + pos)); //动态点赞数
                    int commentCount = Integer.parseInt(jsonObject.getString("commentcount" + pos)); //动态评论数
                    boolean focus = false;  //当前用户是否已点赞
                    if (focus1.equals("1"))
                        focus = true;
                    else
                        focus = false;
                    int dynamicId = Integer.parseInt(jsonObject.getString("dynamicId" + pos));
                    int img_len = Integer.parseInt(jsonObject.getString("img_len" + pos));
                    Bitmap[] bitmaps = new Bitmap[10];
                    for (int i = 0; i < img_len; i++) {
                        String img_text = jsonObject.getString("img" + i + pos);
                        byte[] img_byte = DecodeBase64.decodeBase(img_text);
                        bitmaps[i] = ImageUtil.Bytes2Bimap(img_byte);
                    }
                    for (int i = img_len; i < 6; i++) {
                        bitmaps[i] = null;
                    }
                    DynamicsItem dynamicsItem = new DynamicsItem(dynamicId ,head_img, username, context, time, bitmaps[0], bitmaps[1], bitmaps[2], bitmaps[3], bitmaps[4], bitmaps[5]);
                    dynamicsItem.setPhonenumber(phonenumber);
                    dynamicsItem.setFocus(focus);
                    dynamicsItem.setFocusCount(dynamicCount);
                    dynamicsItem.setCommentCount(commentCount);
                    MainActivity.dynamicsItems.add(dynamicsItem);
                    pos++;
                }
                mainListAdapter.notifyDataSetChanged();
                isLoading=false;
            }
        }catch (JSONException e) {
            //做自己的请求异常操作，如Toast提示（“无网络连接”等）
            Log.e("TAG", e.getMessage(), e);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser && !hasLoginLoad && null!=MainActivity.user) {
            hasLoginLoad=true;
            isLoading=true;
            footTextView.setText(R.string.loading);
            footProgressBar.setVisibility(View.VISIBLE);
            if(!MainActivity.dynamicsItems.isEmpty())
                MainActivity.dynamicsItems.clear();
            mainListAdapter.notifyDataSetChanged();
            sendRefreshDynamic();
        }
        super.setUserVisibleHint(isVisibleToUser);
    }
}
