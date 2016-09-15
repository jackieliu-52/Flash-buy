package com.example.jack.myapplication.Fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.card.CardProvider;
import com.dexafree.materialList.listeners.RecyclerItemClickListener;
import com.dexafree.materialList.view.MaterialListAdapter;
import com.dexafree.materialList.view.MaterialListView;
import com.example.jack.myapplication.Model.BulkItem;
import com.example.jack.myapplication.Model.Item;
import com.example.jack.myapplication.Model.Jiaju;
import com.example.jack.myapplication.Model.TwoTuple;
import com.example.jack.myapplication.R;
import com.example.jack.myapplication.Util.Constant;
import com.example.jack.myapplication.Util.Event.InternetEvent;
import com.example.jack.myapplication.Util.Event.MessageEvent;
import com.example.jack.myapplication.Util.Util;
import com.example.jack.myapplication.View.Recyclerview.CommonRecyclerAdapter;
import com.example.jack.myapplication.View.Recyclerview.CommonRecyclerViewHolder;
import com.example.jack.myapplication.WebActivity;
import com.litesuits.common.utils.NumberUtil;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

/**
 * 智能家具
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

    //智能冰箱中的商品
    public static List<TwoTuple<Boolean,BulkItem>> myItems = new ArrayList<>();

    static {
        Jiaju item = new Jiaju("智能冰箱","您可以随时了解您家中冰箱的状态","http://192.168.191.2:8081/","");
        Jiaju item1 = new Jiaju("智能笔筒","家中是不是需要购置文具呢","http://www.baidu.com","");
        items.add(item);
        items.add(item1);

        //自定义散装商品
        BulkItem bulkitem = new BulkItem();
        bulkitem.setName("青菜");
        bulkitem.setImage("http://obsyvbwp3.bkt.clouddn.com/liquid.png");
        bulkitem.setPrice(2.33);
        bulkitem.setWeight(1);
        bulkitem.setAttr1("闭光存储");
        //5天以前生产
        bulkitem.setProduceTime(Util.getBefoceTime(5));
        //获得到期时间
        bulkitem.jisuan();

        BulkItem bulkitem1 = new BulkItem();
        bulkitem1.setName("花生");
        bulkitem1.setImage("http://obsyvbwp3.bkt.clouddn.com/liquid.png");
        bulkitem1.setPrice(5.00);
        bulkitem1.setWeight(2.33);
        bulkitem1.setAttr1("冷藏");
        bulkitem1.setShelfTime(10);
        //19天前生产
        bulkitem1.setProduceTime(Util.getBefoceTime(19));
        bulkitem1.jisuan();

        myItems.add(new TwoTuple<>(false,bulkitem));
        myItems.add(new TwoTuple<>(false,bulkitem1));
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
            //单击打开详情
            @Override
            public void onItemClick(@NonNull Card card, int position) {
                Jiaju item = (Jiaju) card.getTag();
                if(item.getName().equals("智能冰箱")) {
                    CommonRecyclerAdapter<TwoTuple<Boolean, BulkItem>> adapter;
                    adapter = getAdapter(myItems);
                    new MaterialDialog.Builder(mContext)
                            .title("智能冰箱")
                            .adapter(adapter, null)
                            .positiveText("删除")
                            .negativeText("取消")
                            .negativeColorRes(R.color.orange_button)
                            .onAny(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    if (which == DialogAction.POSITIVE) {
                                        //被选中的物品删除购物车
                                        for (TwoTuple<Boolean, BulkItem> o : myItems) {
                                            if (o.first) {
                                                BulkItem copyOne = o.second;
                                                EventBus.getDefault().post(new MessageEvent(copyOne.getName() + "已删除"));
                                            }
                                        }
                                    }
                                }
                            })
                            .show();
                }
            }

            //长击打开WebView
            @Override
            public void onItemLongClick(@NonNull Card card, int position) {
                Jiaju item = (Jiaju) card.getTag();
                Intent intent = new Intent(mContext, WebActivity.class);
                //将url信息传给WebView
                intent.putExtra("url",item.getUrl());
                startActivity(intent);
            }
        });

        return view;
    }

    private CommonRecyclerAdapter getAdapter(List<TwoTuple<Boolean,BulkItem>> items){
        return new CommonRecyclerAdapter<TwoTuple<Boolean, BulkItem>>(mContext,R.layout.plan_items,items) {
            @Override
            public void convert(final CommonRecyclerViewHolder holder, TwoTuple<Boolean, BulkItem> o) {
                BulkItem item = o.second;        //散装商品
                holder.setText(R.id.tv_item_name, item.getName());
                String date = item.getProduceTime();
                String currentDate = Util.getCurrentTime();     //今天多少号

                String[] temp = date.split("/");
                String[] temp1 = currentDate.split("/");
                //这里只考虑同一个月
                int dis = NumberUtil.convertToInteger(temp1[2]) - NumberUtil.convertToInteger(temp[2]);

                View v_color = holder.getView(R.id.v_color);
                Drawable color;
                color = getResources().getDrawable(R.color.buji);
                if(dis > 10){
                    holder.setText(R.id.tv_item_date, "还有" + dis + "天到期");   //到期日期
                } else if(dis > 5){
                    color = getResources().getDrawable(R.color.ji);
                    holder.setText(R.id.tv_item_date, "还有" + dis + "天到期");   //到期日期
                } else if(dis > 1){
                    color = getResources().getDrawable(R.color.kuaiguoqi);
                    holder.setText(R.id.tv_item_date, "仅有" + dis + "天到期");   //到期日期
                } else{
                    color = getResources().getDrawable(R.color.guoqi);
                    holder.setText(R.id.tv_item_date, "该商品已过期");   //到期日期
                }

                v_color.setBackground(color);

                //图片
                //加载图片
                Picasso.with(mContext)
                        .load(item.getImage())
                        .placeholder(R.drawable.ic_launcher)
                        .into(((ImageView) holder.getView(R.id.ci_image)));

            }
        };
    }
    /**
     * 初始化ListView
     */
    private void init() {
        num = 0;
        for(Jiaju item : items){
//            String image = item.getImage();
//            if(image.equals("")) {
//                int resId = Util.stringToId(mContext,"good");
//                image = Uri.parse("android.resource://" + getContext().getPackageName()
//                        + "/" + resId).toString();
//            }
            final CardProvider provider = new Card.Builder(mContext)
                    .setTag(item)
                    .withProvider(new CardProvider())
                    .setLayout(R.layout.material_image_with_des)
                    .setTitle(item.getName())
                    .setDescription(item.getDescription())
                    .setDrawable("http://img.home.jc001.cn/baike/s/54/8e/548e550f160ba0bd488b4568.jpg")
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
