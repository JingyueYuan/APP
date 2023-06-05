package com.jkf.graduateproject.utils;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.jkf.graduateproject.Enum.respCode;
import com.jkf.graduateproject.HelperClass.okhttpListener;
import com.jkf.graduateproject.R;
import com.jkf.graduateproject.activity.LoginActivity;
import com.jkf.graduateproject.activity.MainActivity;
import com.jkf.graduateproject.application.myApp;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.toast.XToast;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.luck.picture.lib.thread.PictureThreadUtils.runOnUiThread;

public class okhttpUtil {
    private static MediaType mediaType = MediaType.parse("image/png");
    private static okhttpUtil httputils;
    private static OkHttpClient client;

    public static okhttpUtil getInstance() {
        if(httputils==null){
            httputils = new okhttpUtil();
        }
        return httputils;
    }

    public okhttpUtil() {
        client = new OkHttpClient.Builder()
                .connectTimeout(500, TimeUnit.MILLISECONDS)      //设置连接超时
                .readTimeout(500, TimeUnit.MILLISECONDS)         //设置读超时
                .writeTimeout(500, TimeUnit.MILLISECONDS)        //设置写超时
                .retryOnConnectionFailure(true)            //是否自动重连
                .build();
    }

    //登录与注册上传少量的key-value对象，便于我们进行登录、注册
    public static void postMultyBoby(String url, File file,okhttpListener listener){
        setImageLoadClient();
        MultipartBody.Builder mBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("fileName",file.getName())
                .addFormDataPart("files","img_"+System.currentTimeMillis()+".jpg",
                        RequestBody.Companion.create(file,mediaType));
        Request request = new Request.Builder().url(url).post(mBuilder.build()).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
               listener.onFailure(call,e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Log.e("XML", new Date().getTime()+"");
                listener.onResponse(call,response);
            }
        });
    }
    public static void postForms(String url, HashMap<String,String> map,okhttpListener listener){
        FormBody.Builder builder = new FormBody.Builder();
        for(String key1 : map.keySet()){
            builder.add(key1, Objects.requireNonNull(map.get(key1)));
        }
        Request request = new Request.Builder().url(url).post(builder.build()).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                listener.onFailure(call,e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                listener.onResponse(call,response);
            }
        });
    }
    /*
    此方法只要针对于图片上传，设置的时间较长
     */
    public  static void setImageLoadClient(){
        okhttpUtil.client = new OkHttpClient.Builder()
                .connectTimeout(10000, TimeUnit.MILLISECONDS)      //设置连接超时
                .readTimeout(30000, TimeUnit.MILLISECONDS)         //设置读超时
                .writeTimeout(30000, TimeUnit.MILLISECONDS)        //设置写超时
                .retryOnConnectionFailure(true)            //是否自动重连
                .build();
    }

    /*
   此方法只要针对于图片上传，设置的时间较段
    */
    public  void setFormsLoadClient(){
        okhttpUtil.client = new OkHttpClient.Builder()
                .connectTimeout(1000, TimeUnit.MILLISECONDS)      //设置连接超时
                .readTimeout(1000, TimeUnit.MILLISECONDS)         //设置读超时
                .writeTimeout(1000, TimeUnit.MILLISECONDS)        //设置写超时
                .retryOnConnectionFailure(true)            //是否自动重连
                .build();
    }
}
