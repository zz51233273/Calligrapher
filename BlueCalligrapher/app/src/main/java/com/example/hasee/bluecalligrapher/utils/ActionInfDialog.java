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
 * Created by hasee on 2018/8/12.
 */

public class ActionInfDialog extends Dialog implements View.OnClickListener{
    private TextView quitTxt;

    private Context mContext;
    private CommomDialog.OnCloseListener listener;
    private String positiveName;
    private String get_text,value_text,resetting_text;
    private String message,negativeName;

    public ActionInfDialog(Context context) {
        super(context);
        this.mContext = context;
    }

    public ActionInfDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.mContext = context;
    }

    public ActionInfDialog(Context context, int themeResId, String get_text,String value_text,String resetting_text,CommomDialog.OnCloseListener listener) {
        super(context, themeResId);
        this.value_text=value_text;
        this.get_text=get_text;
        this.resetting_text=resetting_text;
        this.mContext = context;
        this.listener = listener;
    }

    protected ActionInfDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
    }

    public ActionInfDialog setMessage(String message){
        this.message = message;
        return this;
    }
    public ActionInfDialog setPositiveButton(String name){
        this.positiveName = name;
        return this;
    }

    public ActionInfDialog setNegativeButton(String name){
        this.negativeName = name;
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_action_inf);
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
        ((TextView)findViewById(R.id.get_text)).setText(get_text);
        ((TextView)findViewById(R.id.value_text)).setText(value_text);
        ((TextView)findViewById(R.id.restting_text)).setText(resetting_text);
        ((TextView)findViewById(R.id.get_text)).setTypeface(typeface);
        ((TextView)findViewById(R.id.value_text)).setTypeface(typeface);
        ((TextView)findViewById(R.id.restting_text)).setTypeface(typeface);
        ((TextView)findViewById(R.id.get_text_inf)).setTypeface(typeface);
        ((TextView)findViewById(R.id.value_text_inf)).setTypeface(typeface);
        ((TextView)findViewById(R.id.restting_text_inf)).setTypeface(typeface);
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
