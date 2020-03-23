package com.example.hasee.bluecalligrapher.poetry;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;


import com.example.hasee.bluecalligrapher.R;
import com.example.hasee.bluecalligrapher.adapter.PoetryListAdapter;
import com.example.hasee.bluecalligrapher.item.PoetryItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hasee on 2018/5/6.
 */

public class SearchPoetryActivity extends AppCompatActivity{
    ListView listView;
    private PoetryListAdapter poetryListAdapter;
    private List<PoetryItem> poetryItems=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_poerty);
        init();
        initPoetry();
    }

    private void init(){
        listView=(ListView)findViewById(R.id.search_poetry_list);
    }

    private void initPoetry(){
        int count=getIntent().getIntExtra("poetry_number",0);
        for(int i=0 ; i<count ; i++){
            PoetryItem poetryItem=new PoetryItem(getIntent().getStringExtra("poetry"+i));
            poetryItems.add(poetryItem);
        }
        poetryListAdapter=new PoetryListAdapter(this,R.layout.poetry_item,poetryItems);
        listView.setAdapter(poetryListAdapter);
    }

}
