package com.example.jack.myapplication.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.jack.myapplication.Adapter.StaggeredHomeAdapter;
import com.example.jack.myapplication.CommentActivity;
import com.example.jack.myapplication.MainActivity;
import com.example.jack.myapplication.Model.Item;
import com.example.jack.myapplication.Model.User;
import com.example.jack.myapplication.R;
import com.example.jack.myapplication.Util.Event.ListEvent;
import com.example.jack.myapplication.Util.Event.MessageEvent;
import com.example.jack.myapplication.View.Recyclerview.FeedItemAnimator;
import com.example.jack.myapplication.View.Recyclerview.SpacesItemDecoration;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jack on 2016/8/5.
 */
public class Fragment_cuxiao extends android.support.v4.app.Fragment   {
    final private String TAG = "Fragment_cuxiao";
    private Context mContext;
    private SwipeRefreshLayout swipeRefreshLayout;

    private RecyclerView mRecyclerView;

    private static ArrayList<Item> mItems = new ArrayList<>();  //保存促销的数据
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
//        //加入分割线
//        mRecyclerView.addItemDecoration(new SpacesItemDecoration(2));
        mRecyclerView.setItemAnimator(new FeedItemAnimator());

        Log.i(TAG,"onCreateView");
        initEvent();


        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_recyclerview);
        //设置刷新时动画的颜色，可以设置4个
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // TODO Auto-generated method stub
                EventBus.getDefault().post(new MessageEvent("刷新促销信息"));
                //获取信息，然后再刷新UI
                initData();
                //刷新UI
                mItems = new ArrayList<Item>(); //这里清空试试
                mStaggeredHomeAdapter = new StaggeredHomeAdapter(mContext,mItems);
                //重新装载
                mRecyclerView.setAdapter(mStaggeredHomeAdapter);

                //最后再把刷新取消
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
             });
        }//onRefresh
       });


        return view;
    }

    /**
     * 向后台请求促销信息
     */
    private void initData()
    {
        //首先清空当前促销信息
        mItems = new ArrayList<>();
        //再向服务器请求信息,这个请求应该异步操作

        //现在自己简单做一下本地化的促销信息,yibao,laoganma,you,pijiu,shuihu
        Item item1 = new Item("安慕希酸奶","01","0101","http://obsyvbwp3.bkt.clouddn.com/133.JPG","怡宝",59.4,"205g*12");
        Item item2 = new Item("三只松鼠夏威夷果","02","0202","http://obsyvbwp3.bkt.clouddn.com/134.JPG","老干妈",9.00,"一瓶");
        Item item3 = new Item("乐事无限薯片三连罐","03","0303","http://obsyvbwp3.bkt.clouddn.com/135.JPG","心相印",4.00,"DT3200");
        Item item4 = new Item("Aji泰氏风味榴莲饼","03","0303","http://obsyvbwp3.bkt.clouddn.com/136.JPG","心相印",4.00,"DT3200");
        Item item5 = new Item("统一老坛酸菜牛肉面","03","0303","http://obsyvbwp3.bkt.clouddn.com/137.JPG","心相印",4.00,"DT3200");
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
            //进入商品详情
            @Override
            public void onItemClick(View view, int position)
            {
//                Toast.makeText(mContext,
//                        position + " click", Toast.LENGTH_SHORT).show();
                Fragment_item.item = mItems.get(position);
                //这里应该通知Activity去改变UI，不能让这个Fragment获取其他Fragment的引用
                EventBus.getDefault().post(new ListEvent("fragment_item",view));
            }

            //收藏该商品
            @Override
            public void onItemLongClick(View view, int position)
            {
//                //收藏该商品
//                Toast.makeText(mContext,
//                        position + " long click", Toast.LENGTH_SHORT).show();
                User.starItems.add( mItems.get(position));

            }

            @Override
            public void onCommentClick(View view, int pos){
                final Intent intent = new Intent(mContext, CommentActivity.class);
                int[] startingLocation = new int[2];
                view.getLocationOnScreen(startingLocation);
                intent.putExtra(CommentActivity.ARG_DRAWING_START_LOCATION, startingLocation[1]);
                startActivity(intent);
                getActivity().overridePendingTransition(0, 0);
            }

            @Override
            public void onLikeClick(View view,int pos){
                //加入收藏夹
                User.starItems.add( mItems.get(pos));
            }

        });
    }


}
