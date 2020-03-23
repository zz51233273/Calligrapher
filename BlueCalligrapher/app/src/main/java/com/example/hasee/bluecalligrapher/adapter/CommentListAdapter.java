package com.example.hasee.bluecalligrapher.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.example.hasee.bluecalligrapher.R;
import com.example.hasee.bluecalligrapher.item.CommentItem;

import java.util.List;

import circleimageview.CircleImageView;

/**
 * Created by hasee on 2018/4/30.
 */

public class CommentListAdapter extends ArrayAdapter<CommentItem>{
    private int resourceId;

    public CommentListAdapter(Context context, int textViewResourceId, List<CommentItem> objects){
        super(context,textViewResourceId,objects);
        resourceId=textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CommentItem commentItem=getItem(position);
        View view;
        ViewCommentHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder = new ViewCommentHolder();
            viewHolder.head=(CircleImageView)view.findViewById(R.id.comment_item_head);
            viewHolder.writer=(TextView)view.findViewById(R.id.comment_item_writer);
            viewHolder.context=(TextView)view.findViewById(R.id.comment_item_content);
            view.setTag(viewHolder); // 将ViewHolder存储在View中
        } else {
            view = convertView;
            viewHolder = (ViewCommentHolder) view.getTag(); // 重新获取ViewHolder
        }
        viewHolder.head.setImageBitmap(commentItem.getHead());
        viewHolder.writer.setText(commentItem.getWriter());
        viewHolder.context.setText(commentItem.getContext());
        return view;
    }
}

class ViewCommentHolder{
    CircleImageView head;   //头像
    TextView writer;        //评论作者
    TextView context;       //评论内容
}

