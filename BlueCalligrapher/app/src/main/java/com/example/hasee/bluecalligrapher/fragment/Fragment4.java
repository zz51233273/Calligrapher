package com.example.hasee.bluecalligrapher.fragment;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.hasee.bluecalligrapher.R;
import com.example.hasee.bluecalligrapher.adapter.PoetryListAdapter;
import com.example.hasee.bluecalligrapher.item.PoetryItem;
import com.example.hasee.bluecalligrapher.poetry.SearchPoetryActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hasee on 2018/3/30.
 */

public class Fragment4 extends Fragment{
    private EditText poetry_search;
    private ListView listView;
    private PoetryListAdapter poetryListAdapter;
    private List<PoetryItem> poetryItems=new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view=inflater.inflate(R.layout.fg_poetry,container, false);
        init(view);
        initPoetry();
        return view;
    }

    private void init(View view){
        poetry_search=(EditText)view.findViewById(R.id.edit_poetry_context);
        poetry_search.setOnEditorActionListener(new EditText.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH){          //点击搜索键
                    String poetry=poetry_search.getText().toString();
                    Intent i=new Intent(getContext(), SearchPoetryActivity.class);
                    boolean hasPoetry=false;
                    int count=0;
                    for(PoetryItem poetryItem : poetryItems){       //判断搜索框中输入的作品是否存在
                        if(poetryItem.getName().contains(poetry)){
                            i.putExtra("poetry"+count,poetryItem.getName());
                            count++;
                            hasPoetry=true;
                        }
                    }
                    if(hasPoetry){      //存在作品就跳转
                        i.putExtra("poetry_number",count);
                        startActivity(i);
                    }else{
                        Toast.makeText(getContext(), "无搜索结果", Toast.LENGTH_LONG).show();
                    }
                    return true;
                }
                return false;
            }
        });
        listView=(ListView)view.findViewById(R.id.fg_poetry_list);
    }
    private void initPoetry(){
        poetryItems.clear();
        PoetryItem poetryItem=new PoetryItem("黄鹤楼送孟浩然之广陵  |  山行");
        poetryItem.setPic(BitmapFactory.decodeResource(getResources(),R.drawable.work_001));
        poetryItems.add(poetryItem);
        poetryItem=new PoetryItem("草/赋得古原草送别");
        poetryItem.setPic(BitmapFactory.decodeResource(getResources(),R.drawable.work_002));
        poetryItems.add(poetryItem);
        poetryItem=new PoetryItem("鹿柴  |  静夜思\n寻隐者不遇  |  江雪\n悯农二首  |  登乐游原");
        poetryItem.setPic(BitmapFactory.decodeResource(getResources(),R.drawable.work_003));
        poetryItems.add(poetryItem);
        poetryItem=new PoetryItem("惠崇春江晚景  |  题西林壁");
        poetryItem.setPic(BitmapFactory.decodeResource(getResources(),R.drawable.work_004));
        poetryItems.add(poetryItem);
        poetryItem=new PoetryItem("赏鹅池");
        poetryItem.setPic(BitmapFactory.decodeResource(getResources(),R.drawable.work_005));
        poetryItems.add(poetryItem);
        poetryListAdapter=new PoetryListAdapter(getContext(),R.layout.poetry_item,poetryItems);
        listView.setAdapter(poetryListAdapter);
    }

}
