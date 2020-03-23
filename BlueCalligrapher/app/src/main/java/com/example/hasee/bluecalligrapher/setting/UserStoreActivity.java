package com.example.hasee.bluecalligrapher.setting;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.example.hasee.bluecalligrapher.R;
import com.example.hasee.bluecalligrapher.bean.Tracing;
import com.example.hasee.bluecalligrapher.item.StoreItem;
import com.example.hasee.bluecalligrapher.main.MainActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hasee on 2018/4/2.
 * 用列表显示已收藏的文字
 */

public class UserStoreActivity extends AppCompatActivity{
    private ImageView back; //返回箭头
    private AppAdapter mAdapter;
    private List<StoreItem> storeItems=new ArrayList<>();
    private SwipeMenuCreator creater;
    private SwipeMenuListView listView;
    public static Tracing[] storeTracings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_store_layout);
        init();
        initStore();
    }

    private void initStore() {
        creater=initMenuCreator();
        storeItems.clear();
        int len = MainActivity.storeCharacter.length();
        if (!MainActivity.storeCharacter.equals("")) {
            for (int i = 0; i < len; i+=4) {
                StoreItem storeItem = new StoreItem(MainActivity.storeCharacter.charAt(i) + "");
                storeItems.add(storeItem);
            }
        }
        listView = (SwipeMenuListView) findViewById(R.id.user_store_list);
        mAdapter=new AppAdapter();
        listView.setAdapter(mAdapter);
        listView.setMenuCreator(creater);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                StoreItem storeItem=storeItems.get(arg2);
                searchStore(storeItem.getCharacter());
            }
        });
        //2.菜单点击事件
        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                final StoreItem storeItem=storeItems.get(position);
                switch (index) {
                    case 0:
                        //删除的逻辑
                        storeItems.remove(position);
                        mAdapter.notifyDataSetChanged();
                        //storeCharacter中也要删除相应的汉字
                        int pos=MainActivity.storeCharacter.indexOf(storeItem.getCharacter());
                        MainActivity.storeCharacter=MainActivity.storeCharacter.replace(storeItem.getCharacter(),"");
                        MainActivity.storeStyle=MainActivity.storeStyle.substring(0,pos)+MainActivity.storeStyle.substring(pos+4,MainActivity.storeStyle.length());
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                cancelStoreRequest(MainActivity.user.getPhoneNumber(),storeItem.getCharacter());
                            }
                        }).start();
                        break;
                }
                // false : not close the menu; true : close the menu
                return true;
            }
        });
    }

    private void init(){
        AssetManager mgr=getAssets();   //得到AssetManager
        Typeface typeface=Typeface.createFromAsset(mgr,"fonts/simhei.ttf");//改变字体
        ((TextView)findViewById(R.id.toolbar_store)).setTypeface(typeface);
    }

    private SwipeMenuCreator initMenuCreator(){
        SwipeMenuCreator creater = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                //同理create删除item
                SwipeMenuItem deleteItem = new SwipeMenuItem(UserStoreActivity.this);
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(dp2px(50));
                // set a icon
                deleteItem.setIcon(R.drawable.ic_delete);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };
        return creater;
    }

    //在服务器上删除该用户收藏的字
    private void cancelStoreRequest(final String phonenumber,final String character){
        //请求地址
        String url = "http://39.106.154.68:8080/calligrapher/CancelStoreServlet";    //注①
        String tag = "cancelStore";    //注②

        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        //取得请求队列
        //防止重复请求，所以先取消tag标识的请求队列
        requestQueue.cancelAll(tag);

        //创建StringRequest，定义字符串请求的请求方式为POST(省略第一个参数会默认为GET方式)
        final StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

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
                params.put("character",character);
                return params;
            }
        };
        //设置Tag标签
        request.setTag(tag);
        //将请求添加到队列中
        requestQueue.add(request);
    }

    private void searchStore(String text){
        int pos= MainActivity.storeCharacter.indexOf(text);
        storeTracings=new Tracing[4];
        for(int i=0;i<4;i++){
            storeTracings[i]=new Tracing();
            storeTracings[i].setCharacter(text);
        }
        if(pos!=-1){
            for(int j=pos;j<=pos+3;j++){
                switch (MainActivity.storeStyle.charAt(j)){
                    case '简':
                        storeTracings[0].setPicture(MainActivity.store[j].getPicture());
                        break;
                    case '繁':
                        storeTracings[1].setPicture(MainActivity.store[j].getPicture());
                        break;
                    case '行':
                        storeTracings[2].setPicture(MainActivity.store[j].getPicture());
                        break;
                    case '草':
                        storeTracings[3].setPicture(MainActivity.store[j].getPicture());
                        break;
                }
            }
        }
        startActivity(new Intent(this, UserStoreCharActivity.class));
    }

    class AppAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return storeItems.size();
        }

        @Override
        public StoreItem getItem(int position) {
            return storeItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getViewTypeCount() {
            // menu type count
            return 3;
        }

        @Override
        public int getItemViewType(int position) {
            // current menu type
            return position % 3;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(),
                        R.layout.store_item, null);
                new ViewHolder(convertView);
            }
            ViewHolder holder = (ViewHolder) convertView.getTag();
            StoreItem item = getItem(position);
            holder.character.setText(item.getCharacter());
            return convertView;
        }

        class ViewHolder {
            TextView character; //汉字

            public ViewHolder(View view) {
                character = (TextView) view.findViewById (R.id.store_item_char);
                view.setTag(this);
            }
        }
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

}
