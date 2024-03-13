package com.jkf.graduateproject.myViews;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.jkf.graduateproject.R;

public class myInputDialog extends Dialog implements View.OnClickListener{
    public EditText contentTxt;
    private TextView titleTxt;
    private TextView submitTxt;
    private TextView cancelTxt;

    private Context mContext;
    private String content;
    private OnCloseListener listener;
    private String positiveName;
    private String negativeName;
    private String title;

    public myInputDialog(Context context) {
        super(context);
        this.mContext = context;
    }

    public myInputDialog(Context context, int themeResId, String content) {
        super(context, themeResId);
        this.mContext = context;
        this.content = content;
    }

    public myInputDialog(Context context, int themeResId,  OnCloseListener listener) {
        super(context, themeResId);
        this.mContext = context;
        this.listener = listener;
    }

    protected myInputDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
    }

    public myInputDialog setTitle(String title){
        this.title = title;
        return this;
    }

    public myInputDialog setPositiveButton(String name){
        this.positiveName = name;
        return this;
    }

    public myInputDialog setNegativeButton(String name){
        this.negativeName = name;
        return this;
    }

    public myInputDialog setContent(String str){
        this.content = str;
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.input_dialog);
        setCanceledOnTouchOutside(false);
        initView();
    }

    private void initView(){
        contentTxt = (EditText) findViewById(R.id.content);
        titleTxt = (TextView)findViewById(R.id.title);
        submitTxt = (TextView)findViewById(R.id.submit);
        submitTxt.setOnClickListener(this);
        cancelTxt = (TextView)findViewById(R.id.cancel);
        cancelTxt.setOnClickListener(this);

        // contentTxt.setText(content);
        if(!TextUtils.isEmpty(positiveName)){
            submitTxt.setText(positiveName);
        }

        if(!TextUtils.isEmpty(negativeName)){
            cancelTxt.setText(negativeName);
        }

        if(!TextUtils.isEmpty(title)){
            titleTxt.setText(title);
        }
        if(!TextUtils.isEmpty(content)){
            contentTxt.setText(content);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cancel:
                if(listener != null){
                    listener.onClickCancel(this, false);
                }
                this.dismiss();
                break;
            case R.id.submit:
                if(listener != null){
                    listener.onClickSubmit(this, contentTxt.getText().toString().trim());
                }
                break;
        }
    }
    public interface OnCloseListener{
        void onClickCancel(Dialog dialog, boolean confirm);
        void onClickSubmit(Dialog dialog, String confirm);
    }
}
