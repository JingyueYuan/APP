package com.jkf.graduateproject.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.jkf.graduateproject.R;
import com.jkf.graduateproject.HelperClass.ProgressListener;
import com.jkf.graduateproject.HelperClass.ProgressResponse;
import com.jkf.graduateproject.adapter.MyPagerAdapter;
import com.jkf.graduateproject.fragment.RecogFragment;
import com.jkf.graduateproject.fragment.RecordFragment;
import com.jkf.graduateproject.fragment.UserFragment;
import com.xuexiang.xui.widget.actionbar.TitleBar;

import com.xuexiang.xui.widget.toast.XToast;


import java.util.ArrayList;

import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.util.TypedValue;
import android.graphics.Typeface;
public class MainActivity extends AppCompatActivity  {

    @BindView(R.id.titlebar) TitleBar titleBar;
    //用于记录返回的点击时间
    private long lastBackTime = 0L;
    @BindView(R.id.viewPager2) ViewPager2 mViewPager;
    @BindView(R.id.mainLayout)
    LinearLayout mainLayout;
    @BindView(R.id.tabbar) TabLayout mTabbar;
    private MyPagerAdapter pagerAdapter;


    public static final String[] mTitles = {"Record", "Identification","Settings"};
    public static final int[] drawableList ={R.drawable.tab_icon1,R.drawable.tab_icon2,R.drawable.tab_icon3};

    private List<Fragment> mPageMap = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        init_view();
        //初始化tabbar
        init_tab();
        Log.i("XML","我不走cReate");

    }

    private void init_view() {
        RecordFragment recordFragment = RecordFragment.newInstance(mTitles[0]);
        mPageMap.add(recordFragment);
        RecogFragment fragmentFragment = RecogFragment.newInstance(mTitles[1]);
        mPageMap.add(fragmentFragment);
        UserFragment userFragment = UserFragment.newInstance(mTitles[2]);
        mPageMap.add(userFragment);
        mViewPager.setOffscreenPageLimit(3);
        pagerAdapter = new MyPagerAdapter(this,mPageMap);
        mViewPager.setAdapter(pagerAdapter);
    }
    /*
    tab与viewPager2的绑定操作
     */
    private void init_tab() {
        //页面可以滑动,二者联动
        new TabLayoutMediator(mTabbar, mViewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(mTitles[position]);
                tab.setIcon(drawableList[position]);

            }
        }).attach();

    }



    /*多次点击返回
     */

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            if(System.currentTimeMillis()-lastBackTime>2000){
                XToast.warning(this,"再次点击退出此软件").show();
                lastBackTime = System.currentTimeMillis();
                return false;
            }
            else{
                System.exit(0);
            }


        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("XML","我直接复活");
    }

    public ViewPager2 getLayout() {
        return mViewPager;
    }
}