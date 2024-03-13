package com.jkf.graduateproject.myViews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.xuexiang.xui.widget.banner.recycler.BannerLayout;

public class myBannerLayout extends BannerLayout {
    public myBannerLayout(Context context) {
        super(context);
    }

    public myBannerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public myBannerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        getParent().requestDisallowInterceptTouchEvent(true);  //设置不拦截
        return super.dispatchTouchEvent(ev);
    }
}
