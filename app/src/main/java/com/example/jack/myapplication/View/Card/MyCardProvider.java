package com.example.jack.myapplication.View.Card;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.card.CardProvider;
import com.example.jack.myapplication.Adapter.ListRecyclerAdapter;
import com.example.jack.myapplication.Model.LineItem;
import com.example.jack.myapplication.R;

import java.util.List;

/**
 * 订单需要改写Card，所以需要改写cardView的Provider
 * Created by Jack on 2016/9/3.
 */
public class MyCardProvider extends CardProvider<MyCardProvider> {

    private List<LineItem> mItems; //Items的list

    @Override
    public int getLayout() {
        return R.layout.my_card_view;
    }



    public MyCardProvider setLineItem(List<LineItem> mItems){
        this.mItems = mItems;
        notifyDataSetChanged();
        return this;
    }

    @Override
    public void render(@NonNull View view, @NonNull Card card) {
        super.render(view, card);
        RecyclerView recyclerView = (RecyclerView) findViewById(view,R.id.list_items,RecyclerView.class);
        if (recyclerView != null) {
            ListRecyclerAdapter adapter = new ListRecyclerAdapter(getContext(),mItems);
            recyclerView.setAdapter(adapter);
            //设置布局管理器
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            recyclerView.setLayoutManager(linearLayoutManager);


        }
    }
}
