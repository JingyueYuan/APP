package com.jkf.graduateproject.adapter;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.jkf.graduateproject.R;
import com.xuexiang.xui.adapter.recyclerview.BaseRecyclerAdapter;
import com.xuexiang.xui.adapter.recyclerview.RecyclerViewHolder;
import com.xuexiang.xui.widget.banner.recycler.BannerLayout;
import com.xuexiang.xui.widget.imageview.ImageLoader;
import com.xuexiang.xui.widget.imageview.strategy.DiskCacheStrategyEnum;

import java.util.Arrays;

/**
 * @author xuexiang
 * @since 2019-05-30 00:50
 */
public class RecyclerViewBannerAdapter extends BaseRecyclerAdapter<Integer> {

    /**
     * 默认加载图片
     */
    private ColorDrawable mColorDrawable;

    /**
     * 是否允许进行缓存
     */
    private boolean mEnableCache = true;

    private BannerLayout.OnBannerItemClickListener mOnBannerItemClickListener;


    public RecyclerViewBannerAdapter() {
        super();
        mColorDrawable = new ColorDrawable(Color.parseColor("#555555"));
    }
    public RecyclerViewBannerAdapter(Integer [] list){
        super(Arrays.asList(list));
        mColorDrawable = new ColorDrawable(Color.parseColor("#555555"));
    }

    /**
     * 适配的布局
     *
     * @param viewType
     * @return
     */
    @Override
    public int getItemLayoutId(int viewType) {
        return R.layout.adapter_record_banner_item;
    }

    /**
     * 绑定数据
     *
     * @param holder
     * @param position
     * @param imgUrl
     */
    @Override
    public void bindData(@NonNull RecyclerViewHolder holder, final int position, Integer imgUrl) {
        ImageView imageView = holder.findViewById(R.id.iv_item);

        if (imgUrl!=null) {
            ImageLoader.get().loadImage(imageView, imgUrl, mColorDrawable,
                    mEnableCache ? DiskCacheStrategyEnum.RESOURCE : DiskCacheStrategyEnum.NONE);
        } else {
            imageView.setImageDrawable(mColorDrawable);
        }

        imageView.setOnClickListener(v -> {
            if (mOnBannerItemClickListener != null) {
                mOnBannerItemClickListener.onItemClick(position);
            }
        });
    }

    /**
     * 设置是否允许缓存
     *
     * @param enableCache
     * @return
     */
    public RecyclerViewBannerAdapter enableCache(boolean enableCache) {
        mEnableCache = enableCache;
        return this;
    }

    /**
     * 获取是否允许缓存
     *
     * @return
     */
    public boolean getEnableCache() {
        return mEnableCache;
    }

    public ColorDrawable getColorDrawable() {
        return mColorDrawable;
    }

    public RecyclerViewBannerAdapter setColorDrawable(ColorDrawable colorDrawable) {
        mColorDrawable = colorDrawable;
        return this;
    }

    public RecyclerViewBannerAdapter setOnBannerItemClickListener(BannerLayout.OnBannerItemClickListener onBannerItemClickListener) {
        mOnBannerItemClickListener = onBannerItemClickListener;
        return this;
    }
}
