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

    @BindView(R.id.rg_userEV) EditText userEV;  //账号信息
    @BindView(R.id.rg_user_delet) ImageView user_delet;  //删除按钮
    @BindView(R.id.rg_ps_visible) ImageView ps_visible;   //密码是否可见
    @BindView(R.id.rg_psEV) EditText psEV;   //密码信息
    @BindView(R.id.rg_ps_delet) ImageView ps_delet;   //密码删除按钮
    @BindView(R.id.rg_submit) Button submit;   //登录按钮
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
        //账户信息的监听，是否开始输入
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
        //密码信息的监听，是否开始输入
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
        //是否记住密码，监听
    }

    //账户删除控件
    @OnClick(R.id.rg_user_delet)
    void onClick(){
        userEV.setText("");
    }
    //密码删除按钮
    @OnClick(R.id.rg_ps_delet)
    void onClickPS_delet(){
        psEV.setText("");
    }
    //密码是否可见
    @OnClick(R.id.rg_ps_visible)
    void onPS_visibleClick(View view){
        if(view.getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.visible).getConstantState())){
            view.setBackgroundResource(R.drawable.invisible);
            psEV.setInputType(129);
            Selection.setSelection(psEV.getText(),psEV.getText().length());   //设置光标的位置
        }else {
            view.setBackgroundResource(R.drawable.visible);
            psEV.setInputType(128);
            Selection.setSelection(psEV.getText(),psEV.getText().length());   //设置光标的位置
        }
    }
    //注册信息逻辑，在此处后期需要联网控制，验证登录
    /*
    首先检验检验输入的信息
    okhttp3上传网址，并验证
    根据不同的返回结果进行不同的提示
     */
    @OnClick(R.id.rg_submit)
    void onSubmitClick(){
        //检查是否为空
        //检测密码是否为空，然后进行提醒
        boolean isCanClickable = judgeUserNamePassWord();
        if(isCanClickable){
            //与服务器进行交互，验证结果是否正确
            judgeInServer();
        }
        else{
            return;
        }
    }

    //判断用户名与密码的合理性
    private boolean judgeUserNamePassWord() {

        if(userEV.getText().length()>=8 && psEV.getText().length()>=8){
            return true;
        }
        else {
            XToast.error(this,"账户与密码至少需要8个字符",Toast.LENGTH_SHORT).show();
            userEV.setText("");
            psEV.setText("");
            return false;
        }
    }

    /*
   与服务器进行交互，判断是否存在数据库
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
                        //输入新的iphost
                        new MaterialDialog.Builder(RegisterActivity.this)
                                .iconRes(R.drawable.icon_tip)
                                .title("提示")
                                .content(R.string.content_warning)
                                .inputType(
                                        InputType.TYPE_NUMBER_FLAG_DECIMAL)
                                .input(
                                        "服务器IP地址",
                                        "",
                                        true,
                                        new MaterialDialog.InputCallback() {
                                            @Override
                                            public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {

                                            }
                                        }
                                )
                                .inputRange(0, 14)
                                .positiveText("确定")
                                .negativeText("取消")
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
                                            XToast.error(RegisterActivity.this,"ip格式不正确").show();
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
                // 123  注册成功
                if(respCode.SUCCESS.getCode().equals(str)){
                    XToast.normal(RegisterActivity.this,"注册成功，即将跳转登录。。。").show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //将登录信息带回
                            Intent intent = new Intent();
                            ArrayList<String> arrayList = new ArrayList<String>();
                            //账号在前，密码在后
                            arrayList.add(userEV.getText().toString().trim());
                            arrayList.add(psEV.getText().toString().trim());
                            intent.putStringArrayListExtra("register",arrayList);
                            setResult(0,intent);
                            finish();
                        }
                    },1000);

                }else if(respCode.ALLWAYS.getCode().equals(str)){
                    XToast.info(RegisterActivity.this,"此账号已经被使用，请更换新的账号").show();
                    userEV.setText("");
                    psEV.setText("");
                }else{
                    XToast.info(RegisterActivity.this,"发生未知错误，请退出重进。。。").show();
                }
                Looper.loop();
            }
        });
    }
}