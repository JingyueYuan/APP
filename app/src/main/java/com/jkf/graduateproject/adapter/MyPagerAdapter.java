package com.jkf.graduateproject.adapter;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyPagerAdapter extends FragmentStateAdapter {
    private List<Fragment> mPageMap = new ArrayList<>();
    private Activity activity;
    public MyPagerAdapter(@NonNull FragmentActivity fragmentActivity, List<Fragment> mPageMap) {
        super(fragmentActivity);
        activity = fragmentActivity;
        this.mPageMap = mPageMap;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return mPageMap.get(position);
    }

    @Override
    public int getItemCount() {
        return mPageMap.size();
    }
}
