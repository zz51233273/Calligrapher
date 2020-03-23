package com.example.hasee.bluecalligrapher.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
import com.example.hasee.bluecalligrapher.bean.Tracing;
import com.example.hasee.bluecalligrapher.decodebase64.DecodeBase64;
import com.example.hasee.bluecalligrapher.fragment.Fragment2;
import com.example.hasee.bluecalligrapher.fragment.FragmentTracing1;
import com.example.hasee.bluecalligrapher.fragment.FragmentTracing2;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hasee on 2018/5/23.
 */

public class TracingLibraryAdapter extends ArrayAdapter<Character> {
    private int resourceId;
    private Character choose_character;
    private final int SEARCH_SUCCESS=1;
    private final int SEARCH_FAIL=2;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case SEARCH_SUCCESS:
                    getCharacters((JSONObject)msg.obj,choose_character);
                    break;
                case SEARCH_FAIL:
                    Toast.makeText(getContext(), "查询失败，请查看网络", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    public TracingLibraryAdapter(Context context, int textViewResourceId, List<Character> objects){
        super(context,textViewResourceId,objects);
        resourceId=textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Character character = getItem(position); // 获取当前项的index实例
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder = new ViewHolder();

            viewHolder.character = (TextView) view.findViewById (R.id.character);

            view.setTag(viewHolder); // 将ViewHolder存储在View中
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag(); // 重新获取ViewHolder
        }
        if(!"".equals(character))
            viewHolder.character.setText(character+"");

        addListener(view);
        return view;
    }

    private void addListener(View v){
        final TextView textView=v.findViewById(R.id.character);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choose_character=textView.getText().charAt(0);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        searchTracingRequest(choose_character);
                    }
                }).start();
                ((Activity)getContext()).finish();
            }
        });
    }

    //查询文字请求
    private void searchTracingRequest(final Character character){
        //请求地址
        String url="http://39.106.154.68:8080/calligrapher/SearchServlet";
        String tag="SearchServlet";
        RequestQueue requestQueue= Volley.newRequestQueue(getContext().getApplicationContext());
        //取得请求队列
        //防止重复请求，所以先取消tag标识的请求队列
        requestQueue.cancelAll(tag);

        //创建StringRequest，定义字符串请求的请求方式为POST(省略第一个参数会默认为GET方式)
        final StringRequest request= new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = (JSONObject) new JSONObject(response).get("params");  //注③
                            String result = jsonObject.getString("Result");  //注④
                            if (result.equals("success")) {  //刷新动态成功
                                Message message=Message.obtain();
                                message.what=SEARCH_SUCCESS;
                                message.obj=jsonObject;
                                handler.sendMessage(message);
                            } else {
                                handler.sendEmptyMessage(SEARCH_FAIL);
                            }
                        } catch (JSONException e) {
                            //做自己的请求异常操作，如Toast提示（“无网络连接”等）
                            Log.e("TAG", e.getMessage(), e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("search_text",character+"");
                return params;
            }
        };
        //设置Tag标签
        request.setTag(tag);
        //将请求添加到队列中
        requestQueue.add(request);
    }

    private void getCharacters(JSONObject jsonObject,Character character){
        for(int i=0;i<4;i++){
            Fragment2.tracings[i]=new Tracing();
            Fragment2.tracings[i].setCharacter(character.toString());
        }
        try {
            Fragment2.tracings[0].setPicture(DecodeBase64.decodeBase(jsonObject.getString("style_jian")));
            Fragment2.tracings[1].setPicture(DecodeBase64.decodeBase(jsonObject.getString("style_fan")));
            Fragment2.tracings[2].setPicture(DecodeBase64.decodeBase(jsonObject.getString("style_xing")));
            Fragment2.tracings[3].setPicture(DecodeBase64.decodeBase(jsonObject.getString("style_cao")));
            Fragment2.tracings[0].setId(jsonObject.getString("style_jian_id"));
            Fragment2.tracings[1].setId(jsonObject.getString("style_fan_id"));
            Fragment2.tracings[2].setId(jsonObject.getString("style_xing_id"));
            Fragment2.tracings[3].setId(jsonObject.getString("style_cao_id"));
            FragmentTracing1.setImage(Fragment2.tracings[0].getPicture());
            FragmentTracing2.setImage(Fragment2.tracings[1].getPicture());
        }catch (JSONException e){
            Log.e("TAG", e.getMessage(), e);
        }
    }

    class ViewHolder{
        TextView character;       //文字
    }

}
