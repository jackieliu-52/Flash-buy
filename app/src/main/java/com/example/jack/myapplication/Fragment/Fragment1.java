package com.example.jack.myapplication.Fragment;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.holder.Holder;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.example.jack.myapplication.R;
import com.example.jack.myapplication.Util.MessageEvent;
import com.example.jack.myapplication.Util.Util;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jack on 2016/8/1.
 */
public class Fragment1 extends android.support.v4.app.Fragment implements OnItemClickListener {
    private String mArgument;
    private ConvenientBanner convenientBanner;//顶部广告栏控件
    private ArrayList<Integer> localImages = new ArrayList<Integer>();   //保存本地图片
    private CBViewHolderCreator cbViewHolderCreator;  //ViewHolder

    public static final String ARGUMENT = "argument";
    /**
     * 单例对象实例
     */
    private static Fragment1 instance = null;

    /**
     * 对外接口
     * @return Fragment1
     */
    public static Fragment1 GetInstance()
    {
        if(instance == null)
            instance = new Fragment1();
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_1, container, false);
        convenientBanner = (ConvenientBanner) view.findViewById(R.id.convenientBanner);
        init();
        return view;
    }

    private void init()
    {
        //本地图片集合
        for (int position = 0; position < 3; position++)
            localImages.add(Util.getResId("ic_test_" + position, R.drawable.class));
        cbViewHolderCreator = new CBViewHolderCreator<LocalImageHolderView>() {
            @Override
            public LocalImageHolderView createHolder() {
                return new LocalImageHolderView();
            }
        };
        Log.i("test",cbViewHolderCreator.toString());
        Log.i("test",convenientBanner.toString());
        //本地图片例子
        convenientBanner.setPages(cbViewHolderCreator
                , localImages)
                //设置两个点图片作为翻页指示器，不设置则没有指示器，可以根据自己需求自行配合自己的指示器,不需要圆点指示器可用不设
                .setPageIndicator(new int[]{R.mipmap.ic_page_indicator, R.mipmap.ic_page_indicator_focused})
                //设置指示器的方向
//                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.ALIGN_PARENT_RIGHT)
//                .setOnPageChangeListener(this)//监听翻页事件
                .setOnItemClickListener(this);
    }



    /**
     * 广告栏的点击响应
     * @param position
     */
    @Override
    public void onItemClick(int position) {
        Toast.makeText(this.getActivity(),"点击了第"+position+"个",Toast.LENGTH_SHORT).show();
    }

    // 开始自动翻页
    @Override
    public void onResume() {
        super.onResume();
        //开始自动翻页
        convenientBanner.startTurning(5000);
    }
    @Override
    public void onHiddenChanged(boolean hidden) {
        if(!hidden)
            convenientBanner.startTurning(5000);
        else
            convenientBanner.stopTurning();
    }
    // 停止自动翻页
    @Override
    public void onPause() {
        super.onPause();
        //停止翻页
        convenientBanner.stopTurning();
    }

    //用来保存图片
    public class LocalImageHolderView implements Holder<Integer> {
        private ImageView imageView;
        @Override
        public View createView(Context context) {
            imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            return imageView;
        }

        @Override
        public void UpdateUI(Context context, final int position, Integer data) {
            imageView.setImageResource(data);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        Log.i("F1","onViewCreated");

        // EventBus演示
        new Thread()
        {
            public void run()
            {
                try
                {
                    Thread.sleep(2000); // 模拟延时
                    // 发布事件，在后台线程发的事件
                    EventBus.getDefault().post(new MessageEvent("Hello everyone!"));
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            };
        }.start();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // mArgument = getActivity().getIntent().getStringExtra(ARGUMENT);
        Bundle bundle = getArguments();
        if (bundle != null)
            mArgument = bundle.getString(ARGUMENT);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i("F1","OnStart");
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        Log.i("F1","OnStop");
        super.onStop();
    }
    /**
     * 传入需要的参数，设置给arguments
     * @param argument
     * @return
     */
    public static Fragment1 newInstance(String argument)
    {
        Bundle bundle = new Bundle();
        bundle.putString(ARGUMENT, argument);
        Fragment1 fragment1 = new Fragment1();
        fragment1.setArguments(bundle);
        return fragment1;
    }
    @Override
    public void onDetach(){
        Log.i("F1","onDetach");
        super.onDetach();
        instance = null;
    }
    @Override
    public String toString()
    {
        return "f1";
    }
    // This method will be called when a SomeOtherEvent is posted
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void handleSomethingElse(MessageEvent event) {
     //   doSomethingWith(event);
    }
}
