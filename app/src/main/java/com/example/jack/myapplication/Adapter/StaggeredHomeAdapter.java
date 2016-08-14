package com.example.jack.myapplication.Adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.jack.myapplication.Model.Item;
import com.example.jack.myapplication.R;
import com.example.jack.myapplication.Util.Util;
import com.facebook.common.util.UriUtil;
import com.facebook.drawee.view.SimpleDraweeView;

public class StaggeredHomeAdapter extends
        RecyclerView.Adapter<StaggeredHomeAdapter.MyViewHolder>
{
    private List<Item> mItems;  //促销商品
    private List<String> mDatas;
    private LayoutInflater mInflater;
    private Context mContext;
    private OnItemClickLitener mOnItemClickLitener;



    //这个是图片的Listener
    public interface OnItemClickLitener
    {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
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
    public StaggeredHomeAdapter(Context context, List<Item> datas)
    {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mItems = datas;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        MyViewHolder holder = new MyViewHolder(mInflater.inflate(
                R.layout.item_goods, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position)
    {


        Item item = mItems.get(position);
        int resId = Util.stringToId(mContext,item.getImage());
        Uri uri = new Uri.Builder()
                .scheme(UriUtil.LOCAL_RESOURCE_SCHEME) // "res"
                .path(String.valueOf(resId))
                .build();

      //  Uri uri = Uri.parse("res://" + mContext.getPackageName()+"/R.drawable." + item.getImage());
//        Log.i("wrong",uri.toString());
//        holder.ib_pic.setImageResource(ResId);
//        holder.draweeView.setImageResource(ResId);
        holder.draweeView.setImageURI(uri);
        //是否喜欢该商品
        if(item.isStar())
            holder.ib_star.setImageResource(R.drawable.ic_favorite);
        else
            holder.ib_star.setImageResource(R.drawable.ic_not_interested);

        holder.tv_title.setText(item.getName());
        holder.tv_price.setText(item.getPrice() + "");
        if(item.getDiscount() <= 5)
            holder.tv_discount.setTextColor(mContext.getResources().getColor(R.color.md_red_700));
        holder.tv_discount.setText(item.getDiscount() + "折");

        // 绑定点击事件
        if (mOnItemClickLitener != null)
        {
            //先设置图片的点击事件
            holder.draweeView.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickLitener.onItemClick(holder.draweeView, pos);   //在这里去调用想实现的方法
                }
            });

            //长点击删除Item
            holder.draweeView.setOnLongClickListener(new OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View v)
                {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickLitener.onItemLongClick(holder.draweeView, pos);  //在这里去调用想实现的方法
                    //应该加入购物车
                    removeData(pos);
                    return false;
                }
            });

            //在设置喜欢按钮的图片点击事件
            holder.ib_star.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    int pos = holder.getLayoutPosition();
                    Item temp = mItems.get(pos);  //获得点击的Item
                    temp.setStar(!temp.isStar());  //反过来设置
                    //是否喜欢该商品
                    if(temp.isStar())
                        holder.ib_star.setImageResource(R.drawable.ic_favorite);
                    else
                        holder.ib_star.setImageResource(R.drawable.ic_not_interested);
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
            // TODO Auto-generated method stub
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

        ImageButton ib_star;
        TextView tv_title;
        TextView tv_price;
        TextView tv_discount;
        SimpleDraweeView draweeView;

        public MyViewHolder(View view)
        {
            super(view);
            draweeView = (SimpleDraweeView)view.findViewById(R.id.item_pic);

            ib_star = (ImageButton) view.findViewById(R.id.item_star);
            tv_title = (TextView) view.findViewById(R.id.item_title);
            tv_price = (TextView) view.findViewById(R.id.item_price);
            tv_discount = (TextView) view.findViewById(R.id.item_discount);
        }
    }
}
