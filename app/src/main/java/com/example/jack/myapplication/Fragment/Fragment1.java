package com.example.jack.myapplication.Fragment;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
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
import com.squareup.picasso.RequestCreator;

import org.greenrobot.eventbus.EventBus;

import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

/**
 * Created by Jack on 2016/8/1.
 */
public class Fragment1 extends Fragment {

    MaterialListView mListView;

    SwipeRefreshLayout sr_swipeMaterialListView;
    /**
     * 单例对象实例
     */
    private static Fragment1 instance = null;
    private Context mContext;

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        mContext = context;
    }

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
        mListView = (MaterialListView) view.findViewById(R.id.material_cart_list);
        sr_swipeMaterialListView = (SwipeRefreshLayout) view.findViewById(R.id.swipe_MaterialListView);

        mListView.setItemAnimator(new SlideInLeftAnimator());
        mListView.getItemAnimator().setAddDuration(300);
        mListView.getItemAnimator().setRemoveDuration(300);
        init();
        initRefresh();
        return view;
    }

    private void init() {
        for(LineItem lineItem: MainActivity.cart){
            Item item = lineItem.getItem();
            //因为加了一个LineItem，所以有点bug要处理
            if(item.getImage().equals(""))
                continue;

            String descri;
            if(item.getSize().equals("") || item.getSize().equals("未知"))
                descri = "";
            else
                descri = item.getSize();

            final CardProvider provider = new Card.Builder(mContext)
                    .setTag(item)
                    .withProvider(new CardProvider<>())
                    .setLayout(R.layout.material_basic_image_buttons_card_layout)
                    .setTitle(item.getName())
                    .setTitleGravity(Gravity.START)
                    .setDescription(descri)
                    .setDescriptionGravity(Gravity.START)
                    .setDrawable(item.getImage())
                    .setDrawableConfiguration(new CardProvider.OnImageConfigListener() {
                        @Override
                        public void onImageConfigure(@NonNull RequestCreator requestCreator) {
                            requestCreator.fit();
                        }
                    })
                    .addAction(R.id.right_text_button, new TextViewAction(mContext)
                            .setText("×  " + lineItem.getNum())
                            .setTextResourceColor(R.color.black_button)
                            )
                    .addAction(R.id.left_text_button, new TextViewAction(mContext)
                            .setText(item.realPrice()+"元")

                            .setTextResourceColor(R.color.orange_button)
                            );
            provider.setDividerVisible(false);
            Card card = provider.endConfig().build();
            mListView.getAdapter().add(card);
        }
    }

    private void initRefresh(){
        //设置刷新时动画的颜色，可以设置4个
        sr_swipeMaterialListView.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);

        sr_swipeMaterialListView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // TODO Auto-generated method stub
                EventBus.getDefault().post(new MessageEvent("刷新购物车"));
                //获取信息，然后再刷新UI
                String temp = "http://155o554j78.iok.la:49817/";
                String args = "Flash-buy/cart?cartNumber=9&userId=9";

                temp += args;
                EventBus.getDefault().post(new InternetEvent(temp, Constant.REQUEST_Cart));

                //先清空所有
                mListView.getAdapter().clearAll();
                init();

                //最后再把刷新取消
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {


                        sr_swipeMaterialListView.setRefreshing(false);

                    }
                });
            }//onRefresh
        });
    }

    // 开始自动翻页
    @Override
    public void onResume() {
        super.onResume();
    }

    // 停止自动翻页
    @Override
    public void onPause() {
        super.onPause();
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
