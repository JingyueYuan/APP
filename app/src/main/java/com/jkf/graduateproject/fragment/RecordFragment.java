package com.jkf.graduateproject.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;

import com.jkf.graduateproject.R;
import com.jkf.graduateproject.SingleInfom;
import com.jkf.graduateproject.activity.DetailRecordActivity;
import com.jkf.graduateproject.activity.MainActivity;
import com.jkf.graduateproject.adapter.RecViewShowAdapter;
import com.jkf.graduateproject.adapter.RecyclerViewBannerAdapter;
import com.jkf.graduateproject.myViews.myBannerLayout;
import com.jkf.graduateproject.utils.FileUtil;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.xuexiang.xui.adapter.recyclerview.DividerItemDecoration;
import com.xuexiang.xui.adapter.recyclerview.RecyclerViewHolder;
import com.xuexiang.xui.adapter.recyclerview.XLinearLayoutManager;
import com.xuexiang.xui.widget.layout.linkage.LinkageScrollLayout;
import com.xuexiang.xui.widget.layout.linkage.view.LinkageLinearLayout;
import com.xuexiang.xui.widget.layout.linkage.view.LinkageRecyclerView;
import com.xuexiang.xui.widget.toast.XToast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static androidx.recyclerview.widget.OrientationHelper.VERTICAL;

public class RecordFragment extends Fragment {

    @BindView(R.id.bl_horizontal) myBannerLayout bl_horizontal; //轮播图
    @BindView(R.id.recyclerView)  LinkageRecyclerView recyclerView; //展示用的RecycView
    @BindView(R.id.lsl_container) LinkageScrollLayout lslContainer;  //包含主要内容的 -- 内联布局
    @BindView(R.id.refreshLayout) SmartRefreshLayout refreshLayout;  //刷新的布局

    private MainActivity activity;

    public static Integer [] imagePath = new Integer[]{R.drawable.ht_5,R.drawable.ht_6,R.drawable.ht_3,R.drawable.ht_4};
    private static List<SingleInfom> infomList = new ArrayList<>();
    private RecViewShowAdapter recViewShowAdapter;

    public static RecordFragment newInstance(String title){
        RecordFragment fragment = new RecordFragment();
        Bundle bundle = new Bundle();
        bundle.putString("text",title);
        fragment.setArguments(bundle);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.record_fragment,container, false);
        String title = getArguments().getString("text","Default value");
        ButterKnife.bind(this,view);
        initView();
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        //initInfomList();
        activity = (MainActivity)context;
    }

    private void initView() {
        //轮播图设置适配器
        bl_horizontal.setAdapter(new RecyclerViewBannerAdapter(imagePath));
        //下拉刷新
        recyclerView.setAdapter(recViewShowAdapter = new RecViewShowAdapter());
        recyclerView.setLayoutManager(new XLinearLayoutManager(recyclerView.getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), VERTICAL, 0));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        refreshLayout.setOnRefreshListener(refreshLayout -> refreshLayout.getLayout().postDelayed(() -> {
            recViewShowAdapter.loadMore(getData1());
            refreshLayout.finishRefresh();
        }, 1000));
        refreshLayout.setOnLoadMoreListener(refreshLayout -> refreshLayout.getLayout().postDelayed(() -> {
            recViewShowAdapter.loadMore(getData1());

            refreshLayout.finishLoadMore();

        }, 1000));

        refreshLayout.autoRefresh();//第一次进入触发自动刷新，演示效果

        //设置详情界面，点击之后跳转详情界面
        recViewShowAdapter.setOnItemClickListener(new RecyclerViewHolder.OnItemClickListener<SingleInfom>() {
            @Override
            public void onItemClick(View itemView, SingleInfom item, int position) {
                XToast.info(activity,"Clicked"+position).show();
                Intent intent = new Intent(activity, DetailRecordActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("item",item);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    private List<SingleInfom> getData() {
        List<SingleInfom> list = new ArrayList<>();
        //List<SingleInfom> list = initInfomList();
        return list;
    }
    private List<SingleInfom> getData1() {
        List<SingleInfom> list = FileUtil.loadDataFromDir(recViewShowAdapter.getItemCount(),4);
        return list;
    }

}
