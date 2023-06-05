package com.jkf.graduateproject.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.jkf.graduateproject.GlideEngine;
import com.jkf.graduateproject.ImageViewInfo;
import com.jkf.graduateproject.R;
import com.jkf.graduateproject.SingleInfom;
import com.jkf.graduateproject.utils.FileUtil;
import com.luck.picture.lib.PictureSelector;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.imageview.RadiusImageView;
import com.xuexiang.xui.widget.imageview.preview.PreviewBuilder;
import com.xuexiang.xui.widget.layout.ExpandableLayout;
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailRecordActivity extends AppCompatActivity {
    //
    @BindView(R.id.detailToolbar) TitleBar detailToolbar;
    //
    @BindView(R.id.originalTV) SuperTextView originalTV;
    @BindView(R.id.originalLayout) ExpandableLayout originalLayout;
    @BindView(R.id.originalImage) RadiusImageView originalImage;
    //
    @BindView(R.id.recognizedTV) SuperTextView recognizedTV;
    @BindView(R.id.recognizedLayout) ExpandableLayout recognizedLayout;
    @BindView(R.id.recognizedImage) RadiusImageView recognizedImage;

    //
    @BindView(R.id.crack_kind) SuperTextView crack_kind;  //
    @BindView(R.id.crack_area) SuperTextView crack_area;  //
    @BindView(R.id.crack_length) SuperTextView crack_length;  //
    @BindView(R.id.crack_meanWidth) SuperTextView crack_meanWidth;  //
    @BindView(R.id.crack_realWidth) SuperTextView crack_realWidth;  //
    @BindView(R.id.crack_maxWidth) SuperTextView crack_maxWidth;  //
    @BindView(R.id.crack_time) SuperTextView crack_time;  //

    //
    @BindView(R.id.detailRemark) SuperTextView detailRemark;
    @BindView(R.id.detailLayout) ExpandableLayout detailLayout;
    @BindView(R.id.detailTextview) TextView detailTextview;

    private SingleInfom singleInfom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_record);
        ButterKnife.bind(this);
        if(getIntent().getSerializableExtra("item")!=null){
            singleInfom = (SingleInfom)getIntent().getSerializableExtra("item");
        }
        //
        init_view();
    }

    private void init_view() {
        //
        detailToolbar.setLeftClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                finish();
            }
        });
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
        List<ImageViewInfo> list1 = new ArrayList<>();
        list1.add(new ImageViewInfo(singleInfom.getCrakOriginalPath()));
        originalImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PreviewBuilder.from(DetailRecordActivity.this)
                        .setImgs(list1)
                        .setCurrentIndex(0)
                        .setProgressColor( R.color.xui_config_color_main_theme)
                        .setType(PreviewBuilder.IndicatorType.Dot)
                        .start();//启动
            }
        });
        FileUtil.loadImageToIm(this,singleInfom.getCrakOriginalPath(),originalImage);

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
        List<ImageViewInfo> list = new ArrayList<>();
        list.add(new ImageViewInfo(singleInfom.getCrackPath()));
        recognizedImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PreviewBuilder.from(DetailRecordActivity.this)
                        .setImgs(list)
                        .setCurrentIndex(0)
                        .setProgressColor( R.color.xui_config_color_main_theme)
                        .setType(PreviewBuilder.IndicatorType.Dot)
                        .start();//
            }
        });
        FileUtil.loadImageToIm(this,singleInfom.getCrackPath(),recognizedImage);
        //
        crack_time.setRightString(singleInfom.getCrackTime());
        crack_area.setRightString(singleInfom.getCrackArea());
        crack_length.setRightString(singleInfom.getCrackLength());
        crack_meanWidth.setRightString(singleInfom.getCrack_meanWidth());
        crack_realWidth.setRightString(singleInfom.getCrack_realWidth());
        crack_kind.setRightString(singleInfom.getKind());
        //
        detailRemark.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClick(SuperTextView superTextView) {
                if(detailLayout!=null){
                    detailLayout.toggle();
                }
            }
        });
        //
        //
        detailLayout.setOnExpansionChangedListener(new ExpandableLayout.OnExpansionChangedListener() {
            @Override
            public void onExpansionChanged(float expansion, int state) {
                if(detailRemark!=null && detailRemark.getRightIconIV()!=null){
                    detailRemark.getRightIconIV().setRotation(expansion*90);
                }
            }
        });
        //
        detailTextview.setText(singleInfom.getDescription());
    }
}