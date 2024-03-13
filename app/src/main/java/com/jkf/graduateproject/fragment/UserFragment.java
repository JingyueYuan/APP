package com.jkf.graduateproject.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.jkf.graduateproject.HelperClass.okhttpListener;
import com.jkf.graduateproject.R;
import com.jkf.graduateproject.activity.LoginActivity;
import com.jkf.graduateproject.activity.MainActivity;
import com.jkf.graduateproject.application.myApp;
import com.jkf.graduateproject.utils.okhttpUtil;
import com.xuexiang.xui.widget.dialog.materialdialog.DialogAction;
import com.xuexiang.xui.widget.dialog.materialdialog.GravityEnum;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView;
import com.xuexiang.xui.widget.toast.XToast;

import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

public class UserFragment extends Fragment {
    private MainActivity activity;
    @BindView(R.id.aboutSetting) SuperTextView aboutSetting;
    @BindView(R.id.sugestSetting) SuperTextView sugestSetting;
    @BindView(R.id.userId) SuperTextView userId;

    public static UserFragment newInstance(String text){
        UserFragment fragment = new UserFragment();
        Bundle bundle = new Bundle();
        bundle.putString("text",text);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (MainActivity)context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_fragment, container,false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }
    //控件参数设置
    private void initView() {
        userId.setLeftString(myApp.getInstance().getUserName());
    }
    @OnClick(R.id.userId)
    void onUserClick(){
        new MaterialDialog.Builder(activity)
                .iconRes(R.drawable.icon_tip)
                .title("Log out")
                .content(R.string.exitInforms)
                .positiveText(R.string.lab_submit)
                .negativeText(R.string.lab_cancel)
                .onPositive((dialog, which) -> {
                    Intent intent = new Intent(activity, LoginActivity.class);
                    activity.startActivity(intent);
                    activity.finish();
                })
                .onNegative(((dialog, which) -> {

                }))
                .cancelable(false)
                .show();
    }

    //关于软件
    @OnClick(R.id.aboutSetting)
    void onClick() {
        new MaterialDialog.Builder(activity)
                .iconRes(R.drawable.icon_tip)
                .title("About the APP")
                .content(R.string.aboutSoft)
                .positiveText(R.string.lab_submit)
                .cancelable(false)
                .show();
    }

    //意见反馈，此处可以输入任何信息，便于联系沟通
    @OnClick(R.id.sugestSetting)
    void onSuggetsClick(){
        MaterialDialog dialog = new MaterialDialog.Builder(activity)
                .iconRes(R.drawable.icon_tip)
                .title("Feedback")
                .content(R.string.suggetContact)
                .inputType(
                        InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                                )
                .input("Please enter your contact information",
                        "",
                        true,
                        new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {

                            }
                        })
                .inputRange(0, 25)
                .negativeColor(getResources().getColor(R.color.text_normal))
                .positiveText(R.string.lab_submit)
                .negativeText(R.string.lab_cancel)
                .onPositive(null)
                .cancelable(false)
                .show();
        dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = dialog.getInputEditText().getText().toString().trim();
                if(message.isEmpty()){
                    XToast.error(activity,"Contact information cannot be empty or cancel feedback").show();
                }
                else if(checkEmail(message) | checkMobile(message)){
                    HashMap<String,String> map = new HashMap<String,String>();
                    map.put("username",myApp.getInstance().getUserName());
                    map.put("connectinfom",message);
                    okhttpUtil.getInstance().postForms("http://172.26.87.174:5000/suggetst", map, new okhttpListener() {
                        @Override
                        public void onFailure(Call call, Exception e) {
                            Looper.prepare();
                            XToast.error(activity,"服务器异常，请稍后再试...").show();
                            Looper.loop();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if(response.code()==200){
                                Looper.prepare();
                                XToast.success(activity, "稍后作者将会与您联系...").show();
                                Looper.loop();
                            }
                        }
                    });
                    dialog.dismiss();
                }
                else {
                    XToast.error(activity,"一般以电话或邮箱作为常用联系方式...").show();
                    dialog.getInputEditText().setText("");
                }
            }
        });
    }

    public  boolean checkEmail(String email) {
        String regex = "\\w+@\\w+\\.[a-z]+(\\.[a-z]+)?";
        return Pattern.matches(regex, email);
    }
    public  boolean checkMobile(String mobile) {
        String regex = "(\\+\\d+)?1[3458]\\d{9}$";
        return Pattern.matches(regex,mobile);
    }
}
