package com.example.jack.myapplication.Fragment;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.card.CardProvider;
import com.dexafree.materialList.card.OnActionClickListener;
import com.dexafree.materialList.card.action.TextViewAction;
import com.dexafree.materialList.view.MaterialListView;

import com.example.jack.myapplication.MainActivity;
import com.example.jack.myapplication.Model.Item;
import com.example.jack.myapplication.Model.LineItem;
import com.example.jack.myapplication.R;
import com.example.jack.myapplication.Util.Constant;
import com.example.jack.myapplication.Util.Event.InternetEvent;
import com.example.jack.myapplication.Util.Event.MessageEvent;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.squareup.picasso.RequestCreator;

import org.greenrobot.eventbus.EventBus;

import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

/**
 * 目前的购物清单
 */
public class Fragment1 extends Fragment {

    /**
     * 单例对象实例
     */
    private static Fragment1 instance = null;
    private Context mContext = this.getActivity();


    /**
     * 对外接口
     *
     * @return Fragment1
     */
    public static Fragment1 GetInstance() {
        if (instance == null)
            instance = new Fragment1();
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_1, container, false);
        mContext = this.getActivity();
        //添加布局
        ViewGroup tab = (ViewGroup) view.findViewById(R.id.tab1);

        tab.addView(LayoutInflater.from(mContext).inflate(R.layout.demo_basic, tab, false));
        //获得viewpager和指示器
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewpager1);
        SmartTabLayout viewPagerTab = (SmartTabLayout) view.findViewById(R.id.viewpagertab1);


        FragmentPagerItems pages = new FragmentPagerItems(mContext);
        //add fragments
        pages.add(FragmentPagerItem.of("商场促销", Fragment_cuxiao.class));
        pages.add(FragmentPagerItem.of("历史订单", Fragment_list.class));

        //Fragment嵌套Fragment一定要使用getChildFragmentManager
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getChildFragmentManager(), pages);
        viewPager.setAdapter(adapter);

        viewPagerTab.setViewPager(viewPager);

        return view;
    }




    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i("F1", "onViewCreated");


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // mArgument = getActivity().getIntent().getStringExtra(ARGUMENT);

    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i("F1", "OnStart");
    }

    @Override
    public void onStop() {
        Log.i("F1", "OnStop");
        super.onStop();
    }

    @Override
    public void onDetach() {
        Log.i("F1", "onDetach");
        super.onDetach();
        instance = null;
    }

    @Override
    public String toString() {
        return "f1";
    }


}
