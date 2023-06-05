package com.jkf.graduateproject.utils;

import android.app.Activity;

import androidx.fragment.app.Fragment;

import com.jkf.graduateproject.GlideEngine;
import com.jkf.graduateproject.R;
import com.jkf.graduateproject.application.myApp;
import com.luck.picture.lib.PictureSelectionModel;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.engine.ImageEngine;

public class PictureUtils {

    //==========图片选择===========//

    /**
     * 获取图片选择的配置
     *
     * @param fragment
     * @return
     */
    public static PictureSelectionModel getPictureSelector(Fragment fragment) {
        return PictureSelector.create(fragment)
                .openGallery(PictureMimeType.ofImage())
                .theme(R.style.picture_WeChat_style)
                .imageEngine(GlideEngine.createGlideEngine())
                .maxSelectNum(8)
                .minSelectNum(1)
                .filterMinFileSize(40)
                .selectionMode(PictureConfig.SINGLE)
                .isMaxSelectEnabledMask(true)
                .imageSpanCount(4)
                .isReturnEmpty(true)
                .isPreviewImage(true)
                .isCamera(true)
                .isEnableCrop(true)
                .isGif(false)
                .freeStyleCropEnabled(true)
                .circleDimmedLayer(false)
                .isDragFrame(true);
    }

}
