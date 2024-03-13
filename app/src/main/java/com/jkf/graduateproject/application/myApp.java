package com.jkf.graduateproject.application;

import android.app.Application;

import com.xuexiang.xui.XUI;

public class myApp extends Application {
    //全局的文件夹的名称
    private String userName;
    private static myApp myApplication;
    private String ipHost="";

    public static myApp getInstance(){
        return myApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        XUI.init(this);
        XUI.debug(true);
        myApplication = this;

    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getIpHost() {
        return ipHost;
    }

    public void setIpHost(String ipHost) {
        this.ipHost = ipHost;
    }
}
