package com.jkf.graduateproject.myViews;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jkf.graduateproject.R;
import com.permissionx.guolindev.request.RationaleDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class myRationaleDialog extends RationaleDialog {
    private Context context;
    //权限组合
    HashMap<String, String> permissionMap = new HashMap<String, String>();
    //提示信息
    private String text = "";
    private List<String> deniedList = new ArrayList();
    private Set<String> groupSet = new HashSet<>();
    public myRationaleDialog(@NonNull Context context, String text,List<String> deniedList) {
        super(context, R.style.CustomDialog);
        this.context = context;
        this.text = text;
        this.deniedList = deniedList;
    }
    //对话dialog的控件
    private TextView messageText;
    //用来展示的Linearlayout
    private LinearLayout permissionsLayout;
    private Button positiveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_dialog_layout);
        messageText = findViewById(R.id.messageText);
        messageText.setText(text);
        permissionsLayout = findViewById(R.id.permissionsLayout);
        positiveBtn = findViewById(R.id.positiveBtn);
        buildHashMap();
        buildPermissionsLayout();
        Window window = getWindow();
        if(window != null){
            int width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.8);   //4/5的宽
            int height = window.getAttributes().height;
            window.setLayout(width,height);
        }
    }
    /*
    获取权限对应的label
     */
    private void buildPermissionsLayout() {
        for (String permission : deniedList) {
            String permissionGroup = permissionMap.get(permission);
            if (permissionGroup != null && !groupSet.contains(permissionGroup)) {
                TextView textView = (TextView) LayoutInflater.from(context).inflate(R.layout.permissions_item, permissionsLayout, false);
                try {
                    String text = context.getPackageManager().getPermissionGroupInfo(permissionGroup,0).loadLabel(context.getPackageManager()).toString();
                    textView.setText(text);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                permissionsLayout.addView(textView);
                groupSet.add(permissionGroup);
            }
        }
    }

    private void buildHashMap() {
        permissionMap.put(Manifest.permission.READ_CALENDAR ,Manifest.permission_group.CALENDAR);
        permissionMap.put(Manifest.permission.WRITE_CALENDAR ,Manifest.permission_group.CALENDAR);
        permissionMap.put( Manifest.permission.READ_CALL_LOG ,Manifest.permission_group.CALL_LOG);
        permissionMap.put(Manifest.permission.WRITE_CALL_LOG ,Manifest.permission_group.CALL_LOG);
        permissionMap.put(Manifest.permission.PROCESS_OUTGOING_CALLS ,Manifest.permission_group.CALL_LOG);
        permissionMap.put(Manifest.permission.CAMERA ,Manifest.permission_group.CAMERA);
        permissionMap.put(Manifest.permission.READ_CONTACTS ,Manifest.permission_group.CONTACTS);
        permissionMap.put(Manifest.permission.WRITE_CONTACTS ,Manifest.permission_group.CONTACTS);
        permissionMap.put(Manifest.permission.GET_ACCOUNTS ,Manifest.permission_group.CONTACTS);
        permissionMap.put(Manifest.permission.ACCESS_FINE_LOCATION ,Manifest.permission_group.LOCATION);
        permissionMap.put(Manifest.permission.ACCESS_COARSE_LOCATION ,Manifest.permission_group.LOCATION);
        permissionMap.put(Manifest.permission.RECORD_AUDIO ,Manifest.permission_group.MICROPHONE);
        permissionMap.put(Manifest.permission.READ_PHONE_STATE ,Manifest.permission_group.PHONE);
        permissionMap.put(Manifest.permission.READ_PHONE_NUMBERS ,Manifest.permission_group.PHONE);
        permissionMap.put(Manifest.permission.CALL_PHONE ,Manifest.permission_group.PHONE);
        permissionMap.put(Manifest.permission.ADD_VOICEMAIL ,Manifest.permission_group.PHONE);
        permissionMap.put(Manifest.permission.USE_SIP ,Manifest.permission_group.PHONE);
        permissionMap.put(Manifest.permission.ACTIVITY_RECOGNITION ,Manifest.permission_group.ACTIVITY_RECOGNITION);
        permissionMap.put(Manifest.permission.SEND_SMS ,Manifest.permission_group.SMS);
        permissionMap.put(Manifest.permission.RECEIVE_SMS ,Manifest.permission_group.SMS);
        permissionMap.put(Manifest.permission.READ_SMS ,Manifest.permission_group.SMS);
        permissionMap.put(Manifest.permission.RECEIVE_WAP_PUSH ,Manifest.permission_group.SMS);
        permissionMap.put(Manifest.permission.RECEIVE_MMS ,Manifest.permission_group.SMS);
        permissionMap.put(Manifest.permission.READ_EXTERNAL_STORAGE ,Manifest.permission_group.STORAGE);
        permissionMap.put(Manifest.permission.WRITE_EXTERNAL_STORAGE ,Manifest.permission_group.STORAGE);
        permissionMap.put(Manifest.permission.ACCESS_MEDIA_LOCATION ,Manifest.permission_group.STORAGE);

    }

    @NonNull
    @Override
    public View getPositiveButton() {
        return positiveBtn;
    }

    @Nullable
    @Override
    public View getNegativeButton() {
        return null;
    }

    @NonNull
    @Override
    public List<String> getPermissionsToRequest() {
        return deniedList;
    }
}
