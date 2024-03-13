package com.jkf.graduateproject.adapter;

import androidx.annotation.NonNull;

import com.jkf.graduateproject.R;
import com.jkf.graduateproject.SingleInfom;
import com.jkf.graduateproject.application.myApp;
import com.xuexiang.xui.adapter.recyclerview.BaseRecyclerAdapter;
import com.xuexiang.xui.adapter.recyclerview.RecyclerViewHolder;

public class RecViewShowAdapter extends BaseRecyclerAdapter<SingleInfom> {
    @Override
    protected int getItemLayoutId(int viewType) {
        return R.layout.adapter_show_record_list_item;
    }

    @Override
    protected void bindData(@NonNull RecyclerViewHolder holder, int position, SingleInfom item) {
        ///storage/emulated/0/GraduateProject/Users/123456789/1595892417220/1595892417220.png
        //在这里设置布局，控件的绑定
        if(item.getCrakOriginalPath()!=null &&  !item.getCrakOriginalPath().isEmpty()){
            holder.image(R.id.iv_image, item.getCrakOriginalPath());
        }
        if(item.getCrackPath()!=null &&  !item.getCrackPath().isEmpty()){
          //  holder.image(R.id.iv_avatar, item.getCrackPath());
        }
        holder.text(R.id.tv_user_name, myApp.getInstance().getUserName());  //用户信息
        holder.text(R.id.tv_kind, item.getKind());  //类别
        holder.text(R.id.tv_tag,item.getCrackTime());
        holder.text(R.id.tv_summary,item.getDescription());

        
    }

}
