package com.example.jack.myapplication.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.jack.myapplication.Model.TwoTuple;
import com.example.jack.myapplication.R;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

/**
 * 计划购买的头部Adapter
 */
public class PlanTopAdapter extends
        RecyclerView.Adapter<PlanTopAdapter.ViewHolder>   {
    private List<TwoTuple<String,String>> mDatas;   //描述信息
    private LayoutInflater mInflater;
    private OnItemClickLitener mOnItemClickLitener;

    //这个是图片的Listener
    public interface OnItemClickLitener
    {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener)
    {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }


    public PlanTopAdapter(Context context, List<TwoTuple<String,String>> datats)
    {
        mInflater = LayoutInflater.from(context);
        mDatas = datats;
    }


    class ViewHolder extends RecyclerView.ViewHolder
    {
        public ViewHolder(View view)
        {
            super(view);
            draweeView = (SimpleDraweeView)view.findViewById(R.id.plan_image1);
            mTxt = (TextView) view.findViewById(R.id.plan_image1_desc);
        }


        SimpleDraweeView draweeView;
        TextView mTxt;
    }

    /**
     * 创建ViewHolder
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {
        View view = mInflater.inflate(R.layout.plan_item1,
                viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }
    /**
     * 设置值
     */
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i)
    {
        TwoTuple<String,String> item = mDatas.get(i);
        viewHolder.draweeView.setImageURI(item.first);
        viewHolder.mTxt.setText(item.second);
        // 绑定点击事件
        if (mOnItemClickLitener != null) {
            //先设置图片的点击事件
            viewHolder.draweeView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    int pos = viewHolder.getLayoutPosition();
                    mOnItemClickLitener.onItemClick(viewHolder.draweeView, pos);   //在这里去调用外部实现的方法
                }
            });


        }
    }

    @Override
    public int getItemCount()
    {
        return mDatas.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
