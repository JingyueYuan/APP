package com.jkf.graduateproject.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class ShareUtils {
    /**
     * 我们存储账户和密码的MD5的加密格式
     * username -->useranme
     * password -->password
     */

    public static final String TABLE1_NAME ="USER_INFO";
    //键 值
    public static void setString(Context mContext, String key, String value) {
        SharedPreferences sp = mContext.getSharedPreferences(TABLE1_NAME, Context.MODE_PRIVATE);
        sp.edit().putString(key, value).apply();
    }

    //键 默认值
    public static String getString(Context mContext, String key, String defValue) {
        SharedPreferences sp = mContext.getSharedPreferences(TABLE1_NAME, Context.MODE_PRIVATE);
        return sp.getString(key, defValue);
    }
}

