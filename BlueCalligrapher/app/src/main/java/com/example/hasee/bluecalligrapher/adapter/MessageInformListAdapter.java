package com.example.hasee.bluecalligrapher.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.example.hasee.bluecalligrapher.R;
import com.example.hasee.bluecalligrapher.item.InformItem;

import java.util.List;

import circleimageview.CircleImageView;

/**
 * Created by hasee on 2018/5/29.
 */

public class MessageInformListAdapter extends ArrayAdapter<InformItem> {
    private int resourceId;

    public MessageInformListAdapter(Context context, int textViewResourceId, List<InformItem> objects){
        super(context,textViewResourceId,objects);
        resourceId=textViewResourceId;

    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        InformItem informItem=getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.head=(CircleImageView)view.findViewById(R.id.inform_item_head);
            viewHolder.writer=(TextView)view.findViewById(R.id.inform_item_writer);
            viewHolder.context=(TextView)view.findViewById(R.id.inform_item_title);
            viewHolder.dy_context=(TextView)view.findViewById(R.id.inform_item_context);
            viewHolder.time_text=(TextView)view.findViewById(R.id.inform_item_date);
            view.setTag(viewHolder); // 将ViewHolder存储在View中
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag(); // 重新获取ViewHolder
        }
        viewHolder.head.setImageBitmap(informItem.getHeadId());
        viewHolder.writer.setText(informItem.getUsername());
        viewHolder.context.setText(informItem.getContext());
        viewHolder.dy_context.setText(informItem.getDy_context());
        viewHolder.time_text.setText(informItem.getTime());
        return view;
    }
    class ViewHolder{
        CircleImageView head;   //头像
        TextView writer;        //通知人
        TextView context;       //通知标题
        TextView dy_context;    //通知动态内容
        TextView time_text;     //通知时间
    }
}
