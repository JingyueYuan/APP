package com.jkf.graduateproject.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jkf.graduateproject.Enum.respCode;
import com.jkf.graduateproject.HelperClass.okhttpListener;
import com.jkf.graduateproject.R;
import com.jkf.graduateproject.application.myApp;
import com.jkf.graduateproject.myViews.myRationaleDialog;
import com.jkf.graduateproject.utils.FileUtil;
import com.jkf.graduateproject.utils.Md5Utils;
import com.jkf.graduateproject.utils.NetUtils;
import com.jkf.graduateproject.utils.ShareUtils;
import com.jkf.graduateproject.utils.okhttpUtil;
import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.ExplainReasonCallback;
import com.permissionx.guolindev.callback.ForwardToSettingsCallback;
import com.permissionx.guolindev.callback.RequestCallback;
import com.permissionx.guolindev.request.ExplainScope;
import com.permissionx.guolindev.request.ForwardScope;
import com.xuexiang.xui.widget.dialog.materialdialog.DialogAction;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.edittext.materialedittext.MaterialEditText;
import com.xuexiang.xui.widget.imageview.preview.PreviewBuilder;
import com.xuexiang.xui.widget.toast.XToast;


import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import qiu.niorgai.StatusBarCompat;

import static com.xuexiang.xui.XUI.getContext;

public class LoginActivity extends AppCompatActivity {
    @BindView(R.id.userEV) EditText userEV;  //账号信息
    @BindView(R.id.user_delet) ImageView user_delet;  //删除按钮
    @BindView(R.id.ps_visible) ImageView ps_visible;   //密码是否可见
    @BindView(R.id.psEV) EditText psEV;   //密码信息
    @BindView(R.id.ps_delet) ImageView ps_delet;   //密码删除按钮
    @BindView(R.id.repsCheck) CheckBox repsCheck;   //是否记住密码
    @BindView(R.id.submit) Button submit;   //登录按钮
    @BindView(R.id.forgetPs) TextView forgetPs;   //忘记密码操作
    @BindView(R.id.registerPs) TextView registerPs;   //用户注册操作

    private boolean isSavePS = false;
    private boolean isNetConnected = false;

    //注册界面的请求code
    private static int LOGIN_2_REGI = 10;
    private static int LOGIN_2_FORG = 20;

    //登陆时等待加载框
    private MaterialDialog dialog;


    //权限是否全部具备
    private boolean isAllPermissionCheked = false;
    private String [] permissionsList = new String [] {
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.INTERNET,
        Manifest.permission.CAMERA};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        StatusBarCompat.setStatusBarColor(this,getResources().getColor(R.color.xui_config_color_titlebar));
        ButterKnife.bind(this);
        //申请权限
        requestPermissions();
        init_dialog();
        //控件绑定相应的监听事件
        init_view();
        judgeNetConnected();
        //检查是否已经记住了密码，然后直接进行设置即可
        checkUserInfos();
    }

    private void init_dialog() {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(this)
                .title("Tip")
                .limitIconToDefaultSize()
                .content(R.string.wait_for_login)
                .progress(true, 0)
                .progressIndeterminateStyle(false);
        dialog = builder.build();
    }

    /*
    判断此时网络是否可用，如果不可用，就给出提示
     */
    private void judgeNetConnected() {
        isNetConnected = NetUtils.checkNet(this);
        if(!isNetConnected){
            new MaterialDialog.Builder(this)
                    .iconRes(R.drawable.no_net_icon)
                    .title(R.string.no_net_error)
                    .content(R.string.netUnConneted)
                    .positiveText(R.string.lab_submit)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                            Intent intent =  new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                            startActivity(intent);
                        }
                    })
                    .show();
        }
    }

    private void init_view() {
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
        //一开始密码不可见设置
        psEV.setInputType(129);
    }
    //是否记住密码监听
    @OnCheckedChanged(R.id.repsCheck)
    void onOnCheckedChanged(boolean isChecked){
        if(isChecked){
            isSavePS = isChecked;
        }
        else{
            isSavePS = isChecked;
        }
    }
    //账户删除控件
    @OnClick(R.id.user_delet)
    void onClick(){
        userEV.setText("");
    }
    //密码删除按钮
    @OnClick(R.id.ps_delet)
    void onClickPS_delet(){
        psEV.setText("");
    }
    //登录的逻辑，在此处后期需要联网控制，验证登录
    @OnClick(R.id.submit)
    void onSubmitClick(){
        //当前网络是否可用
        if(!NetUtils.checkNet(LoginActivity.this)){
            XToast.error(this,"当前网络不可用，请确保网络已经打开",Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isCanClickable = judgeUserNamePassWord();
        if(isCanClickable){
            //与服务器进行交互，验证结果是否正确
            judgeInServer();
        }
        else{
            return;
        }
    }
    /*
    与服务器进行交互，判断是否存在数据库
     */
    private void judgeInServer() {
        HashMap<String,String> map = new HashMap<>();
        map.put("username",userEV.getText().toString().trim());
        map.put("password",psEV.getText().toString().trim());

        String url = "";
        if(!(ShareUtils.getString(this,"IPHost","").isEmpty())){
            myApp.getInstance().setIpHost(ShareUtils.getString(this,"IPHost",""));
            url = "http://"+myApp.getInstance().getIpHost()+":5000/login";
        }
        else{
            url = "http://172.26.242.204:5000/login";
        }
        okhttpUtil.getInstance().postForms(url, map, new okhttpListener() {
            @Override
            public void onFailure(Call call,Exception e) {
                Log.e("XML",e.getMessage());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //输入新的iphost
                        new MaterialDialog.Builder(LoginActivity.this)
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

                                .positiveText("确定")
                                .negativeText("取消")
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        String temp = dialog.getInputEditText().getText().toString().trim();

                                        ShareUtils.setString(LoginActivity.this,"IPHost",temp);



                                    }
                                })
                                .cancelable(false)
                                .show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException{
                String str = response.body().string();
                Log.e("XML",str);
                Log.e("XML",response.message().toString());
                Log.e("XML",response.code()+"");
                Looper.prepare();
                if(respCode.LOGIN.getCode().equals(str)){
                    XToast.success(LoginActivity.this,"Log in").show();
                    //保存密码，利用sharedPrefrence
                    saveUserInfos();
                    //创建文件夹,本期存储在此文件夹下面
                    FileUtil.creatRootDir(userEV.getText().toString().trim());
                    myApp.getInstance().setUserName(userEV.getText().toString().trim());
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    },1000);
                }else if(respCode.LOGINFAIL.getCode().equals(str)){
                    XToast.info(LoginActivity.this,"用户名或者密码错误").show();
                }else if(respCode.LOGINUNRE.getCode().equals(str)){
                    XToast.error(LoginActivity.this,"此账号还未注册，请进行注册").show();
                }else{
                    XToast.info(LoginActivity.this,"发生未知错误，请退出重新进入。。。").show();
                }
                Looper.loop();
            }
        });
    }

    //判断用户名与密码的合理性
    private boolean judgeUserNamePassWord() {

        if(userEV.getText().length()!=0 && psEV.getText().length()!=0){
            return true;
        }
        else {
            XToast.error(this,"账户与密码不能为空",Toast.LENGTH_SHORT).show();
            userEV.setText("");
            psEV.setText("");
            return false;
        }
    }

    //忘记密码的操作
    @OnClick(R.id.forgetPs)
    void onForgetPs(){
        Intent intent = new Intent(LoginActivity.this,ForgetPWActivity.class);
        intent.putExtra("user",userEV.getText()!=null?userEV.getText().toString():"");
        startActivityForResult(intent,LOGIN_2_FORG);
    }
    //密码是否可见
    @OnClick(R.id.ps_visible)
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
    //跳转注册界面
    @OnClick(R.id.registerPs)
    void onRegisterPs(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivityForResult(intent,LOGIN_2_REGI);
            }
        },500);
    }

    private void requestPermissions() {
        //权限申请
        PermissionX.init(this)
                .permissions(permissionsList)
                .onExplainRequestReason(new ExplainReasonCallback() {
                    @Override
                    public void onExplainReason(ExplainScope scope, List<String> deniedList) {
                        String text = "软件需要以下权限才能正常使用：";
                        myRationaleDialog dialog = new myRationaleDialog(LoginActivity.this,text,deniedList);
                        scope.showRequestReasonDialog(dialog);
                    }
                })
                .onForwardToSettings(new ForwardToSettingsCallback() {
                    @Override
                    public void onForwardToSettings(ForwardScope scope, List<String> deniedList) {
                        String text = "您需要去设置中手动开启以下权限";
                        myRationaleDialog dialog = new myRationaleDialog(LoginActivity.this,text,deniedList);
                        scope.showForwardToSettingsDialog(dialog);
                    }
                })
                .request(new RequestCallback() {
                    @Override
                    public void onResult(boolean allGranted, List<String> grantedList, List<String> deniedList) {
                        if(allGranted)
                            isAllPermissionCheked = true;
                        else{
                            isAllPermissionCheked = false;
                        }

                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //注册界面带回来的信息直接进行填充我们的用户名与密码，然后进行登录
        if(data==null){
            return;
        }
        if(resultCode == 0 && requestCode == LOGIN_2_REGI){
            ArrayList<String> arrayList = data.getStringArrayListExtra("register");
            if(arrayList != null){
                userEV.setText(arrayList.get(0));
                psEV.setText(arrayList.get(1));
                Selection.setSelection(psEV.getText(),psEV.getText().length());   //设置光标的位置
            }
        }
        else if(resultCode ==0 && requestCode == LOGIN_2_FORG){
            ArrayList<String> arrayList = data.getStringArrayListExtra("forgetpw");
            if(arrayList != null){
                userEV.setText(arrayList.get(0));
                psEV.setText(arrayList.get(1));
                Selection.setSelection(psEV.getText(),psEV.getText().length());   //设置光标的位置
            }
        }
    }
    /*
    保存用户密码、账号等基本信息；
     */

    private void saveUserInfos() {
        String userName = "";
        String psName = "";
        if(isSavePS){
            userName = userEV.getText().toString().trim();
            psName = psEV.getText().toString().trim();
        }
        //进行加密，然后进行保存
        String userEncode = Md5Utils.encodeDES("43D107LM",userName);
        String psEncode = Md5Utils.encodeDES("43D107LM",psName);
        //保存
        ShareUtils.setString(this,"username",userEncode);
        ShareUtils.setString(this, "password",psEncode);
    }

    /*
    此方法用于保存了密码操作的用户直接获取 用户信息等基本的信息
     */
    private void checkUserInfos() {
        String userEncode = ShareUtils.getString(this,"username","");
        String psEncode = ShareUtils.getString(this,"password","");
        if(!userEncode.isEmpty() && !psEncode.isEmpty()){
            String userName = Md5Utils.decodeDES("43D107LM",userEncode);
            String password = Md5Utils.decodeDES("43D107LM",psEncode);
            userEV.setText(userName);
            psEV.setText(password);
            if(!userName.isEmpty() && !password.isEmpty()){
                repsCheck.setChecked(true);
            }
        }
    }

    @OnClick(R.id.image1)
    void headClick(){

    }

    @Override
    protected void onResume() {
        super.onResume();
        /*
        netReceiver = new NetReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(netReceiver,intentFilter);*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    class NetReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if("android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction().toString())){
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                @SuppressLint("MissingPermission")
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if(networkInfo!=null && networkInfo.isAvailable()){
                    XToast.info(LoginActivity.this,"网络数据可以使用").show();
                }
            }
        }
    }
}