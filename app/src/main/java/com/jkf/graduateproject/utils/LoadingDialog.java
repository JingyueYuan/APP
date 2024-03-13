package com.jkf.graduateproject.utils;

import android.content.Context;

import com.jkf.graduateproject.R;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;

public class LoadingDialog {
    private MaterialDialog dialog;
    private static LoadingDialog loadingDialog;
    public static LoadingDialog getInstance(Context context) {
        if(loadingDialog==null){
            loadingDialog = new LoadingDialog(context);
        }
        return loadingDialog;
    }

    private LoadingDialog(Context context) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
                .title("提示")
                .iconRes(R.drawable.icon_tip)
                .limitIconToDefaultSize()
                .content(R.string.wait_for_login)
                .contentColor(context.getResources().getColor(R.color.loadingDialog))
                .progress(true, 0)
                .canceledOnTouchOutside(false)
                .cancelable(false)
                .progressIndeterminateStyle(false);
        dialog = builder.build();
        dialog.getContentView().setTextSize(16);
    }
    public void loadingDialogShow(){
        dialog.show();
    }

    public void loadingDialogOff(){
        dialog.dismiss();
    }
}
