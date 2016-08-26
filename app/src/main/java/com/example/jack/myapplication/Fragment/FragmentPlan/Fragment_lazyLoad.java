package com.example.jack.myapplication.Fragment.FragmentPlan;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.jack.myapplication.Fragment.BaseFragment;
import com.example.jack.myapplication.Model.Item;
import com.example.jack.myapplication.R;
import com.example.jack.myapplication.View.Recyclerview.SpacesItemDecoration;
import com.facebook.drawee.view.SimpleDraweeView;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;

/**
 * 实现懒加载的Fragment
 */
public class Fragment_lazyLoad extends BaseFragment {
    RecyclerView mRecyclerView;
    List<Item> mItems;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lazy_load, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.lazy_recyclerView);
        //瀑布流设置
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,
        StaggeredGridLayoutManager.VERTICAL));

        //加入分割线
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(3));

        Load();


        return view;
    }

    protected  void Load(){
        if(getArguments() != null) {
            Bundle bundle = getArguments();
            mItems = bundle.getParcelableArrayList("list");
        }else {
            return;
        }
        //这里再去设置Adapter
        mRecyclerView.setAdapter(new CommonAdapter<Item>(mContext, R.layout.plan_item, mItems)
        {
            @Override
            public void convert(ViewHolder holder, Item item,int pos)
            {
                //设置Adapter
                mRecyclerView.setAdapter(new CommonAdapter<Item>(mContext, R.layout.plan_item, mItems)
                {
                    @Override
                    public void convert(ViewHolder holder, Item item,int pos)
                    {
                        holder.setText(R.id.plan_item_name,item.getName());
                        ((SimpleDraweeView) holder.getView(R.id.plan_item_pic) ).setImageURI(item.getImage());
                        holder.setText(R.id.plan_storage,""+1000);  //因为这个Adapter不能容纳一个二元组，我也暂时懒得重写了，库存暂时写死
                        holder.setText(R.id.plan_price,"￥"+item.getPrice());
                        final ImageButton buy = ((ImageButton) holder.getView(R.id.plan_buy));
                        buy.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                buy.setImageResource(R.drawable.ic_plan_to_buy);
                                //加入到准备购买的商品名单当中去
                            }
                        });

                    }
                });
            }
        });
    }

}
