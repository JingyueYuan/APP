package com.jkf.graduateproject.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetUtils {
    public static boolean checkNet(Context ctx){
        boolean isMobileConnected = checkMobil(ctx);
        boolean isWifiConnected = checkWifi(ctx);
        if(isMobileConnected || isWifiConnected){
            return true;
        }
        else {
            return false;
        }
    }

    private static boolean checkMobil(Context ctx) {
        ConnectivityManager connectivityManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if(networkInfo != null && networkInfo.isConnected()){
            return true;
        }
        else {
            return false;
        }
    }

    private static boolean checkWifi(Context ctx){
        ConnectivityManager connectivityManager = (ConnectivityManager)ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if(networkInfo != null && networkInfo.isConnected()){
            return true;
        }
        else{
            return false;
        }
    }
}
