package com.jkf.graduateproject.HelperClass;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

public abstract class okhttpListener {
    public abstract void onFailure(Call call,Exception e);
    public abstract void onResponse(Call call, Response response) throws IOException;
}
