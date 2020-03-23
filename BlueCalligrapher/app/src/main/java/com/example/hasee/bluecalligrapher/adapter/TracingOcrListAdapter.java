package com.example.hasee.bluecalligrapher.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.example.hasee.bluecalligrapher.R;

import java.util.List;

/**
 * Created by hasee on 2018/5/5.
 */

public class TracingOcrListAdapter extends ArrayAdapter<String> {
    private int resourceId;

    public TracingOcrListAdapter(Context context, int textViewResourceId, List<String> objects){
        super(context,textViewResourceId,objects);
        resourceId=textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        String character=getItem(position);  // 获取当前项的index实例
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.character = (TextView) view.findViewById (R.id.char_ocr);
            view.setTag(viewHolder); // 将ViewHolder存储在View中
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag(); // 重新获取ViewHolder
        }
        viewHolder.character.setText(character);
        return view;
    }

    class ViewHolder{
        TextView character; //汉字
    }
}
