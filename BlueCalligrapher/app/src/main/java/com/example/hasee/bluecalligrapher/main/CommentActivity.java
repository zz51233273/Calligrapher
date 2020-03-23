package com.example.hasee.bluecalligrapher.main;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.example.hasee.bluecalligrapher.adapter.CommentListAdapter;
import com.example.hasee.bluecalligrapher.adapter.MainListAdapter;
import com.example.hasee.bluecalligrapher.adapter.UserDynamicListAdapter;
import com.example.hasee.bluecalligrapher.decodebase64.DecodeBase64;
import com.example.hasee.bluecalligrapher.item.CommentItem;
import com.example.hasee.bluecalligrapher.item.DynamicsItem;
import com.example.hasee.bluecalligrapher.utils.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hasee on 2018/4/30.
 */

public class CommentActivity extends AppCompatActivity implements View.OnClickListener{
    private CommentListAdapter comListAdapter;
    private List<CommentItem> commentItems=new ArrayList<CommentItem>();
    private final int GET_COM_SUCCESS=1;
    private final int GET_COM_FAIL=2;
    private final int SEND_SUCCESS=3;
    private final int SEND_FAIL=4;
    private final int UPDATE_MISSION_FAIL=5;
    private DynamicsItem dynamicsItem;
    private ListView listView;
    private ImageView head;     //动态作者头像
    private TextView writer;    //动态作者
    private TextView context;   //动态内容
    private EditText comment;   //评论内容
    private ImageView[] img=new ImageView[6];
    private Button send;        //发送
    private TextView footTextView=null;
    private ProgressBar footProgressBar=null;
    private boolean isLoading=false;            //正在加载
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case GET_COM_SUCCESS:
                    getComments((JSONObject)msg.obj);
                    break;
                case SEND_SUCCESS:
                    Toast.makeText(getApplicationContext(), "发送成功", Toast.LENGTH_LONG).show();
                    CommentItem commentItem=new CommentItem(MainActivity.head_bitmap,MainActivity.user.getUserName(),comment.getText().toString(),dynamicsItem.getDynamicId());
                    commentItems.add(commentItem);
                    comListAdapter=new CommentListAdapter(CommentActivity.this,R.layout.comment_item,commentItems);
                    listView.setAdapter(comListAdapter);
                    if(MainActivity.user.getMission3()==0){
                        MainActivity.user.setMission3(1);
                    }
                    break;
                case SEND_FAIL:
                    Toast.makeText(getApplicationContext(), "发送失败,请查看网络", Toast.LENGTH_LONG).show();
                    break;
                case GET_COM_FAIL:
                    if(null!=footTextView && null!=footProgressBar){
                        footTextView.setText(R.string.load_finish);
                        footProgressBar.setVisibility(View.GONE);
                    }
                    isLoading=true;
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
        setContentView(R.layout.comment_list);
        init();
    }

    private void init(){
        boolean isUserDynamic=getIntent().getBooleanExtra("user_dynamic",false);
        if(isUserDynamic)dynamicsItem= UserDynamicListAdapter.com_dynamicsItem;
        else dynamicsItem= MainListAdapter.com_dynamicsItem;
        final int dyId=dynamicsItem.getDynamicId();
        new Thread(new Runnable() {
            @Override
            public void run() {
                //搜索当前动态评论
                searchCommentsRequest(dyId);
            }
        }).start();
        head=(ImageView) findViewById(R.id.dynamic_head);
        writer=(TextView) findViewById(R.id.dynamic_writer);
        context=(TextView)findViewById(R.id.dynamic_context);
        img[0]=(ImageView) findViewById(R.id.img0);
        img[1]=(ImageView) findViewById(R.id.img1);
        img[2]=(ImageView) findViewById(R.id.img2);
        img[3]=(ImageView) findViewById(R.id.img3);
        img[4]=(ImageView) findViewById(R.id.img4);
        img[5]=(ImageView) findViewById(R.id.img5);
        send=(Button)findViewById(R.id.comment_send);
        comment=(EditText)findViewById(R.id.comment_context);
        head.setImageBitmap(dynamicsItem.getHeadId());
        writer.setText(dynamicsItem.getWriterName());
        context.setText(dynamicsItem.getContent());
        img[0].setImageBitmap(dynamicsItem.getImg0());
        img[1].setImageBitmap(dynamicsItem.getImg1());
        img[2].setImageBitmap(dynamicsItem.getImg2());
        img[3].setImageBitmap(dynamicsItem.getImg3());
        img[4].setImageBitmap(dynamicsItem.getImg4());
        img[5].setImageBitmap(dynamicsItem.getImg5());
        send.setOnClickListener(this);
        listView=findViewById(R.id.comment_list);
        addListViewFooterView();
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // 当不滚动时
                if (scrollState == SCROLL_STATE_IDLE) {
                    //判断是否滚动到底部
                    if (!isLoading && view.getLastVisiblePosition() == view.getCount() - 1 && !commentItems.isEmpty()) {
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
        comListAdapter=new CommentListAdapter(CommentActivity.this,R.layout.comment_item,commentItems);
        listView.setAdapter(comListAdapter);
        AssetManager mgr=getAssets();   //得到AssetManager
        Typeface typeface=Typeface.createFromAsset(mgr,"fonts/STXINGKA.TTF");//改变字体
        ((TextView) findViewById(R.id.title_name)).setTypeface(typeface);
    }

    private void addListViewFooterView() {
        View footer = getLayoutInflater().inflate(R.layout.listview_footerview, null);
        footProgressBar = (ProgressBar) footer.findViewById(R.id.listview_footview_progressBar);
        footTextView = (TextView) footer.findViewById(R.id.listview_footview_textview);
        listView.addFooterView(footer);
        footTextView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (footTextView.getText().equals(R.string.load_error)&&!commentItems.isEmpty()){ //加载数据
                    getMoreData();
                    isLoading=true;
                    footTextView.setText(R.string.loading);
                    footProgressBar.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    //下拉得到更多的评论
    private void getMoreData(){
        CommentItem commentItem=commentItems.get(commentItems.size()-1);
        searchMoreCommentsRequest(commentItem.getDyId(),commentItem.getId());
    }

    //得到当前动态的评论
    private void searchCommentsRequest(final int dynamicId){
        //请求地址
        String url = "http://39.106.154.68:8080/calligrapher/SearchCommentsServlet";    //注①
        final String tag = "SearchComments";    //注②

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
                            String result = jsonObject.getString("Result");  //注④
                            if (result.equals("success")) {  //刷新评论成功
                                Message message=Message.obtain();
                                message.what=GET_COM_SUCCESS;
                                message.obj=jsonObject;
                                handler.sendMessage(message);
                            } else {
                                handler.sendEmptyMessage(GET_COM_FAIL);
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
                Toast.makeText(getApplicationContext(), "服务器繁忙，请稍后重试", Toast.LENGTH_LONG).show();
                Log.e("TAG", error.getMessage(), error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("dynamicId",dynamicId+"");
                return params;
            }
        };
        //设置Tag标签
        request.setTag(tag);
        //将请求添加到队列中
        requestQueue.add(request);
    }

    @Override
    public void onClick(View v){
        int id=v.getId();
        switch (id){
            case R.id.comment_send:
                if(null!=MainActivity.user && !comment.getText().toString().equals("")){    //已登录且评论内容不为空
                    final String context=comment.getText().toString();
                    if(!StringUtil.check(context)){     //检查评论内容是否存在敏感字词
                        Toast.makeText(CommentActivity.this,R.string.unchecked_char,Toast.LENGTH_SHORT).show();
                        return;
                    }
                    final int pos=dynamicsItem.getDynamicId();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            sendCommentRequest(MainActivity.user.getPhoneNumber(),context,pos);
                        }
                    }).start();
                    if(MainActivity.user.getMission3()==0){
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                updateMissionRequest(MainActivity.user.getPhoneNumber(),3);
                            }
                        }).start();
                    }
                    // 隐藏输入法，然后暂存当前输入框的内容，方便下次使用
                    InputMethodManager im = (InputMethodManager)getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    im.hideSoftInputFromWindow(comment.getWindowToken(), 0);
                }else if(comment.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "评论不能为空", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext(), "您还未登录呢", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void getComments(JSONObject jsonObject){
        try {
            int len=Integer.parseInt(jsonObject.getString("length"));
            for(int i=0;i<len;i++){
                int id=Integer.parseInt(jsonObject.getString("commentId"+i));       //评论id
                String username=jsonObject.getString("username"+i);                 //动态作者用户名
                String context=jsonObject.getString("context"+i);                   //动态内容
                Bitmap head_img=null;
                if(!(jsonObject.getString("head_img"+i)).equals("")){               //动态作者头像
                    byte[] img= DecodeBase64.decodeBase(jsonObject.getString("head_img"+i));
                    head_img=Bytes2Bimap(img);
                }else{
                    //得到该图片的id(name 是该图片的名字，"drawable" 是该图片存放的目录，appInfo.packageName是应用程序的包)
                    int resID = getResources().getIdentifier("main_head_1", "drawable", getApplicationInfo().packageName);
                    head_img = BitmapFactory.decodeResource(getResources(), resID);
                }
                CommentItem commentItem=new CommentItem(id,head_img,username,context,dynamicsItem.getDynamicId());
                commentItems.add(commentItem);
            }
            comListAdapter.notifyDataSetChanged();
        }catch (JSONException e){
            Log.e("TAG", e.getMessage(), e);
        }finally {
            if(commentItems.size()<10){
                isLoading=true;
                footProgressBar.setVisibility(View.GONE);
                footTextView.setVisibility(View.GONE);
            }else
                isLoading=false;
        }
    }
    //byte[]转Bitmap
    public Bitmap Bytes2Bimap(byte[] b) {
        if (b.length != 0) {
            Bitmap bitmap= BitmapFactory.decodeByteArray(b, 0, b.length);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 80, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
            int options = 80;//先压缩到80%
            while (baos.toByteArray().length / 1024 > 200) { // 循环判断如果压缩后图片是否大于200kb,大于继续压缩
                if (options <= 0) {             //有的图片过大，可能当options小于或者等于0时，它的大小还是大于目标大小，于是就会发生异常，异常的原因是options超过规定值。所以此处需要判断一下
                    break;
                }
                baos.reset();// 重置baos即清空baos
                options -= 10;// 每次都减少10
                bitmap.compress(Bitmap.CompressFormat.PNG, options, baos);
            }
            return bitmap;
        } else {
            return null;
        }
    }

    //发送评论
    private void sendCommentRequest(final String phonenumber,final String context,final int dynamicId){
        //请求地址
        String url = "http://39.106.154.68:8080/calligrapher/SendCommentsServlet";    //注①
        final String tag = "SendComments";    //注②

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
                            String result = jsonObject.getString("Result");  //注④
                            if (result.equals("success")) {  //发送评论成功
                                handler.sendEmptyMessage(SEND_SUCCESS);
                            } else {
                                handler.sendEmptyMessage(SEND_FAIL);
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
                Toast.makeText(getApplicationContext(), "服务器繁忙，请稍后重试", Toast.LENGTH_LONG).show();
                Log.e("TAG", error.getMessage(), error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("phonenumber",phonenumber);
                params.put("context",context);
                params.put("dynamicId",dynamicId+"");
                return params;
            }
        };
        //设置Tag标签
        request.setTag(tag);
        //将请求添加到队列中
        requestQueue.add(request);
    }

    //得到更多的十条评论
    private void searchMoreCommentsRequest(final int dyId,final int commentId){
        //请求地址
        String url = "http://39.106.154.68:8080/calligrapher/SearchMoreCommentsServlet";    //注①
        final String tag = "SearchMoreComments";    //注②

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
                            String result = jsonObject.getString("Result");  //注④
                            if (result.equals("success")) {  //刷新评论成功
                                Message message=Message.obtain();
                                message.what=GET_COM_SUCCESS;
                                message.obj=jsonObject;
                                handler.sendMessage(message);
                            } else {
                                handler.sendEmptyMessage(GET_COM_FAIL);
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
                Toast.makeText(getApplicationContext(), "服务器繁忙，请稍后重试", Toast.LENGTH_LONG).show();
                Log.e("TAG", error.getMessage(), error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("dynamicId",dyId+"");
                params.put("commentId",commentId+"");
                return params;
            }
        };
        //设置Tag标签
        request.setTag(tag);
        //将请求添加到队列中
        requestQueue.add(request);
    }

    //更新活动任务
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
                        MainActivity.user.setMission3(1);
                        MainActivity.user.setDay_score(MainActivity.user.getDay_score()+5);
                        MainActivity.user.setWeek_score(MainActivity.user.getWeek_score()+5);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //做自己的响应错误操作，如Toast提示（“请稍后重试”等）
                Log.e("TAG", error.getMessage(), error);
                handler.sendEmptyMessage(UPDATE_MISSION_FAIL);
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
