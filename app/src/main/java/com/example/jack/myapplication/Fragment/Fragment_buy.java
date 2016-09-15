package com.example.jack.myapplication.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.jack.myapplication.Model.Item;
import com.example.jack.myapplication.Model.TwoTuple;
import com.example.jack.myapplication.R;
import com.example.jack.myapplication.Util.Event.PlanBuyEvent;
import com.example.jack.myapplication.Util.Interface.NeedPageChanged;
import com.example.jack.myapplication.View.NoScrollViewPager;
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
 * 用户在购买的时候主要用到的三个Fragment
 */
public class Fragment_buy extends android.support.v4.app.Fragment implements NeedPageChanged {

    private final String TAG = "Fragment_buy";
    public static final String ARGUMENT = "argument";

    private NoScrollViewPager viewPager;
    private SmartTabLayout viewPagerTab;
    private Context context;

    public static List<Item> planItems = new ArrayList<>(); //已经决定决定计划要购买的商品
    public static boolean isShopping = false; //是否处于购物状态中？
    /**
     * 单例对象实例
     */
    private static Fragment_buy instance = null;

    /**
     * 对外接口
     * @return Fragment_buy
     */
    public static Fragment_buy GetInstance()
    {
        if(instance == null)
            instance = new Fragment_buy();
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        context = this.getActivity();
        View view = inflater.inflate(R.layout.fragment_buy, container, false);

        //添加布局
        ViewGroup tab = (ViewGroup) view.findViewById(R.id.tab);

        tab.addView(LayoutInflater.from(context).inflate(R.layout.demo_custom_tab_icons1, tab, false));

        //获得viewpager和指示器
        viewPager = (NoScrollViewPager) view.findViewById(R.id.viewpager);
        viewPagerTab = (SmartTabLayout) view.findViewById(R.id.viewpagertab);

        //为指示器增加图标
        Log.i("setupTab", setupTab(viewPagerTab) + "");

        FragmentPagerItems pages = new FragmentPagerItems(context);


        //add fragments
        pages.add(FragmentPagerItem.of(getString(R.string.demo_tab_no_title), Fragment_plan.class));
        pages.add(FragmentPagerItem.of(getString(R.string.demo_tab_no_title), Fragment_map.class));
        pages.add(FragmentPagerItem.of(getString(R.string.demo_tab_no_title), Fragment_cart.class));

        //Fragment嵌套Fragment一定要使用getChildFragmentManager
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getChildFragmentManager(), pages);
        viewPager.setAdapter(adapter);

        viewPagerTab.setViewPager(viewPager);
        return view;
    }

    private boolean setupTab(SmartTabLayout layout){
        final LayoutInflater inflater = LayoutInflater.from(layout.getContext());


        layout.setCustomTabView(new SmartTabLayout.TabProvider() {
            @Override
            public View createTabView(ViewGroup container, int position, PagerAdapter adapter) {
                ImageView icon = (ImageView) inflater.inflate(R.layout.custom_tab_icon1, container,
                        false);
                switch (position) {
                    case 0:
                        icon.setImageDrawable(new IconicsDrawable(context)
                                .icon(GoogleMaterial.Icon.gmd_mall)
                                .sizeDp(24));
                        break;
                    case 1:
                        icon.setImageDrawable(new IconicsDrawable(context)
                                .icon(FontAwesome.Icon.faw_map)
                                .sizeDp(24));
                        break;
                    case 2:
                        icon.setImageDrawable(new IconicsDrawable(context)
                                .icon(GoogleMaterial.Icon.gmd_layers)
                                .sizeDp(24));
                        break;
                    default:
                        throw new IllegalStateException("Invalid position: " + position);
                }
                return icon;
            }
        });
        return true;
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        Log.i(TAG,"onCreate");
        super.onCreate(savedInstanceState);

    }
    @Override
    public String toString(){
        return "fragment_buy";
    }

    //往前跳转到指定的pager
    public void pageChanged(int page){
        viewPager.setCurrentItem(viewPager.getCurrentItem() + page, true);
        EventBus.getDefault().post(new PlanBuyEvent("initMap"));
        isShopping = true;
    }
}
