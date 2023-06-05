package com.jkf.graduateproject.HelperClass;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

public class ProgressResponse extends ResponseBody {
    private ResponseBody responseBody;
    private ProgressListener progressListener;
    private BufferedSource bufferedSource;

    public ProgressResponse(ResponseBody responseBody, ProgressListener progressListener) {
        this.responseBody = responseBody;
        this.progressListener = progressListener;
    }

    @Override
    public long contentLength() {
        return responseBody.contentLength();
    }

    @Nullable
    @Override
    public MediaType contentType() {
        return responseBody.contentType();
    }

    @NotNull
    @Override
    public BufferedSource source() {
        if(bufferedSource==null){
            bufferedSource = Okio.buffer(Mysource(responseBody.source()));
        }
        return bufferedSource;
    }

    private Source Mysource(BufferedSource source) {
        return new ForwardingSource(source) {
            long totalRead = 0L;
            @Override
            public long read(@NotNull Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink,byteCount);
                totalRead += bytesRead!=-1 ? bytesRead:0;
                //DecimalFormat dateFormat = new DecimalFormat("#.0");

                progressListener.onProgressChanged((int)(totalRead*100/contentLength()),totalRead==contentLength());
                return bytesRead;
            }
        };
    }
}
