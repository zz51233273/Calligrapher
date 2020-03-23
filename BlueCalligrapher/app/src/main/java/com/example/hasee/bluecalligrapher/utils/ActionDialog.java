package com.example.hasee.bluecalligrapher.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.example.hasee.bluecalligrapher.R;


/**
 * Created by hasee on 2018/8/9.
 */

public class ActionDialog extends Dialog implements View.OnClickListener{
    private TextView quitTxt;

    private Context mContext;
    private CommomDialog.OnCloseListener listener;
    private String positiveName;
    private String mission,value;
    private String message,negativeName;

    public ActionDialog(Context context) {
        super(context);
        this.mContext = context;
    }

    public ActionDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.mContext = context;
    }

    public ActionDialog(Context context, int themeResId, String mission,String value,CommomDialog.OnCloseListener listener) {
        super(context, themeResId);
        this.mission=mission;
        this.value=value;
        this.mContext = context;
        this.listener = listener;
    }

    protected ActionDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
    }

    public ActionDialog setMessage(String message){
        this.message = message;
        return this;
    }
    public ActionDialog setPositiveButton(String name){
        this.positiveName = name;
        return this;
    }

    public ActionDialog setNegativeButton(String name){
        this.negativeName = name;
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_action);
        setCanceledOnTouchOutside(false);
        initView();
    }

    private void initView(){
        quitTxt = (TextView)findViewById(R.id.ok);
        quitTxt.setOnClickListener(this);
        if(!TextUtils.isEmpty(positiveName)){
            quitTxt.setText(positiveName);
        }
        AssetManager mgr=getContext().getAssets();   //得到AssetManager
        Typeface typeface=Typeface.createFromAsset(mgr,"fonts/STXINWEI.TTF");//改变字体
        ((TextView)findViewById(R.id.misson_text)).setText(mission);
        ((TextView)findViewById(R.id.value_text)).setText(value);
        ((TextView)findViewById(R.id.misson_text)).setTypeface(typeface);
        ((TextView)findViewById(R.id.value_text)).setTypeface(typeface);
        typeface=Typeface.createFromAsset(mgr,"fonts/STXINGKA.TTF");//改变字体
        ((TextView)findViewById(R.id.ok)).setTypeface(typeface);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ok:
                if(listener != null){
                    listener.onClick(this, true);
                }
                break;
        }
    }

    public interface OnCloseListener{
        void onClick(Dialog dialog, boolean confirm);
    }
}
