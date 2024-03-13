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
    //当前正加载展示的图片的路径
    private String originalPath;
    private String oldPath = "";   //防止重复识别
    private Bitmap recognizedBitmap;   //识别之后下载的位图
    private String recognedPath;

    //识别结束的时间  最后图片的名字
    private Long timeStamp = 0L;
    //上传的记录
    private static final int ALBUM = 147;
    private final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
    private  MaterialDialog dialog ;
    private MainActivity activity;
    //预览图的展示
    List<ImageViewInfo> tempList = new ArrayList<>();  //原图
    List<ImageViewInfo> list = new ArrayList<>();   //裂缝图

    //总的布局
    @BindView(R.id.detailScrool) NestedScrollView detailScrool;
    //类别选择的按钮 ,水下 or 水上，默认为水上
    @BindView(R.id.kindTV) SuperTextView kindTV;

    //原图控件展示
    @BindView(R.id.startTV) SuperTextView originalTV;
    @BindView(R.id.startLayout) ExpandableLayout originalLayout;
    @BindView(R.id.startImage) RadiusImageView originalImage;
    //识别之后的图像
    @BindView(R.id.endTV) SuperTextView recognizedTV;
    @BindView(R.id.endLayout) ExpandableLayout recognizedLayout;
    @BindView(R.id.endImage) RadiusImageView recognizedImage;
    //识别加过保存参数
    @BindView(R.id.resultTV)SuperTextView resultTV;                //结果展示的总界面
    @BindView(R.id.resultLayout) ExpandableLayout resultLayout;    //结果扩展布局
    @BindView(R.id.resultArea)SuperTextView resultArea;            //面积
    @BindView(R.id.resultLength)SuperTextView resultLength;       //长度
    @BindView(R.id.resultMenWidth)SuperTextView resultMenWidth;   //平均宽度
    @BindView(R.id.resultRealWidth)SuperTextView resultRealWidth;   //实际宽度
    @BindView(R.id.resultTime)SuperTextView resultTime;   //识别时间
    //裂缝描述相关
    @BindView(R.id.resultRemark)SuperTextView resultRemark;
    @BindView(R.id.remarkLayout)ExpandableLayout remarkLayout;
    @BindView(R.id.resultTextview) TextView resultTextview;  //直接用tv进行展示即可

    //保存按钮
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
        //初始化dialog
        init_dialog();
        init_view();
        return view;
    }

    private void init_view() {
        //原图 -- 右侧的指示箭头发生变化,expandlayput展示
        originalLayout.setOnExpansionChangedListener(new ExpandableLayout.OnExpansionChangedListener() {
            @Override
            public void onExpansionChanged(float expansion, int state) {
                if(originalTV!=null && originalTV.getRightIconIV()!=null){
                    originalTV.getRightIconIV().setRotation(expansion*90);
                }
            }
        });
        //如果点击了此文字，则下方展示图片，expandlayout进行打开
        originalTV.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClick(SuperTextView superTextView) {
                if(originalLayout!=null){
                    originalLayout.toggle();
                }
            }
        });
        //原图中 导入图片 点击事件
        originalTV.setRightTvClickListener(new SuperTextView.OnRightTvClickListener() {
            @Override
            public void onClick(TextView textView) {
                //首先检查权限
                if(ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)!=
                        PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            12);
                }else{
                    openAlbum();
                }
            }
        });
        //原图的预览
        //点击进行大图的展示
        originalImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PreviewBuilder.from(activity)
                        .setImgs(tempList)
                        .setCurrentIndex(0)
                        .setProgressColor( R.color.xui_config_color_main_theme)
                        .setType(PreviewBuilder.IndicatorType.Dot)
                        .start();//启动
            }
        });
        //识别之后的图像 --- 右侧的指示箭头发生变化,expandlayput展示
        recognizedLayout.setOnExpansionChangedListener(new ExpandableLayout.OnExpansionChangedListener() {
            @Override
            public void onExpansionChanged(float expansion, int state) {
                if(recognizedTV!=null && recognizedTV.getRightIconIV()!=null){
                    recognizedTV.getRightIconIV().setRotation(expansion*90);
                }
            }
        });
        //如果点击了此文字，则下方展示图片，expandlayout进行打开
        recognizedTV.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClick(SuperTextView superTextView) {
                if(originalLayout!=null){
                    recognizedLayout.toggle();
                }
            }
        });
        //识别 中 点击上传  进行原图图片的识别回调
        recognizedTV.setRightTvClickListener(new SuperTextView.OnRightTvClickListener() {
            @Override
            public void onClick(TextView textView) {
                if(originalPath!=null && !originalPath.isEmpty()){
                    File file = new File(originalPath);
                    upLoadImage(file);
                }
            }
        });
        // 裂缝识别图预览图
        recognizedImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PreviewBuilder.from(activity)
                        .setImgs(list)
                        .setCurrentIndex(0)
                        .setProgressColor( R.color.xui_config_color_main_theme)
                        .setType(PreviewBuilder.IndicatorType.Dot)
                        .start();//启动
            }
        });
        //类别设设置监听,点击时 设置为相反的结果就行
        kindTV.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClick(SuperTextView superTextView) {
                kindTV.setSwitchIsChecked(!kindTV.getSwitchIsChecked());
                kindTV.setSwitchCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if(b){
                            superTextView.setLeftString("In-air");
                        }else{
                            superTextView.setLeftString("Underwater");
                        }
                    }
                });
            }
        });

        //识别结果界面的展示
        resultTV.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClick(SuperTextView superTextView) {
                if(resultLayout!=null){
                    resultLayout.toggle();
                }
            }
        });
        //识别之后的图像 --- 右侧的指示箭头发生变化,expandlayput展示
        resultLayout.setOnExpansionChangedListener(new ExpandableLayout.OnExpansionChangedListener() {
            @Override
            public void onExpansionChanged(float expansion, int state) {
                if(resultTV!=null && resultTV.getRightIconIV()!=null){
                    resultTV.getRightIconIV().setRotation(expansion*90);
                }
            }
        });
        //裂缝描述界面的展开
        resultRemark.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClick(SuperTextView superTextView) {
                remarkLayout.toggle();
            }
        });
        //增加裂缝描述  点击事件
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
        //识别之后的图像 --- 右侧的指示箭头发生变化,expandlayput展示
        remarkLayout.setOnExpansionChangedListener(new ExpandableLayout.OnExpansionChangedListener() {
            @Override
            public void onExpansionChanged(float expansion, int state) {
                if(resultRemark!=null && resultRemark.getRightIconIV()!=null){
                    resultRemark.getRightIconIV().setRotation(expansion*90);
                }
            }
        });
        //保存 按钮 点击进行保存
        saveTV.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClick(SuperTextView superTextView) {
                //原图与识别的结果都已经存在,主要就是 原图路径、回传图片不为空就可以了
                if(originalPath!=null && !originalPath.isEmpty() && recognizedBitmap!=null){
                    if(originalPath.equals(oldPath)){
                        XToast.error(activity,"Do not save repeatedly").show();
                    }else{
                        //保存excel文件
                        FileUtil.creatSonDir(timeStamp+"");
                        String [] array = originalPath.split("/");
                        String newOriginalPath = FileUtil.SDPATH+"/"+myApp.getInstance().getUserName()+"/"+timeStamp+"/"+ array[array.length-1];
                        String newCrackPath = FileUtil.SDPATH+"/"+myApp.getInstance().getUserName()+"/"+timeStamp+"/"+timeStamp+".png";
                        String textPath = FileUtil.SDPATH+"/"+myApp.getInstance().getUserName()+"/"+timeStamp+"/"+timeStamp+".txt";
                        SingleInfom singleInfo = new SingleInfom();
                        singleInfo.setKind(kindTV.getLeftString());  //种类水上与水下
                        singleInfo.setCrakOriginalPath(newOriginalPath);  //原图路径
                        singleInfo.setCrackPath(newCrackPath);   //处理之后的图片的路劲
                        singleInfo.setCrackArea(resultArea.getRightString());
                        singleInfo.setCrackTime(resultTime.getRightString());
                        singleInfo.setCrackLength(resultLength.getRightString());
                        singleInfo.setCrack_meanWidth(resultMenWidth.getRightString());
                        singleInfo.setCrack_realWidth(resultRealWidth.getRightString());
                        singleInfo.setDescription(resultTextview.getText().toString());
                        FileUtil.saveTxtFile(singleInfo,textPath);
                        //进行图片的复制移动操作
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
                        XToast.info(activity,"Saving").show();
                    }

                }
                else{
                    XToast.error(activity,"Please confirm identification").show();
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
    打开相册,去进行选择，得到我们需要展示的图片path
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
                    Log.i("XML", "是否压缩:" + media.isCompressed());
                    Log.i("XML", "压缩:" + media.getCompressPath());
                    Log.i("XML", "原图:" + media.getPath());
                    Log.i("XML", "是否裁剪:" + media.isCut());
                    Log.i("XML", "裁剪:" + media.getCutPath());
                    Log.i("XML", "是否开启原图:" + media.isOriginal());
                    Log.i("XML", "原图路径:" + media.getOriginalPath());
                    Log.i("XML", "Android Q 特有Path:" + media.getAndroidQToPath());
                    ExifInterface exchange = PictureSelectorExternalUtils.getExifInterface(activity,media.getAndroidQToPath());
                    Log.i("XML","精度："+exchange.getAttribute(ExifInterface.TAG_GPS_LATITUDE));
                    Log.i("XML","维度："+exchange.getAttribute(ExifInterface.TAG_GPS_LONGITUDE));
                    Log.i("XML","高度："+exchange.getAttribute(ExifInterface.TAG_IMAGE_LENGTH));

                    int aa =25;
                }
                LocalMedia media = result.get(0);
                String path;
                if (media.isCut() && !media.isCompressed()) {
                    // 裁剪过
                    path = media.getCutPath();
                } else if (media.isCompressed() || (media.isCut() && media.isCompressed())) {
                    // 压缩过,或者裁剪同时压缩过,以最终压缩过图片为准
                    path = media.getCompressPath();
                } else {
                    // 原图
                    path = media.getPath();
                }
                //获取到裁剪的图片，保留其路径，后面要进行文件的复制读取
                originalPath = path;
                FileUtil.loadImageToIm(activity,path,originalImage);
                originalLayout.toggle();
                //数据的加载
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
    上传文件
     */

    private void upLoadImage(File file) {
        //String url = "http://172.26.28.79:5000/airImage";
        LoadingDialog.getInstance(activity).loadingDialogShow();
        String url;
        if(kindTV.getLeftString().equals("In-air")){
            url = "http://"+myApp.getInstance().getIpHost()+":5000/airImage";
        }
        else{
            url = "http://"+myApp.getInstance().getIpHost()+":5000/underwaterImage";
        }
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(5000, TimeUnit.MILLISECONDS)      //设置连接超时
                .readTimeout(60000, TimeUnit.MILLISECONDS)         //设置读超时
                .writeTimeout(60000, TimeUnit.MILLISECONDS)        //设置写超时
                .retryOnConnectionFailure(true)            //是否自动重连
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
                //开启一个线程进行图片的保存
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
      权限申请的回调
       */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 12 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            openAlbum();
        }
        else {
            XToast.error(activity,"上传图片需要获得存储权限").show();
        }
    }
}
