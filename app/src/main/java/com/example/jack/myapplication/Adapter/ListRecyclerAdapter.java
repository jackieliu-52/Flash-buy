package com.example.jack.myapplication.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.jack.myapplication.Model.Item;
import com.example.jack.myapplication.Model.LineItem;
import com.example.jack.myapplication.R;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

/**
 * 订单简略商品的Adapter
 * Created by Jack on 2016/9/3.
 */
public class ListRecyclerAdapter extends RecyclerView.Adapter<ListRecyclerAdapter.ViewHolder> {
    private List<LineItem> mItems;
    private LayoutInflater mInflater;

    /**
     * 传了context的构造函数
     * @param context
     * @param datas
     */
    public ListRecyclerAdapter(Context context, List<LineItem> datas)
    {
        mInflater = LayoutInflater.from(context);
        mItems = datas;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        ViewHolder holder = new ViewHolder(mInflater.inflate(
                R.layout.list_recycler_item, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        LineItem item = mItems.get(position);
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

    class ViewHolder extends RecyclerView.ViewHolder {

        private SimpleDraweeView mSimpleDraweeView;
        private TextView mTextView;


        public ViewHolder(View view){
            super(view);
            mSimpleDraweeView = (SimpleDraweeView)view.findViewById(R.id.id_index_gallery_item_image);
            mTextView = (TextView)view.findViewById(R.id.id_index_gallery_item_text);
        }

        public void bindView(LineItem item){
            mSimpleDraweeView.setImageURI(item.getItem().getImage());
            mTextView.setText(item.getItem().getName());
        }
    }
}
