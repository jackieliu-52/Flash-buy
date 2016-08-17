package com.example.jack.myapplication.Fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.card.CardProvider;
import com.dexafree.materialList.listeners.RecyclerItemClickListener;
import com.dexafree.materialList.view.MaterialListAdapter;
import com.dexafree.materialList.view.MaterialListView;
import com.example.jack.myapplication.Model.Jiaju;
import com.example.jack.myapplication.R;
import com.example.jack.myapplication.Util.Constant;
import com.example.jack.myapplication.Util.Event.InternetEvent;
import com.example.jack.myapplication.Util.Event.MessageEvent;
import com.example.jack.myapplication.Util.Util;
import com.example.jack.myapplication.WebActivity;
import com.squareup.picasso.RequestCreator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

/**
 * Created by Jack on 2016/8/1.
 */
public class Fragment2 extends android.support.v4.app.Fragment{

    private Context mContext;
    //智能家居网址List
    private static ArrayList<Jiaju> items = new ArrayList<>();
    private int num = 0 ;
    //默认的智能家居网址（通过IP地址调用摄像头）
    private String url = "http://192.168.191.2:8081/";


    //List和SwipeLayout
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private MaterialListView mListView;
    private MaterialListAdapter mListAdapter;

    static {
        Jiaju item = new Jiaju("智能冰箱","您可以随时了解您家中冰箱的状态","http://192.168.191.2:8081/","");
        Jiaju item1 = new Jiaju("智能笔筒","家中是不是需要购置文具呢","http://www.baidu.com","");
        items.add(item);
        items.add(item1);
    }


    private static Fragment2 instance = null;
    /**
     * 对外接口
     * @return Fragment2
     */
    public static Fragment2 GetInstance()
    {
        if(instance == null)
            instance = new Fragment2();
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_2, container, false);
        Log.i("F2","onCreateView");
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_jList);
        mListView = (MaterialListView) view.findViewById(R.id.material_jiaju_list);
        mListView.setItemAnimator(new SlideInLeftAnimator());
        mListView.getItemAnimator().setAddDuration(300);
        mListView.getItemAnimator().setRemoveDuration(300);
        //设置两列
        mListView.setColumnCountPortrait(2);
        mListAdapter = mListView.getAdapter();

        init();
        initRefresh();

        // Add the ItemTouchListener
        mListView.addOnItemTouchListener(new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull Card card, int position) {
                Jiaju item = (Jiaju) card.getTag();
                Intent intent = new Intent(mContext, WebActivity.class);
                //将url信息传给WebView
                intent.putExtra("url",item.getUrl());
                startActivity(intent);

            }

            @Override
            public void onItemLongClick(@NonNull Card card, int position) {

            }
        });

        return view;
    }

    /**
     * 初始化ListView
     */
    private void init() {
        num = 0;
        for(Jiaju item : items){
            String image = item.getImage();
            if(image.equals("")) {
                int resId = Util.stringToId(mContext,"yibao");
                image = Uri.parse("android.resource://" + getContext().getPackageName()
                        + "/" + resId).toString();
            }
            final CardProvider provider = new Card.Builder(mContext)
                    .setTag(item)
                    .withProvider(new CardProvider())
                    .setLayout(R.layout.material_image_with_des)
                    .setTitle(item.getName())
                    .setDescription(item.getDescription())
                    .setDrawable(image)
                    ;
            Card card = provider.endConfig().build();
            mListAdapter.add(card);
            num++;
        }
    }

    /**
     * 绑定刷新事件
     */
    private void initRefresh(){
        //设置刷新时动画的颜色，可以设置4个
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // TODO Auto-generated method stub
                EventBus.getDefault().post(new MessageEvent("刷新智能家居"));
                //获取信息，然后再刷新UI

                //先清空所有
                mListAdapter.clearAll();
                init();

                //最后再把刷新取消
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);

                    }
                });
            }//onRefresh
        });
    }
    /**
     * 获得想绑定的Activity的Context
     * @param context
     */
    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(getUserVisibleHint()){
            Log.i("F2","可见");
        }
        else {
            Log.i("F2","不可见");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        Log.i("F2","onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

        Log.i("F2","onStart");
    }
    @Override
    public void onResume() {
        super.onResume();
        Log.i("F2","onResume");
    }
    @Override
    public void onPause() {
        super.onPause();
        Log.i("F2","onPause");
    }
    @Override
    public void onStop() {
        Log.i("F2","onStop");
        super.onStop();
    }
    @Override
    public void onDestroyView(){
        Log.i("F2","onDestroyView");
        super.onDestroyView();
    }
    @Override
    public void onDestroy(){
        Log.i("F2","onDestroy");
        super.onDestroy();
    }

    @Override
    public void onDetach(){
        Log.i("F2","onDetach");
        super.onDetach();
        instance = null;
    }
    @Override
    public String toString()
    {
        return "f2";
    }


}
