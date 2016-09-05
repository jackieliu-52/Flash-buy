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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 计划界面，这里还是有点问题，因为如果想加入一个scrollview
 * 那么会和viewpager产生不可描述的冲突，即使自定义scrollview,viewpager滑动的手势也会被scrollview捕获
 * 再加上在scrollview滑动的时候还得去固定住viewpager的指示器，太过于麻烦，暂时不处理
 */
public class Fragment_plan extends android.support.v4.app.Fragment {

    public static List<Item> planItems = new ArrayList<>(); //计划要购买的商品

    Context mContext;
    RecyclerView mRecyclerView;
    List<TwoTuple<String,String>> tops;
    PlanTopAdapter mAdapter;

    List<Item> mItems;
    List<Item> mItems1;
    List<Item> mItems2;
    List<Item> mItems3;
    List<Item> mItems4;

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

        //添加商品数据
        test();

        Bundle bundle1 = new Bundle();
        bundle1.putParcelableArrayList("list",(ArrayList<? extends Parcelable>) mItems);
        Bundle bundle2 = new Bundle();
        bundle2.putParcelableArrayList("list",(ArrayList<? extends Parcelable>) mItems1);
        Bundle bundle3 = new Bundle();
        bundle3.putParcelableArrayList("list",(ArrayList<? extends Parcelable>) mItems2);
        Bundle bundle4 = new Bundle();
        bundle4.putParcelableArrayList("list",(ArrayList<? extends Parcelable>) mItems3);
        Bundle bundle5 = new Bundle();
        bundle5.putParcelableArrayList("list",(ArrayList<? extends Parcelable>) mItems4);

        FragmentPagerItems pages = new FragmentPagerItems(mContext);
        //add fragments
        pages.add(FragmentPagerItem.of(getString(R.string.demo_tab_no_title), Fragment_lazyLoad.class,bundle1));
        pages.add(FragmentPagerItem.of(getString(R.string.demo_tab_no_title), Fragment_lazyLoad.class,bundle2));
        pages.add(FragmentPagerItem.of(getString(R.string.demo_tab_no_title), Fragment_lazyLoad.class,bundle3));
        pages.add(FragmentPagerItem.of(getString(R.string.demo_tab_no_title), Fragment_lazyLoad.class,bundle4));
        pages.add(FragmentPagerItem.of(getString(R.string.demo_tab_no_title), Fragment_lazyLoad.class,bundle5));

        //Fragment嵌套Fragment一定要使用getChildFragmentManager
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getChildFragmentManager(), pages);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2); //增加预加载功能
        viewPagerTab.setViewPager(viewPager);

        //新建一个菜单
        setHasOptionsMenu(true);

        return view;
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
                        textView.setText("美容洗护");
                        break;
                    case 2:
                        icon.setImageDrawable(new IconicsDrawable(mContext)
                                .icon(GoogleMaterial.Icon.gmd_layers)
                                .sizeDp(18));
                        textView.setText("生鲜");
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
                        textView.setText("酒水饮料");
                        break;
                    default:
                        throw new IllegalStateException("Invalid position: " + position);
                }
                return view;
            }
        });

    }

    /**
     * 加入测试数据
     */
    private void test(){
        mItems = new ArrayList<>();
        mItems1 = new ArrayList<>();
        mItems2 = new ArrayList<>();
        mItems3 = new ArrayList<>();
        mItems4 = new ArrayList<>();

        Item item = new Item();
        item.setName("安慕希酸奶");
        item.setPrice(59.4);
        item.setImage("http://obsyvbwp3.bkt.clouddn.com/133.JPG");
        item.setIid("1330");
        item.setPid("13");
        item.setSource("中国");
        item.setSize("205g*12");

        Item item1 = new Item();
        item1.setName("三只松鼠夏威夷果");
        item1.setPrice(22.9);
        item1.setImage("http://obsyvbwp3.bkt.clouddn.com/134.JPG");
        item1.setIid("1340");
        item1.setPid("13");
        item1.setSource("中国");
        item1.setSize("265g");

        Item item2 = new Item();
        item2.setName("乐事无限薯片三连罐");
        item2.setPrice(35);
        item2.setImage("http://obsyvbwp3.bkt.clouddn.com/135.JPG");
        item2.setIid("1350");
        item2.setPid("13");
        item2.setSource("中国");
        item2.setSize("104g*3");

        Item item3 = new Item();
        item3.setName("Aji泰氏风味榴莲饼");
        item3.setPrice(15.9);
        item3.setImage("http://obsyvbwp3.bkt.clouddn.com/136.JPG");
        item3.setIid("1360");
        item3.setPid("13");
        item3.setSource("中国");
        item3.setSize("200g");

        Item item4 = new Item();
        item4.setName("统一老坛酸菜牛肉面");
        item4.setPrice(12.5);
        item4.setImage("http://obsyvbwp3.bkt.clouddn.com/137.JPG");
        item4.setIid("1370");
        item4.setPid("13");
        item4.setSource("中国");
        item4.setSize("121g*5");

        Item item5 = new Item();
        item5.setName("香楠玫瑰鲜花饼");
        item5.setPrice(3.9);
        item5.setImage("http://obsyvbwp3.bkt.clouddn.com/1380.JPG");
        item5.setIid("1380");
        item5.setPid("13");
        item5.setSource("中国");
        item5.setSize("60g");

        Item item6 = new Item();
        item6.setName("百草味碧根果奶油味");
        item6.setPrice(35.9);
        item6.setImage("http://obsyvbwp3.bkt.clouddn.com/1381.JPG");
        item6.setIid("1381");
        item6.setPid("13");
        item6.setSource("中国");
        item6.setSize("218g");

        Item item7 = new Item();
        item7.setName("张君雅小妹妹日式串烧烤丸子");
        item7.setPrice(7.9);
        item7.setImage("http://obsyvbwp3.bkt.clouddn.com/1383.JPG");
        item7.setIid("1383");
        item7.setPid("13");
        item7.setSource("中国");
        item7.setSize("80g");

        Item item8 = new Item();
        item8.setName("致中和龟苓膏");
        item8.setPrice(14.3);
        item8.setImage("http://obsyvbwp3.bkt.clouddn.com/1382.JPG");
        item8.setIid("1382");
        item8.setPid("13");
        item8.setSource("中国");
        item8.setSize("80g");

        Item item9 = new Item();
        item9.setName("姚太太榴莲干");
        item9.setPrice(12.8);
        item9.setImage("http://obsyvbwp3.bkt.clouddn.com/1384.JPG");
        item9.setIid("1384");
        item9.setPid("13");
        item9.setSource("中国");
        item9.setSize("30g");
        mItems.add(item);
        mItems.add(item1);
        mItems.add(item2);
        mItems.add(item3);
        mItems.add(item4);
        mItems.add(item5);
        mItems.add(item6);
        mItems.add(item7);
        mItems.add(item8);
        mItems.add(item9);

        Item item10 = new Item();
        item10.setName("云南白药牙膏薄荷清爽型");
        item10.setPrice(31);
        item10.setImage("http://obsyvbwp3.bkt.clouddn.com/141.JPG");
        item10.setIid("1410");
        item10.setPid("14");
        item10.setSource("中国");
        item10.setSize("210g");

        Item item11 = new Item();
        item11.setName("欧乐B电动牙刷");
        item11.setPrice(199);
        item11.setImage("http://obsyvbwp3.bkt.clouddn.com/142.JPG");
        item11.setIid("1420");
        item11.setPid("14");
        item11.setSource("中国");
        item11.setSize("0.255kg");

        Item item12 = new Item();
        item12.setName("Za姬芮新能真皙美白隔离霜");
        item12.setPrice(69.9);
        item12.setImage("http://obsyvbwp3.bkt.clouddn.com/143.JPG");
        item12.setIid("1430");
        item12.setPid("14");
        item12.setSource("中国");
        item12.setSize("35g");

        Item item13 = new Item();
        item13.setName("完美芦荟胶");
        item13.setPrice(89);
        item13.setImage("http://obsyvbwp3.bkt.clouddn.com/144.JPG");
        item13.setIid("1440");
        item13.setPid("14");
        item13.setSource("中国");
        item13.setSize("40g*3");

        Item item14 = new Item();
        item14.setName("力士沐浴露");
        item14.setPrice(28.9);
        item14.setImage("http://obsyvbwp3.bkt.clouddn.com/145.JPG");
        item14.setIid("1450");
        item14.setPid("14");
        item14.setSource("中国");
        item14.setSize("720ml");

        Item item15 = new Item();
        item15.setName("百雀羚眼部精华");
        item15.setPrice(128);
        item15.setImage("http://obsyvbwp3.bkt.clouddn.com/146.JPG");
        item15.setIid("1460");
        item15.setPid("14");
        item15.setSource("中国");
        item15.setSize("15ml");

        Item item16 = new Item();
        item16.setName("薇婷沐浴用脱毛膏");
        item16.setPrice(95.9);
        item16.setImage("http://obsyvbwp3.bkt.clouddn.com/1470.JPG");
        item16.setIid("1470");
        item16.setPid("14");
        item16.setSource("中国");
        item16.setSize("135ml");

        Item item17 = new Item();
        item17.setName("御泥坊亮颜水润蚕丝面膜");
        item17.setPrice(59.9);
        item17.setImage("http://obsyvbwp3.bkt.clouddn.com/1480.JPG");
        item17.setIid("1480");
        item17.setPid("14");
        item17.setSource("中国");
        item17.setSize("20片");

        Item item18 = new Item();
        item18.setName("御泥坊亮颜水润蚕丝面膜");
        item18.setPrice(99.9);
        item18.setImage("http://obsyvbwp3.bkt.clouddn.com/1490.JPG");
        item18.setIid("1490");
        item18.setPid("14");
        item18.setSource("中国");
        item18.setSize("120g*3");

        mItems1.add(item10);
        mItems1.add(item11);
        mItems1.add(item12);
        mItems1.add(item13);
        mItems1.add(item14);
        mItems1.add(item15);
        mItems1.add(item16);
        mItems1.add(item17);
        mItems1.add(item18);

        Item item19 = new Item();
        item19.setName("河北皇冠梨");
        item19.setPrice(29);
        item19.setImage("http://obsyvbwp3.bkt.clouddn.com/151.JPG");
        item19.setIid("1510");
        item19.setPid("15");
        item19.setSource("中国");
        item19.setSize("9*200g");

        Item item20 = new Item();
        item20.setName("泰国椰青");
        item20.setPrice(32);
        item20.setImage("http://obsyvbwp3.bkt.clouddn.com/152.JPG");
        item20.setIid("1520");
        item20.setPid("15");
        item20.setSource("中国");
        item20.setSize("1.4kg");

        Item item21 = new Item();
        item21.setName("海南西周蜜瓜");
        item21.setPrice(25);
        item21.setImage("http://obsyvbwp3.bkt.clouddn.com/153.JPG");
        item21.setIid("1530");
        item21.setPid("15");
        item21.setSource("中国");
        item21.setSize("1.5kg");

        Item item22 = new Item();
        item22.setName("海南三亚苹果芒");
        item22.setPrice(29.9);
        item22.setImage("http://obsyvbwp3.bkt.clouddn.com/155.JPG");
        item22.setIid("1550");
        item22.setPid("15");
        item22.setSource("中国");
        item22.setSize("1kg");

        Item item23 = new Item();
        item23.setName("智利牛油果");
        item23.setPrice(118);
        item23.setImage("http://obsyvbwp3.bkt.clouddn.com/154.JPG");
        item23.setIid("1540");
        item23.setPid("15");
        item23.setSource("中国");
        item23.setSize("1kg");

        Item item24 = new Item();
        item24.setName("泰国龙眼");
        item24.setPrice(118);
        item24.setImage("http://obsyvbwp3.bkt.clouddn.com/1551.JPG");
        item24.setIid("1551");
        item24.setPid("15");
        item24.setSource("中国");
        item24.setSize("500g");


        Item item25 = new Item();
        item25.setName("南非葡萄柚");
        item25.setPrice(59);
        item25.setImage("http://obsyvbwp3.bkt.clouddn.com/1552.JPG");
        item25.setIid("1552");
        item25.setPid("15");
        item25.setSource("中国");
        item25.setSize("280g*8");

        mItems2.add(item19);
        mItems2.add(item20);
        mItems2.add(item21);
        mItems2.add(item22);
        mItems2.add(item23);
        mItems2.add(item24);
        mItems2.add(item25);

        Item item26 = new Item();
        item26.setName("小熊SNJ-A10K5酸奶机");
        item26.setPrice(149);
        item26.setImage("http://obsyvbwp3.bkt.clouddn.com/161.JPG");
        item26.setIid("1610");
        item26.setPid("16");
        item26.setSource("中国");
        item26.setSize("8分杯内胆");

        Item item27 = new Item();
        item27.setName("九阳JYY-50YL1智能电压力锅");
        item27.setPrice(199);
        item27.setImage("http://obsyvbwp3.bkt.clouddn.com/162.JPG");
        item27.setIid("1620");
        item27.setPid("16");
        item27.setSource("中国");
        item27.setSize("5L");

        Item item28 = new Item();
        item28.setName("美的MJ-BL25B3料理机果汁机电动搅拌机");
        item28.setPrice(199);
        item28.setImage("http://obsyvbwp3.bkt.clouddn.com/163.JPG");
        item28.setIid("1630");
        item28.setPid("16");
        item28.setSource("中国");
        item28.setSize("2.7kg");

        Item item29 = new Item();
        item29.setName("九阳C06-M1电磁炉");
        item29.setPrice(129);
        item29.setImage("http://obsyvbwp3.bkt.clouddn.com/165.JPG");
        item29.setIid("1650");
        item29.setPid("16");
        item29.setSource("中国");
        item29.setSize("1000W");

        Item item30 = new Item();
        item30.setName("松下家用吸尘器WGE61");
        item30.setPrice(1990);
        item30.setImage("http://obsyvbwp3.bkt.clouddn.com/1661.JPG");
        item30.setIid("1661");
        item30.setPid("16");
        item30.setSource("中国");
        item30.setSize("850W");

        mItems3.add(item26);
        mItems3.add(item27);
        mItems3.add(item28);
        mItems3.add(item29);
        mItems3.add(item30);

        Item item31 = new Item();
        item31.setName("雀巢速溶咖啡1+2原味");
        item31.setPrice(99);
        item31.setImage("http://obsyvbwp3.bkt.clouddn.com/171.JPG");
        item31.setIid("1710");
        item31.setPid("17");
        item31.setSource("中国");
        item31.setSize("100条");

        Item item32 = new Item();
        item32.setName("桂格即食燕麦片");
        item32.setPrice(23.9);
        item32.setImage("http://obsyvbwp3.bkt.clouddn.com/172.JPG");
        item32.setIid("1720");
        item32.setPid("17");
        item32.setSource("中国");
        item32.setSize("1478g");

        mItems4.add(item32);
        mItems4.add(item31);
    }
}
