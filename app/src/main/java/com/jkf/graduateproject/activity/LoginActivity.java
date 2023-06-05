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
    @BindView(R.id.userEV) EditText userEV;  //
    @BindView(R.id.user_delet) ImageView user_delet;  //
    @BindView(R.id.ps_visible) ImageView ps_visible;   //
    @BindView(R.id.psEV) EditText psEV;   //
    @BindView(R.id.ps_delet) ImageView ps_delet;   //
    @BindView(R.id.repsCheck) CheckBox repsCheck;   //
    @BindView(R.id.submit) Button submit;   //
    @BindView(R.id.forgetPs) TextView forgetPs;   //
    @BindView(R.id.registerPs) TextView registerPs;   //

    private boolean isSavePS = false;
    private boolean isNetConnected = false;

    //
    private static int LOGIN_2_REGI = 10;
    private static int LOGIN_2_FORG = 20;

    //
    private MaterialDialog dialog;


    //
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
        //
        requestPermissions();
        init_dialog();
        //
        init_view();
        judgeNetConnected();
        //
        checkUserInfos();
    }

    private void init_dialog() {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(this)
                .title("Tips")
                .limitIconToDefaultSize()
                .content(R.string.wait_for_login)
                .progress(true, 0)
                .progressIndeterminateStyle(false);
        dialog = builder.build();
    }

    /*

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
        psEV.setInputType(129);
    }
    //
    @OnCheckedChanged(R.id.repsCheck)
    void onOnCheckedChanged(boolean isChecked){
        if(isChecked){
            isSavePS = isChecked;
        }
        else{
            isSavePS = isChecked;
        }
    }
    //
    @OnClick(R.id.user_delet)
    void onClick(){
        userEV.setText("");
    }
    //
    @OnClick(R.id.ps_delet)
    void onClickPS_delet(){
        psEV.setText("");
    }
    //
    @OnClick(R.id.submit)
    void onSubmitClick(){
        //
        if(!NetUtils.checkNet(LoginActivity.this)){
            XToast.error(this,"The current network is not available, please ensure that the network has been opened",Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isCanClickable = judgeUserNamePassWord();
        if(isCanClickable){
            //
            judgeInServer();
        }
        else{
            return;
        }
    }
    /*

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
                        //iphost
                        new MaterialDialog.Builder(LoginActivity.this)
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

                                .positiveText("OK")
                                .negativeText("Cancel")
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
                    XToast.success(LoginActivity.this,"Coming soon").show();
                    //
                    saveUserInfos();
                    //
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
                    XToast.info(LoginActivity.this,"Incorrect username or password").show();
                }else if(respCode.LOGINUNRE.getCode().equals(str)){
                    XToast.error(LoginActivity.this,"This account has not yet been registered, please do so").show();
                }else{
                    XToast.info(LoginActivity.this,"An unknown error occurred, please exit and re-enter.").show();
                }
                Looper.loop();
            }
        });
    }

    //
    private boolean judgeUserNamePassWord() {

        if(userEV.getText().length()!=0 && psEV.getText().length()!=0){
            return true;
        }
        else {
            XToast.error(this,"Account and password cannot be empty",Toast.LENGTH_SHORT).show();
            userEV.setText("");
            psEV.setText("");
            return false;
        }
    }

    //
    @OnClick(R.id.forgetPs)
    void onForgetPs(){
        Intent intent = new Intent(LoginActivity.this,ForgetPWActivity.class);
        intent.putExtra("user",userEV.getText()!=null?userEV.getText().toString():"");
        startActivityForResult(intent,LOGIN_2_FORG);
    }
    //
    @OnClick(R.id.ps_visible)
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
        //
        PermissionX.init(this)
                .permissions(permissionsList)
                .onExplainRequestReason(new ExplainReasonCallback() {
                    @Override
                    public void onExplainReason(ExplainScope scope, List<String> deniedList) {
                        String text = "The software requires the following permissions to function properly:";
                        myRationaleDialog dialog = new myRationaleDialog(LoginActivity.this,text,deniedList);
                        scope.showRequestReasonDialog(dialog);
                    }
                })
                .onForwardToSettings(new ForwardToSettingsCallback() {
                    @Override
                    public void onForwardToSettings(ForwardScope scope, List<String> deniedList) {
                        String text = "You need to go to the settings to manually enable the following permissions";
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
        //
        if(data==null){
            return;
        }
        if(resultCode == 0 && requestCode == LOGIN_2_REGI){
            ArrayList<String> arrayList = data.getStringArrayListExtra("register");
            if(arrayList != null){
                userEV.setText(arrayList.get(0));
                psEV.setText(arrayList.get(1));
                Selection.setSelection(psEV.getText(),psEV.getText().length());   //
            }
        }
        else if(resultCode ==0 && requestCode == LOGIN_2_FORG){
            ArrayList<String> arrayList = data.getStringArrayListExtra("forgetpw");
            if(arrayList != null){
                userEV.setText(arrayList.get(0));
                psEV.setText(arrayList.get(1));
                Selection.setSelection(psEV.getText(),psEV.getText().length());   //
            }
        }
    }
    /*

     */

    private void saveUserInfos() {
        String userName = "";
        String psName = "";
        if(isSavePS){
            userName = userEV.getText().toString().trim();
            psName = psEV.getText().toString().trim();
        }
        //
        String userEncode = Md5Utils.encodeDES("43D107LM",userName);
        String psEncode = Md5Utils.encodeDES("43D107LM",psName);
        //
        ShareUtils.setString(this,"username",userEncode);
        ShareUtils.setString(this, "password",psEncode);
    }

    /*

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
                    XToast.info(LoginActivity.this,"Network data is available using").show();
                }
            }
        }
    }
}