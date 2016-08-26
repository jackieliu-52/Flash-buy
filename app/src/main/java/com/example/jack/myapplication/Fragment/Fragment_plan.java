package com.example.jack.myapplication.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jack.myapplication.Adapter.PlanTopAdapter;
import com.example.jack.myapplication.Fragment.FragmentPlan.Fragment_lazyLoad;
import com.example.jack.myapplication.Model.Item;
import com.example.jack.myapplication.Model.TwoTuple;
import com.example.jack.myapplication.R;
import com.example.jack.myapplication.Util.Event.MessageEvent;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * 计划界面
 */
public class Fragment_plan extends android.support.v4.app.Fragment {
    Context mContext;
    RecyclerView mRecyclerView;
    List<TwoTuple<String,String>> tops;
    PlanTopAdapter mAdapter;

    List<Item> mItems;
    List<Item> mItems1;
    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_plan, container, false);
        initTop();

        mRecyclerView = (RecyclerView) view.findViewById(R.id.plan_recyclerview);
        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        //设置适配器
        mAdapter = new PlanTopAdapter(mContext,tops);
        initClickEvent();
        mRecyclerView.setAdapter(mAdapter);


        //获得viewpager和指示器
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewpager_plan);
        SmartTabLayout viewPagerTab = (SmartTabLayout) view.findViewById(R.id.viewpager_tab_plan);

        setupTab(viewPagerTab);


        test();
        Bundle bundle1 = new Bundle();
        bundle1.putParcelableArrayList("list",(ArrayList<? extends Parcelable>) mItems);
        Bundle bundle2 = new Bundle();
        bundle2.putParcelableArrayList("list",(ArrayList<? extends Parcelable>) mItems1);

        FragmentPagerItems pages = new FragmentPagerItems(mContext);
        //add fragments
        pages.add(FragmentPagerItem.of(getString(R.string.demo_tab_no_title), Fragment_lazyLoad.class,bundle1));
        pages.add(FragmentPagerItem.of(getString(R.string.demo_tab_no_title), Fragment_lazyLoad.class,bundle2));
        pages.add(FragmentPagerItem.of(getString(R.string.demo_tab_no_title), Fragment_lazyLoad.class,bundle2));
        pages.add(FragmentPagerItem.of(getString(R.string.demo_tab_no_title), Fragment_lazyLoad.class,bundle2));
        pages.add(FragmentPagerItem.of(getString(R.string.demo_tab_no_title), Fragment_lazyLoad.class,bundle2));

        //Fragment嵌套Fragment一定要使用getChildFragmentManager
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getChildFragmentManager(), pages);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(0); //取消viewPager的预加载功能2
        viewPagerTab.setViewPager(viewPager);
        return view;
    }


    private void test(){
        mItems = new ArrayList<>();
        mItems1 = new ArrayList<>();

        Item item2 = new Item();
        item2.setName("牛奶");
        item2.setImage("http://obsyvbwp3.bkt.clouddn.com/milk.png");
        item2.setPrice(2);

        Item item3 = new Item();
        item3.setName("233");
        item3.setImage("http://obsyvbwp3.bkt.clouddn.com/liquid.png");
        item3.setPrice(5);

        mItems.add(item2);
        mItems.add(item3);
        mItems1.add(item3);
    }

    private void initTop(){
        tops = new ArrayList<>();
        TwoTuple<String,String> top = new TwoTuple<>("http://obsyvbwp3.bkt.clouddn.com/food.png","生鲜蔬菜");
        TwoTuple<String,String> top1 = new TwoTuple<>("http://obsyvbwp3.bkt.clouddn.com/milk.png","酒水饮料");
        TwoTuple<String,String> top2 = new TwoTuple<>("http://obsyvbwp3.bkt.clouddn.com/oil.png","粮油副食");
        TwoTuple<String,String> top3 = new TwoTuple<>("http://obsyvbwp3.bkt.clouddn.com/liquid.png","美容洗护");
        tops.add(top);
        tops.add(top1);
        tops.add(top2);
        tops.add(top3);
    }
    private void initClickEvent(){
        mAdapter.setOnItemClickLitener(new PlanTopAdapter.OnItemClickLitener() {
            //打开一个Diaglog，可以选择商品
            @Override
            public void onItemClick(View view, int position) {
                //首先确认是哪个
                TwoTuple<String,String> temp = tops.get(position);
                switch (temp.second){
                    case "生鲜蔬菜":
                        EventBus.getDefault().post(new MessageEvent("生鲜蔬菜"));
                        break;
                    case "酒水饮料":
                        break;
                    case "粮油副食":
                        break;
                    case "美容洗护":
                        break;
                    default:
                        Log.e("Fragment_plan",temp.second);
                        break;
                }

            }


        });
    }

    private void setupTab(SmartTabLayout layout){
        final LayoutInflater inflater = LayoutInflater.from(layout.getContext());
        layout.setCustomTabView(new SmartTabLayout.TabProvider() {
            @Override
            public View createTabView(ViewGroup container, int position, PagerAdapter adapter) {
                View view = inflater.inflate(R.layout.custom_tab_icon_and_text, container,
                        false);
                ImageView icon = (ImageView) view.findViewById(R.id.custom_tab_icon);
                TextView textView = (TextView) view.findViewById(R.id.custom_tab_text);

                switch (position) {
                    case 0:
                        icon.setImageDrawable(new IconicsDrawable(mContext)
                                .icon(GoogleMaterial.Icon.gmd_mall)
                                .sizeDp(18));
                        textView.setText("精选");
                        break;
                    case 1:
                        icon.setImageDrawable(new IconicsDrawable(mContext)
                                .icon(FontAwesome.Icon.faw_map)
                                .sizeDp(18));
                        textView.setText("零食");
                        break;
                    case 2:
                        icon.setImageDrawable(new IconicsDrawable(mContext)
                                .icon(GoogleMaterial.Icon.gmd_layers)
                                .sizeDp(18));
                        textView.setText("家清家居");
                        break;
                    case 3:
                        icon.setImageDrawable(new IconicsDrawable(mContext)
                                .icon(GoogleMaterial.Icon.gmd_watch)
                                .sizeDp(18));
                        textView.setText("家用电器");
                        break;
                    case 4:
                        icon.setImageDrawable(new IconicsDrawable(mContext)
                                .icon(GoogleMaterial.Icon.gmd_account_box)
                                .sizeDp(18));
                        textView.setText("生鲜");
                        break;
                    default:
                        throw new IllegalStateException("Invalid position: " + position);
                }
                return view;
            }
        });

    }
}
