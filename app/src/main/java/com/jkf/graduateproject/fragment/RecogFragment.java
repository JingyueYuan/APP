package com.jkf.graduateproject.fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.jkf.graduateproject.HelperClass.ProgressListener;
import com.jkf.graduateproject.HelperClass.ProgressResponse;
import com.jkf.graduateproject.ImageViewInfo;
import com.jkf.graduateproject.R;
import com.jkf.graduateproject.SingleInfom;
import com.jkf.graduateproject.activity.DetailRecordActivity;
import com.jkf.graduateproject.activity.MainActivity;
import com.jkf.graduateproject.application.myApp;
import com.jkf.graduateproject.myViews.myInputDialog;
import com.jkf.graduateproject.utils.FileUtil;
import com.jkf.graduateproject.utils.LoadingDialog;
import com.jkf.graduateproject.utils.PictureUtils;
import com.luck.picture.lib.PictureSelectorExternalUtils;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnResultCallbackListener;
import com.xuexiang.xui.widget.button.ButtonView;
import com.xuexiang.xui.widget.dialog.materialdialog.GravityEnum;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.edittext.MultiLineEditText;
import com.xuexiang.xui.widget.imageview.RadiusImageView;
import com.xuexiang.xui.widget.imageview.preview.PreviewBuilder;
import com.xuexiang.xui.widget.imageview.preview.view.SmoothImageView;
import com.xuexiang.xui.widget.layout.ExpandableLayout;
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView;
import com.xuexiang.xui.widget.toast.XToast;
import com.yalantis.ucrop.util.FileUtils;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.luck.picture.lib.thread.PictureThreadUtils.runOnUiThread;

public class RecogFragment extends Fragment {
    //
    private String originalPath;
    private String oldPath = "";   //
    private Bitmap recognizedBitmap;   //
    private String recognedPath;

    //
    private Long timeStamp = 0L;
    //
    private static final int ALBUM = 147;
    private final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
    private  MaterialDialog dialog ;
    private MainActivity activity;
    //
    List<ImageViewInfo> tempList = new ArrayList<>();  //
    List<ImageViewInfo> list = new ArrayList<>();   //

    //
    @BindView(R.id.detailScrool) NestedScrollView detailScrool;
    //
    @BindView(R.id.kindTV) SuperTextView kindTV;

    //
    @BindView(R.id.startTV) SuperTextView originalTV;
    @BindView(R.id.startLayout) ExpandableLayout originalLayout;
    @BindView(R.id.startImage) RadiusImageView originalImage;
    //
    @BindView(R.id.endTV) SuperTextView recognizedTV;
    @BindView(R.id.endLayout) ExpandableLayout recognizedLayout;
    @BindView(R.id.endImage) RadiusImageView recognizedImage;
    //
    @BindView(R.id.resultTV)SuperTextView resultTV;                //
    @BindView(R.id.resultLayout) ExpandableLayout resultLayout;    //
    @BindView(R.id.resultArea)SuperTextView resultArea;            //
    @BindView(R.id.resultLength)SuperTextView resultLength;       //
    @BindView(R.id.resultMenWidth)SuperTextView resultMenWidth;   //
    @BindView(R.id.resultRealWidth)SuperTextView resultRealWidth;   //
    @BindView(R.id.resultTime)SuperTextView resultTime;   //
    //
    @BindView(R.id.resultRemark)SuperTextView resultRemark;
    @BindView(R.id.remarkLayout)ExpandableLayout remarkLayout;
    @BindView(R.id.resultTextview) TextView resultTextview;  //

    //
    @BindView(R.id.saveTV) SuperTextView saveTV;

    public static RecogFragment newInstance(String name){
        RecogFragment fragment = new RecogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("text",name);
        fragment.setArguments(bundle);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recog_fragment,container,false);
        ButterKnife.bind(this, view);
        //
        init_dialog();
        init_view();
        return view;
    }

    private void init_view() {
        //
        originalLayout.setOnExpansionChangedListener(new ExpandableLayout.OnExpansionChangedListener() {
            @Override
            public void onExpansionChanged(float expansion, int state) {
                if(originalTV!=null && originalTV.getRightIconIV()!=null){
                    originalTV.getRightIconIV().setRotation(expansion*90);
                }
            }
        });
        //
        originalTV.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClick(SuperTextView superTextView) {
                if(originalLayout!=null){
                    originalLayout.toggle();
                }
            }
        });
        //
        originalTV.setRightTvClickListener(new SuperTextView.OnRightTvClickListener() {
            @Override
            public void onClick(TextView textView) {
                //
                if(ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)!=
                        PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            12);
                }else{
                    openAlbum();
                }
            }
        });
        //
        //
        originalImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PreviewBuilder.from(activity)
                        .setImgs(tempList)
                        .setCurrentIndex(0)
                        .setProgressColor( R.color.xui_config_color_main_theme)
                        .setType(PreviewBuilder.IndicatorType.Dot)
                        .start();//
            }
        });
        //
        recognizedLayout.setOnExpansionChangedListener(new ExpandableLayout.OnExpansionChangedListener() {
            @Override
            public void onExpansionChanged(float expansion, int state) {
                if(recognizedTV!=null && recognizedTV.getRightIconIV()!=null){
                    recognizedTV.getRightIconIV().setRotation(expansion*90);
                }
            }
        });
        //
        recognizedTV.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClick(SuperTextView superTextView) {
                if(originalLayout!=null){
                    recognizedLayout.toggle();
                }
            }
        });
        //
        recognizedTV.setRightTvClickListener(new SuperTextView.OnRightTvClickListener() {
            @Override
            public void onClick(TextView textView) {
                if(originalPath!=null && !originalPath.isEmpty()){
                    File file = new File(originalPath);
                    upLoadImage(file);
                }
            }
        });
        //
        recognizedImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PreviewBuilder.from(activity)
                        .setImgs(list)
                        .setCurrentIndex(0)
                        .setProgressColor( R.color.xui_config_color_main_theme)
                        .setType(PreviewBuilder.IndicatorType.Dot)
                        .start();//
            }
        });
        //
        kindTV.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClick(SuperTextView superTextView) {
                kindTV.setSwitchIsChecked(!kindTV.getSwitchIsChecked());
                kindTV.setSwitchCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if(b){
                            superTextView.setLeftString("On water");
                        }else{
                            superTextView.setLeftString("Under water");
                        }
                    }
                });
            }
        });

        //
        resultTV.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClick(SuperTextView superTextView) {
                if(resultLayout!=null){
                    resultLayout.toggle();
                }
            }
        });
        //
        resultLayout.setOnExpansionChangedListener(new ExpandableLayout.OnExpansionChangedListener() {
            @Override
            public void onExpansionChanged(float expansion, int state) {
                if(resultTV!=null && resultTV.getRightIconIV()!=null){
                    resultTV.getRightIconIV().setRotation(expansion*90);
                }
            }
        });
        //
        resultRemark.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClick(SuperTextView superTextView) {
                remarkLayout.toggle();
            }
        });
        //
        resultRemark.setRightTvClickListener(new SuperTextView.OnRightTvClickListener() {
            @Override
            public void onClick(TextView textView) {
                myInputDialog myDialog = new myInputDialog(activity, R.style.dialog, new myInputDialog.OnCloseListener() {
                    @Override
                    public void onClickCancel(Dialog dialog, boolean confirm) {
                        dialog.dismiss();
                    }

                    @Override
                    public void onClickSubmit(Dialog dialog, String confirm) {
                        resultTextview.setText(confirm);
                        dialog.dismiss();
                    }
                });
                myDialog.setContent(resultTextview.getText().toString().trim()).show();
            }
        });
        //
        remarkLayout.setOnExpansionChangedListener(new ExpandableLayout.OnExpansionChangedListener() {
            @Override
            public void onExpansionChanged(float expansion, int state) {
                if(resultRemark!=null && resultRemark.getRightIconIV()!=null){
                    resultRemark.getRightIconIV().setRotation(expansion*90);
                }
            }
        });
        //
        saveTV.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClick(SuperTextView superTextView) {
                //
                if(originalPath!=null && !originalPath.isEmpty() && recognizedBitmap!=null){
                    if(originalPath.equals(oldPath)){
                        XToast.error(activity,"Please do not duplicate the save.").show();
                    }else{
                        //
                        FileUtil.creatSonDir(timeStamp+"");
                        String [] array = originalPath.split("/");
                        String newOriginalPath = FileUtil.SDPATH+"/"+myApp.getInstance().getUserName()+"/"+timeStamp+"/"+ array[array.length-1];
                        String newCrackPath = FileUtil.SDPATH+"/"+myApp.getInstance().getUserName()+"/"+timeStamp+"/"+timeStamp+".png";
                        String textPath = FileUtil.SDPATH+"/"+myApp.getInstance().getUserName()+"/"+timeStamp+"/"+timeStamp+".txt";
                        SingleInfom singleInfo = new SingleInfom();
                        singleInfo.setKind(kindTV.getLeftString());  //
                        singleInfo.setCrakOriginalPath(newOriginalPath);  //
                        singleInfo.setCrackPath(newCrackPath);   //
                        singleInfo.setCrackArea(resultArea.getRightString());
                        singleInfo.setCrackTime(resultTime.getRightString());
                        singleInfo.setCrackLength(resultLength.getRightString());
                        singleInfo.setCrack_meanWidth(resultMenWidth.getRightString());
                        singleInfo.setCrack_realWidth(resultRealWidth.getRightString());
                        singleInfo.setDescription(resultTextview.getText().toString());
                        FileUtil.saveTxtFile(singleInfo,textPath);
                        //
                        new Thread(new Runnable(){
                            @Override
                            public void run() {
                                try {
                                    FileUtil.copyFile(originalPath,newOriginalPath);
                                    FileUtil.copyFile(recognedPath,newCrackPath);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                        oldPath = originalPath;
                        XToast.info(activity,"Information is being saved").show();
                    }

                }
                else{
                    XToast.error(activity,"Please check if you recognize").show();
                }
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (MainActivity)context;
    }

    /*

     */
    private void openAlbum() {
        /*
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent,ALBUM);*/

        PictureUtils.getPictureSelector(this).forResult(new OnResultCallbackListener<LocalMedia>() {
            @Override
            public void onResult(List <LocalMedia> result) {
                for (LocalMedia media : result) {
                    Log.i("XML", "Whether to compress." + media.isCompressed());
                    Log.i("XML", "Compression." + media.getCompressPath());
                    Log.i("XML", "Original image." + media.getPath());
                    Log.i("XML", "Whether to crop." + media.isCut());
                    Log.i("XML", "Crop." + media.getCutPath());
                    Log.i("XML", "Whether to turn on the original image." + media.isOriginal());
                    Log.i("XML", "Original image path." + media.getOriginalPath());
                    Log.i("XML", "Android Q unique Path:" + media.getAndroidQToPath());
                    ExifInterface exchange = PictureSelectorExternalUtils.getExifInterface(activity,media.getAndroidQToPath());
                    Log.i("XML","Precision"+exchange.getAttribute(ExifInterface.TAG_GPS_LATITUDE));
                    Log.i("XML","Dimensions"+exchange.getAttribute(ExifInterface.TAG_GPS_LONGITUDE));
                    Log.i("XML","Height:"+exchange.getAttribute(ExifInterface.TAG_IMAGE_LENGTH));

                    int aa =25;
                }
                LocalMedia media = result.get(0);
                String path;
                if (media.isCut() && !media.isCompressed()) {
                    //
                    path = media.getCutPath();
                } else if (media.isCompressed() || (media.isCut() && media.isCompressed())) {
                    //
                    path = media.getCompressPath();
                } else {
                    //
                    path = media.getPath();
                }
                //
                originalPath = path;
                FileUtil.loadImageToIm(activity,path,originalImage);
                originalLayout.toggle();
                //
                if(tempList.size()!=0){
                    tempList.clear();
                }
                tempList.add(new ImageViewInfo(originalPath));
            }

            @Override
            public void onCancel() {

            }
        });
    }

    /*

     */

    private void upLoadImage(File file) {
        //String url = "http://172.26.28.79:5000/airImage";
        LoadingDialog.getInstance(activity).loadingDialogShow();
        String url;
        if(kindTV.getLeftString().equals("On water")){
            url = "http://"+myApp.getInstance().getIpHost()+":5000/airImage";
        }
        else{
            url = "http://"+myApp.getInstance().getIpHost()+":5000/underwaterImage";
        }
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(5000, TimeUnit.MILLISECONDS)      //
                .readTimeout(60000, TimeUnit.MILLISECONDS)         //
                .writeTimeout(60000, TimeUnit.MILLISECONDS)        //
                .retryOnConnectionFailure(true)            //
                .build();
        MultipartBody.Builder mBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("fileName",file.getName())
                .addFormDataPart("files","img_"+System.currentTimeMillis()+".jpg",
                        RequestBody.Companion.create(file,MEDIA_TYPE_PNG));
        Request request = new Request.Builder().url(url).post(mBuilder.build()).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        XToast.error(activity,"Upload failed").show();
                        LoadingDialog.getInstance(activity).loadingDialogOff();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Log.e("XML", new Date().getTime()+"");
                LoadingDialog.getInstance(activity).loadingDialogOff();
                InputStream inputStream  = response.body().byteStream();
                recognizedBitmap = BitmapFactory.decodeStream(inputStream);
                timeStamp = System.currentTimeMillis();
                //
                if(recognizedBitmap!=null){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            recognedPath = FileUtil.saveBitmap(recognizedBitmap);
                            if(list.size()!=0){
                                list.clear();
                            }
                            list.add(new ImageViewInfo(recognedPath));
                        }
                    }).start();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            recognizedImage.setImageBitmap(recognizedBitmap);
                            if(!recognizedLayout.isExpanded()){
                                recognizedLayout.toggle();
                            }
                            if(!resultLayout.isExpanded()){
                                resultLayout.toggle();
                            }
                            resultArea.setRightString(response.header("area","0"));
                            resultLength.setRightString(response.header("length","0"));
                            resultMenWidth.setRightString(response.header("mean_width","0"));
                            resultRealWidth.setRightString(response.header("real_width","0"));
                            SimpleDateFormat dateFormat_24=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            resultTime.setRightString(dateFormat_24.format(timeStamp));
                            remarkLayout.toggle();
                        }
                    });
                }
            }
        });
    }




    public void onClick(View view) {
        dialog.show();
        //String url ="http://172.26.28.79:5000/airImage";
        String url = "http://"+myApp.getInstance().getIpHost()+":5000/airImage";
        OkHttpClient.Builder okClient = new OkHttpClient.Builder();
        //增加插值器
        OkHttpClient client = okClient.addNetworkInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response response = chain.proceed(chain.request());
                return response.newBuilder().body(new ProgressResponse(response.body(), new ProgressListener() {
                    @Override
                    public void onProgressChanged(int progress, boolean isDone) {
                        updateProgress(progress, isDone);
                    }
                })).build();
            }
        }).build();

        FormBody.Builder builder = new FormBody.Builder();
        Request request = new Request.Builder().url(url).post(builder.build()).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                dialog.dismiss();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new MaterialDialog.Builder(activity)
                                .iconRes(R.drawable.icon_tip)
                                .title(R.string.dialog_tips)
                                .content(R.string.content_simple_confirm_dialog)
                                .positiveText(R.string.lab_submit)
                                .show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                Log.e("XML", new Date().getTime()+"");
                InputStream inputStream  = response.body().byteStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                Log.e("XML",response.message().toString());
                Log.e("XML",response.code()+"");
                Log.e("XML",response.headers()+"");
                Log.e("XML",response.header("length","0"));
                Log.e("XML",response.header("Content-Length"));

            }
        });
    }

    private void init_dialog() {
        MaterialDialog.Builder dialogBuilder = new MaterialDialog.Builder(activity)
                .title(R.string.dialog_tips)
                .content(R.string.content_downloading)
                .contentGravity(GravityEnum.CENTER)
                .progress(false, 100, true)
                .cancelable(false);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog = dialogBuilder.build();
            }
        });

    }

    private void updateProgress(int progress, boolean isDone){
        if(dialog != null){
            if(isDone){
                dialog.dismiss();
            }else{
                dialog.setProgress(progress);
            }
        }
    }

    /*

       */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 12 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            openAlbum();
        }
        else {
            XToast.error(activity,"You need to get storage permission to upload images.").show();
        }
    }
}
