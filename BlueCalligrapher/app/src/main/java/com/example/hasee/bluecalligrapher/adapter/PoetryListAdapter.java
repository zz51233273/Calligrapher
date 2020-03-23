package com.example.hasee.bluecalligrapher.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.hasee.bluecalligrapher.R;
import com.example.hasee.bluecalligrapher.item.PoetryItem;
import com.example.hasee.bluecalligrapher.poetry.PoetryActivity;

import java.util.List;

/**
 * Created by hasee on 2018/4/8.
 */

public class PoetryListAdapter extends ArrayAdapter<PoetryItem> {
    private int resourceId;

    public PoetryListAdapter(Context context, int textViewResourceId, List<PoetryItem> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PoetryItem poetryItem = getItem(position);  // 获取当前项的index实例
        View view;
        ViewHolder3 viewHolder3;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder3 = new ViewHolder3();
            viewHolder3.textView = (TextView) view.findViewById(R.id.poetry_item_name);
            viewHolder3.imageView = (ImageView)view.findViewById(R.id.poetry_item_pic);
            view.setTag(viewHolder3); // 将ViewHolder存储在View中
        } else {
            view = convertView;
            viewHolder3 = (ViewHolder3) view.getTag(); // 重新获取ViewHolder
        }
        viewHolder3.textView.setText(poetryItem.getName());
        viewHolder3.imageView.setImageBitmap(poetryItem.getPic());
        AssetManager mgr=getContext().getAssets();   //得到AssetManager
        Typeface typeface=Typeface.createFromAsset(mgr,"fonts/STXINGKA.TTF");//改变字体
        viewHolder3.textView.setTypeface(typeface);
        addListener(view);
        return view;
    }

    public void addListener(View view) {
        ((TextView)view.findViewById(R.id.poetry_item_name)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String text=((TextView)v.findViewById(R.id.poetry_item_name)).getText().toString();
                        searchPoetry(text);
                    }
                });
    }
    private void searchPoetry(String name){
        final String s1="黄鹤楼送孟浩然之广陵  |  山行";
        final String s2="草/赋得古原草送别";
        final String s3="鹿柴  |  静夜思\n寻隐者不遇  |  江雪\n悯农二首  |  登乐游原";
        final String s4="惠崇春江晚景  |  题西林壁";
        final String s5="赏鹅池";
        Intent i=new Intent(getContext(), PoetryActivity.class);
        switch (name){
            case s1:
                i.putExtra("image_url","shufa/001.jpg");
                i.putExtra("poetryId",1);
                break;
            case s2:
                i.putExtra("image_url","shufa/002.jpg");
                i.putExtra("poetryId",2);
                break;
            case s3:
                i.putExtra("image_url","shufa/003.jpg");
                i.putExtra("poetryId",3);
                break;
            case s4:
                i.putExtra("image_url","shufa/004.jpg");
                i.putExtra("poetryId",4);
                break;
            case s5:
                i.putExtra("image_url","shufa/005.jpg");
                i.putExtra("poetryId",5);
                break;
        }
        getContext().startActivity(i);
    }
}

class ViewHolder3{
    TextView textView; //作品
    ImageView imageView; //作品缩略图
}