package com.jkf.graduateproject.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;
import com.jkf.graduateproject.Enum.respCode;
import com.jkf.graduateproject.R;
import com.jkf.graduateproject.application.myApp;
import com.jkf.graduateproject.utils.NetUtils;
import com.jkf.graduateproject.utils.ShareUtils;
import com.xuexiang.xui.widget.button.ButtonView;
import com.xuexiang.xui.widget.dialog.materialdialog.DialogAction;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.edittext.ClearEditText;
import com.xuexiang.xui.widget.edittext.PasswordEditText;
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

public class ForgetPWActivity extends AppCompatActivity {
    //控件的绑定
    @BindView(R.id.toolbar)Toolbar toolbar;
    @BindView(R.id.input_layout_username) TextInputLayout input_layout_username;
    @BindView(R.id.input_username) ClearEditText input_username;
    @BindView(R.id.input_layout_password) TextInputLayout input_layout_password;
    @BindView(R.id.input_password) PasswordEditText input_password;
    @BindView(R.id.input_layout_password2) TextInputLayout input_layout_password2;
    @BindView(R.id.input_password2) PasswordEditText input_password2;
    @BindView(R.id.btnView) ButtonView btnView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_p_w);
        ButterKnife.bind(this);
        //初始化控件
        init_view();
    }

    private void init_view() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //携带过来的账号信息
        input_username.setText(getIntent().getStringExtra("user"));
    }

    /**
     * 用于展示错误的提示信息
     * @param inputLayout   布局
     * @param message   错误的消息
     */
    private void showError(TextInputLayout inputLayout, String message){
        inputLayout.setError(message);
        inputLayout.getEditText().setFocusable(true);
        inputLayout.getEditText().setFocusableInTouchMode(true);
        inputLayout.getEditText().requestFocus();
    }
    @OnClick(R.id.btnView)
    void onClick(){
        //首先判空
        if(input_username.getText().toString().trim().isEmpty()){
            showError(input_layout_username,"用户名不能为空");
            return;
        }
        if(input_password.getText().toString().trim().isEmpty()){
            showError(input_layout_password,"密码不能为空");
            return;
        }
        if(input_password2.getText().toString().trim().isEmpty()){
            showError(input_layout_password2,"密码不能为空");
            return;
        }
        if( ! input_password2.getText().toString().trim().equals(input_password.getText().toString().trim())){
            showError(input_layout_password2,"两次输入的密码不一致");
            return;
        }
        //开始验证，利用okhttp3数据进行上传
        if(NetUtils.checkNet(ForgetPWActivity.this)){
            String url = "";
            if(!(ShareUtils.getString(this,"IPHost","").isEmpty())){
                myApp.getInstance().setIpHost(ShareUtils.getString(this,"IPHost",""));
                url = "http://"+myApp.getInstance().getIpHost()+":5000/forgetPS";
            }
            else{
                url = "http://172.26.87.174:5000/forgetPS";
            }
            OkHttpClient client = new OkHttpClient();
            FormBody.Builder builder = new FormBody.Builder();
            builder.add("username",input_username.getText().toString().trim());
            builder.add("password",input_password2.getText().toString().trim());
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
                            new MaterialDialog.Builder(ForgetPWActivity.this)
                                    .iconRes(R.drawable.icon_tip)
                                    .title("Tip")
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
                                                ShareUtils.setString(ForgetPWActivity.this,"IPHost",temp);
                                            }
                                            else{
                                                XToast.error(ForgetPWActivity.this,"ip格式不正确").show();
                                                dialog.getInputEditText().setText("");
                                            }
                                        }
                                    })
                                    .cancelable(false)
                                    .show();
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
                    // 159--->修改成功
                    if(respCode.EDITPWSUCC.getCode().equals(str)){
                        XToast.normal(ForgetPWActivity.this,"修改成功，即将跳转登录。。。。").show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent();
                                ArrayList<String> args = new ArrayList<>();
                                args.add(input_username.getText().toString().trim());
                                args.add(input_password.getText().toString().trim());
                                intent.putExtra("forgetpw",args);
                                setResult(0,intent);
                                finish();
                            }
                        },1000);
                        // 357 ---> 修改失败
                    }else if(respCode.EDITPWFAIL.getCode().equals(str)){
                        XToast.info(ForgetPWActivity.this,"此用户先注册").show();
                    }else{
                        XToast.info(ForgetPWActivity.this,"发生未知错误，请退出重新进入。。。").show();
                    }
                    Looper.loop();
                }
            });
        }
        else{
            new MaterialDialog.Builder(this)
                    .iconRes(R.drawable.no_net_icon)
                    .title(R.string.no_net_error)
                    .content(R.string.netUnConneted)
                    .positiveText(R.string.lab_submit)
                    .show();
        }
    }
}