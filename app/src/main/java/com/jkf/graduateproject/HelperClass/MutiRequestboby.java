package com.jkf.graduateproject.HelperClass;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

public class MutiRequestboby extends RequestBody {
    private MultipartBody multipartBody;
    private ProgressListener listener;
    private long totalLength;
    private long mCurrentLength =0;

    public MutiRequestboby(MultipartBody multipartBody, ProgressListener listener) {
        this.multipartBody = multipartBody;
        this.listener = listener;
    }

    @Nullable
    @Override
    public MediaType contentType() {
        return multipartBody.contentType();
    }

    @Override
    public long contentLength() throws IOException {
        return multipartBody.contentLength();
    }

    @Override
    public void writeTo(@NotNull BufferedSink bufferedSink) throws IOException {
        if(totalLength==0L){
            totalLength = contentLength();
        }
        //ForWardingSink为抽象方法，所以咱们继承然后重写其方法
        MyForwardingSink myForwardingSink = new MyForwardingSink(bufferedSink);
        BufferedSink sink = Okio.buffer(myForwardingSink);
        multipartBody.writeTo(sink);
        // 刷新
        sink.flush();
    }

    private class MyForwardingSink extends ForwardingSink{

        public MyForwardingSink(@NotNull Sink delegate) {
            super(delegate);
        }

        @Override
        public void write(@NotNull Buffer source, long byteCount) throws IOException {
            mCurrentLength += byteCount;

            if (listener != null) {
                listener.onProgressChanged((int)(mCurrentLength*100/totalLength), mCurrentLength==totalLength);
            }
            super.write(source, byteCount);
        }
    }
}
