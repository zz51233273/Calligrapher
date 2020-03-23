package com.example.hasee.bluecalligrapher.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;


import com.example.hasee.bluecalligrapher.R;
import com.example.hasee.bluecalligrapher.item.LetterItem;
import com.example.hasee.bluecalligrapher.letter.UserLetterInfoActivity;
import com.example.hasee.bluecalligrapher.userinfo.UserInfoActivity;

import java.io.ByteArrayOutputStream;
import java.util.List;

import circleimageview.CircleImageView;

/**
 * Created by hasee on 2018/6/18.
 */

public class MessageLetterListAdapter extends ArrayAdapter<LetterItem>{
    private int resourceId;

    public MessageLetterListAdapter(Context context, int textViewResourceId, List<LetterItem> objects){
        super(context,textViewResourceId,objects);
        resourceId=textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LetterItem letterItem=getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.head=(CircleImageView)view.findViewById(R.id.letter_item_head);
            viewHolder.sender=(TextView)view.findViewById(R.id.letter_item_writer);
            viewHolder.context=(TextView)view.findViewById(R.id.letter_item_context);
            viewHolder.time_text=(TextView)view.findViewById(R.id.letter_item_date);
            view.setTag(viewHolder); // 将ViewHolder存储在View中
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag(); // 重新获取ViewHolder
        }
        viewHolder.head.setImageBitmap(letterItem.getHead());
        viewHolder.sender.setText(letterItem.getSender());
        viewHolder.context.setText(letterItem.getContext());
        viewHolder.time_text.setText(letterItem.getTime());
        addListener(view,letterItem);
        return view;
    }

    private void addListener(View view, final LetterItem letterItem){
        ((FrameLayout)view.findViewById(R.id.letter_item)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchDetailInfo(letterItem);
            }
        });

        ((CircleImageView)view.findViewById(R.id.letter_item_head)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchUserInfo(letterItem);
            }
        });
    }

    //查看私信详细信息
    private void searchDetailInfo(LetterItem letterItem){
        Intent i=new Intent(getContext(), UserLetterInfoActivity.class);
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        letterItem.getHead().compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte [] bitmapByte =baos.toByteArray();
        i.putExtra("head",bitmapByte);
        i.putExtra("sender",letterItem.getSender_name());
        i.putExtra("context",letterItem.getContext());
        i.putExtra("time",letterItem.getTime());
        i.putExtra("img",letterItem.getImg());
        getContext().startActivity(i);
    }

    //点击头像后查询用户信息
    private void searchUserInfo(LetterItem letterItem){
        Intent i=null;
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        letterItem.getHead().compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte [] bitmapByte =baos.toByteArray();
        i=new Intent(getContext(), UserInfoActivity.class);
        i.putExtra("head",bitmapByte);
        i.putExtra("username",letterItem.getSender_name());
        i.putExtra("phonenumber",letterItem.getSender());
        getContext().startActivity(i);
    }

    class ViewHolder{
        CircleImageView head;   //头像
        TextView sender;        //发送私信者
        TextView context;       //发送内容
        TextView time_text;     //发送时间
    }
}
