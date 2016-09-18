package com.example.jack.myapplication.Fragment.FragmentPlan;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.jack.myapplication.Adapter.LazyLoadAdapter;
import com.example.jack.myapplication.Fragment.BaseFragment;
import com.example.jack.myapplication.Fragment.Fragment_item;
import com.example.jack.myapplication.Fragment.Fragment_plan;
import com.example.jack.myapplication.Model.Item;
import com.example.jack.myapplication.Model.TwoTuple;
import com.example.jack.myapplication.R;
import com.example.jack.myapplication.Util.Event.ListEvent;
import com.example.jack.myapplication.Util.Event.MessageEvent;
import com.example.jack.myapplication.View.Recyclerview.DividerItemDecoration;
import com.example.jack.myapplication.View.Recyclerview.MyItemDecoration;


import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * 实现懒加载的Fragment
 */
public class Fragment_lazyLoad extends BaseFragment  {
    RecyclerView mRecyclerView;
    List<Item> mItems; //要加载的商品

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lazy_load, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.lazy_recyclerView);
        //瀑布流设置
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,
        StaggeredGridLayoutManager.VERTICAL));

        //加入分割线
        mRecyclerView.addItemDecoration(new MyItemDecoration(10));

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
        LazyLoadAdapter adapter = new LazyLoadAdapter(mContext,mItems);

        //这里再去设置Adapter
        mRecyclerView.setAdapter(adapter);
        adapter.setOnItemClickLitener(new LazyLoadAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                //打开商品页面
                //EventBus.getDefault().post(new MessageEvent("您点击了" + mItems.get(position).getName()));
                Fragment_item.item = mItems.get(position);
                //这里应该通知Activity去改变UI，不能让这个Fragment获取其他Fragment的引用
                EventBus.getDefault().post(new ListEvent("fragment_item",view));
            }

            @Override
            public void onItemPlantoBuy(View view, int position) {
                Fragment_plan.planItems.add(new TwoTuple(false,mItems.get(position)));  //加入要购买的商品中
                EventBus.getDefault().post(new MessageEvent( mItems.get(position).getName() +"已加入了购物车"));
            }
        });
    }




}
