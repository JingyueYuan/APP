package com.jkf.graduateproject.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jkf.graduateproject.Enum.respCode;
import com.jkf.graduateproject.R;
import com.jkf.graduateproject.application.myApp;
import com.jkf.graduateproject.utils.ShareUtils;
import com.xuexiang.xui.widget.dialog.materialdialog.DialogAction;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.toast.XToast;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import qiu.niorgai.StatusBarCompat;


public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.rg_userEV) EditText userEV;  //
    @BindView(R.id.rg_user_delet) ImageView user_delet;  //
    @BindView(R.id.rg_ps_visible) ImageView ps_visible;   //
    @BindView(R.id.rg_psEV) EditText psEV;   //
    @BindView(R.id.rg_ps_delet) ImageView ps_delet;   //
    @BindView(R.id.rg_submit) Button submit;   //
    @BindView(R.id.toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        StatusBarCompat.setStatusBarColor(this,getResources().getColor(R.color.xui_config_color_titlebar));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        initView();
    }

    private void initView() {
        //
        userEV.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().length()>0){
                    user_delet.setVisibility(View.VISIBLE);
                }else {
                    user_delet.setVisibility(View.INVISIBLE);
                }
            }
        });
        //
        psEV.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().length()>0){
                    ps_delet.setVisibility(View.VISIBLE);
                }else {
                    ps_delet.setVisibility(View.INVISIBLE);
                }
            }
        });
        //
    }

    //
    @OnClick(R.id.rg_user_delet)
    void onClick(){
        userEV.setText("");
    }
    //
    @OnClick(R.id.rg_ps_delet)
    void onClickPS_delet(){
        psEV.setText("");
    }
    //
    @OnClick(R.id.rg_ps_visible)
    void onPS_visibleClick(View view){
        if(view.getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.visible).getConstantState())){
            view.setBackgroundResource(R.drawable.invisible);
            psEV.setInputType(129);
            Selection.setSelection(psEV.getText(),psEV.getText().length());   //
        }else {
            view.setBackgroundResource(R.drawable.visible);
            psEV.setInputType(128);
            Selection.setSelection(psEV.getText(),psEV.getText().length());   //
        }
    }
    //
    /*

     */
    @OnClick(R.id.rg_submit)
    void onSubmitClick(){
        //
        //
        boolean isCanClickable = judgeUserNamePassWord();
        if(isCanClickable){
            //
            judgeInServer();
        }
        else{
            return;
        }
    }

    //
    private boolean judgeUserNamePassWord() {

        if(userEV.getText().length()>=8 && psEV.getText().length()>=8){
            return true;
        }
        else {
            XToast.error(this,"Accounts and passwords need to be at least 8 characters long.",Toast.LENGTH_SHORT).show();
            userEV.setText("");
            psEV.setText("");
            return false;
        }
    }

    /*

    */
    private void judgeInServer() {
        String url = "";
        if(!(ShareUtils.getString(this,"IPHost","").isEmpty())){
            myApp.getInstance().setIpHost(ShareUtils.getString(this,"IPHost",""));
            url = "http://"+myApp.getInstance().getIpHost()+":5000/register";
        }
        else{
            url = "http://172.26.87.174:5000/register";
        }
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("username",userEV.getText().toString().trim());
        builder.add("password",psEV.getText().toString().trim());
        Request request = new Request.Builder().url(url).post(builder.build()).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("XML",e.getMessage());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //iphost
                        new MaterialDialog.Builder(RegisterActivity.this)
                                .iconRes(R.drawable.icon_tip)
                                .title("Tips")
                                .content(R.string.content_warning)
                                .inputType(
                                        InputType.TYPE_NUMBER_FLAG_DECIMAL)
                                .input(
                                        "IP",
                                        "",
                                        true,
                                        new MaterialDialog.InputCallback() {
                                            @Override
                                            public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {

                                            }
                                        }
                                )
                                .inputRange(0, 14)
                                .positiveText("OK")
                                .negativeText("Cancel")
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        String temp = dialog.getInputEditText().getText().toString().trim();
                                        String regex = "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
                                                +"(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                                                +"(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                                                +"(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
                                        if(temp.matches(regex)){
                                            ShareUtils.setString(RegisterActivity.this,"IPHost",temp);
                                        }
                                        else{
                                            XToast.error(RegisterActivity.this,"Wrong ip").show();
                                            dialog.getInputEditText().setText("");
                                        }
                                    }
                                })
                                .cancelable(false)
                                .show();
                        /*
                        new MaterialDialog.Builder(RegisterActivity.this)
                                .iconRes(R.drawable.icon_tip)
                                .title(R.string.dialog_tips)
                                .content(R.string.content_simple_confirm_dialog)
                                .positiveText(R.string.lab_submit)
                                .show();*/
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String str = response.body().string();
                Log.e("XML",str);
                Log.e("XML",response.message().toString());
                Log.e("XML",response.code()+"");
                Looper.prepare();
                //
                if(respCode.SUCCESS.getCode().equals(str)){
                    XToast.normal(RegisterActivity.this,"Registration is successful and will soon jump to login.").show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //
                            Intent intent = new Intent();
                            ArrayList<String> arrayList = new ArrayList<String>();
                            //
                            arrayList.add(userEV.getText().toString().trim());
                            arrayList.add(psEV.getText().toString().trim());
                            intent.putStringArrayListExtra("register",arrayList);
                            setResult(0,intent);
                            finish();
                        }
                    },1000);

                }else if(respCode.ALLWAYS.getCode().equals(str)){
                    XToast.info(RegisterActivity.this,"This account has already been used, please change to a new account").show();
                    userEV.setText("");
                    psEV.setText("");
                }else{
                    XToast.info(RegisterActivity.this,"An unknown error occurred, please exit and re-enter.").show();
                }
                Looper.loop();
            }
        });
    }
}