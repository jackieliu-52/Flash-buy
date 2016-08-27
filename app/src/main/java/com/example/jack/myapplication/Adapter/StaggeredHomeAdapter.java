package com.example.jack.myapplication.Adapter;

import java.util.List;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jack.myapplication.Model.Item;
import com.example.jack.myapplication.R;
import com.example.jack.myapplication.Util.Event.MessageEvent;
import com.example.jack.myapplication.Util.Util;
import com.facebook.common.util.UriUtil;
import com.facebook.drawee.view.SimpleDraweeView;

import org.greenrobot.eventbus.EventBus;

public class StaggeredHomeAdapter extends
        RecyclerView.Adapter<StaggeredHomeAdapter.MyViewHolder>
{

    public static final String ACTION_LIKE_BUTTON_CLICKED = "action_like_button_button";
    public static final String ACTION_LIKE_IMAGE_CLICKED = "action_like_image_button";

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

        void onCommentClick(View view, int pos);

        void onLikeClick(View view,int pos);
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
        holder.bindView(item);

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
    public class MyViewHolder extends ViewHolder
    {

        public ImageButton ib_star;
        TextView tv_title;
        TextView tv_price;
        TextView tv_discount;
        SimpleDraweeView draweeView;

        FrameLayout vImageRoot;
        ImageButton btnComments;
        public View vBgLike;
        public ImageView ivLike;

        public MyViewHolder(View view)
        {
            super(view);
            draweeView = (SimpleDraweeView)view.findViewById(R.id.item_pic);
            ib_star = (ImageButton) view.findViewById(R.id.item_star);
            tv_title = (TextView) view.findViewById(R.id.item_title);
            tv_price = (TextView) view.findViewById(R.id.item_price);
            tv_discount = (TextView) view.findViewById(R.id.item_discount);

            vImageRoot = (FrameLayout) view.findViewById(R.id.vImageRoot);
            btnComments  = (ImageButton) view.findViewById(R.id.btnComments);
            vBgLike = (View) view.findViewById(R.id.vBgLike);
            ivLike = (ImageView) view.findViewById(R.id.ivLike);

        }



        public void bindView(Item item){
            int resId = Util.stringToId(mContext,item.getImage());
            Uri uri = new Uri.Builder()
                    .scheme(UriUtil.LOCAL_RESOURCE_SCHEME) // "res"
                    .path(String.valueOf(resId))
                    .build();
            draweeView.setImageURI(uri);
            //是否喜欢该商品
            ib_star.setImageResource(item.isStar() ? R.mipmap.ic_heart_red : R.mipmap.ic_heart_outline_grey);

            tv_title.setText(item.getName());
            tv_price.setText("￥"+item.getPrice() );
            if(item.getDiscount() <= 5)
                tv_discount.setTextColor(mContext.getResources().getColor(R.color.md_red_700));
            tv_discount.setText(item.getDiscount() + "折");

            // 绑定点击事件
            if (mOnItemClickLitener != null)
            {
                //先设置图片的点击事件
                draweeView.setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(View v) {
                        int pos = getLayoutPosition();

                        mOnItemClickLitener.onItemClick(draweeView, pos);   //在这里去调用想实现的方法
                    }
                });

                //收藏
                draweeView.setOnLongClickListener(new OnLongClickListener()
                {
                    @Override
                    public boolean onLongClick(View v) {
                        int pos = getLayoutPosition();
                        notifyItemChanged(pos, ACTION_LIKE_IMAGE_CLICKED);
                        EventBus.getDefault().post(new MessageEvent("liked"));
                        mOnItemClickLitener.onItemLongClick(draweeView, pos);  //在这里去调用想实现的方法
                        return false;
                    }
                });

                //在设置喜欢按钮的图片点击事件
                ib_star.setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(View v) {
                        int pos = getLayoutPosition();
                        Item temp = mItems.get(pos);  //获得点击的Item
                        temp.setStar(!temp.isStar());  //反过来设置
                        notifyItemChanged(pos, ACTION_LIKE_BUTTON_CLICKED);
                        //是否喜欢该商品
                        ib_star.setImageResource(temp.isStar() ? R.mipmap.ic_heart_red : R.mipmap.ic_heart_outline_grey);
                        mOnItemClickLitener.onLikeClick(ib_star, pos);  //在这里去调用想实现的方法
                    }
                });

                btnComments.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = getLayoutPosition();
                        mOnItemClickLitener.onCommentClick(btnComments, pos);  //在这里去调用想实现的方法
                    }
                });

            }//if
        }
    }
}
