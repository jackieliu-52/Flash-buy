package com.example.jack.myapplication.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.jack.myapplication.Adapter.StaggeredHomeAdapter;
import com.example.jack.myapplication.MainActivity;
import com.example.jack.myapplication.Model.Item;
import com.example.jack.myapplication.R;
import com.example.jack.myapplication.View.Recyclerview.SpacesItemDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jack on 2016/8/5.
 */
public class Fragment_cuxiao extends android.support.v4.app.Fragment   {
    final private String TAG = "Fragment_cuxiao";
    private Context mContext;


    private RecyclerView mRecyclerView;
    private List<String> mDatas;
    private List<Item> mItems;  //保存促销的数据
    private StaggeredHomeAdapter mStaggeredHomeAdapter;

    @Override
    public void onAttach(Context context){
        Log.i("TAG","onAttach + Context");
        super.onAttach(context);
        this.mContext = context;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_cuxiao, container, false);
        //加载促销信息
        initData();

        mRecyclerView = (RecyclerView) view.findViewById(R.id.id_recyclerview);

        mStaggeredHomeAdapter = new StaggeredHomeAdapter(mContext,mItems);

        //瀑布流设置
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(mStaggeredHomeAdapter);
        //加入分割线
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(16));


        Log.i(TAG,"onCreateView");
        initEvent();

        return view;
    }

    /**
     * 向后台请求促销信息
     */
    private void initData()
    {
        mDatas = new ArrayList<String>();
        for (int i = 'A'; i < 'z'; i++)
        {
            mDatas.add("" + (char) i);
        }

        mItems = new ArrayList<Item>();
        //现在自己简单做一下本地化的促销信息,yibao,laoganma,you,pijiu,shuihu
        Item item1 = new Item("怡宝","01","0101","yibao","怡宝",3.00,"1.555L");
        Item item2 = new Item("老干妈","02","0202","laoganma","老干妈",9.00,"一瓶");
        Item item3 = new Item("纸面巾","03","0303","you","心相印",4.00,"DT3200");
        Item item4 = new Item("啤酒","03","0303","pijiu","心相印",4.00,"DT3200");
        Item item5 = new Item("水壶","03","0303","shuihu","心相印",4.00,"DT3200");
        item1.setStar(true); //设置为喜欢
        item1.setDiscount(3);
        mItems.add(item1);
        mItems.add(item2);
        mItems.add(item3);
        mItems.add(item4);
        mItems.add(item5);
    }

    private void initEvent()
    {
        //设置点击图片的方法
        mStaggeredHomeAdapter.setOnItemClickLitener(new StaggeredHomeAdapter.OnItemClickLitener()
        {
            @Override
            public void onItemClick(View view, int position)
            {
                Toast.makeText(mContext,
                        position + " click", Toast.LENGTH_SHORT).show();
            }

            //考虑直接可以加入购物车
            @Override
            public void onItemLongClick(View view, int position)
            {
                Toast.makeText(mContext,
                        position + " long click", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
