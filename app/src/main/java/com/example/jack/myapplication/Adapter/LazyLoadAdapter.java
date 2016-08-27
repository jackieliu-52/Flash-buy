package com.example.jack.myapplication.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.jack.myapplication.Model.Item;
import com.example.jack.myapplication.R;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

public class LazyLoadAdapter extends
        RecyclerView.Adapter<LazyLoadAdapter.MyViewHolder>
{
    private List<Item> mItems;  //准备购买的商品
    private LayoutInflater mInflater;
    private Context mContext;
    private OnItemClickLitener mOnItemClickLitener;



    //这个是图片的Listener
    public interface OnItemClickLitener
    {
        void onItemClick(View view, int position);

        void onItemPlantoBuy(View view, int position);
    }



    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener)
    {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }


    /**
     * 传了context的构造函数
     * @param context
     * @param datas
     */
    public LazyLoadAdapter(Context context, List<Item> datas)
    {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mItems = datas;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        MyViewHolder holder = new MyViewHolder(mInflater.inflate(
                R.layout.plan_item, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position)
    {


        Item item = mItems.get(position);

        holder.plan_item_pic.setImageURI(item.getImage());

        holder.plan_storage.setText(
                (int)(1+Math.random()*(1000-1+1))+"库存");
        holder.plan_price.setText("￥"+item.getPrice());

        holder.plan_item_name.setText(item.getName());


        // 绑定点击事件
        if (mOnItemClickLitener != null)
        {
            //先设置图片的点击事件
            holder.plan_item_pic.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickLitener.onItemClick(holder.plan_item_pic, pos);   //在这里去调用想实现的方法
                }
            });


            //在设置计划购买的图片点击事件
            holder.plan_buy.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    int pos = holder.getLayoutPosition();
                    holder.plan_buy.setImageResource(R.drawable.ic_plan_to_buy);
                    mOnItemClickLitener.onItemPlantoBuy(holder.plan_buy, pos);   //在这里去调用想实现的方法

                }
            });
        }
    }

    @Override
    public int getItemCount()
    {
        return mItems.size();
    }
    @Override
    public long getItemId(int position) {
        return position;
    }


    public void removeData(int position)
    {
        mItems.remove(position);
        notifyItemRemoved(position);
    }

    /**
     * ViewHolder
     */
    class MyViewHolder extends ViewHolder
    {

        ImageButton plan_buy;
        TextView plan_storage;
        TextView plan_price;
        TextView plan_item_name;
        SimpleDraweeView plan_item_pic;

        public MyViewHolder(View view)
        {
            super(view);
            plan_item_pic = (SimpleDraweeView)view.findViewById(R.id.plan_item_pic);
            plan_buy = (ImageButton) view.findViewById(R.id.plan_buy);
            plan_storage = (TextView) view.findViewById(R.id.plan_storage);
            plan_price = (TextView) view.findViewById(R.id.plan_price);
            plan_item_name = (TextView) view.findViewById(R.id.plan_item_name);
        }
    }
}
