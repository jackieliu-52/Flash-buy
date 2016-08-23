package com.example.jack.myapplication.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.card.CardProvider;
import com.dexafree.materialList.card.action.TextViewAction;
import com.dexafree.materialList.view.MaterialListView;
import com.example.jack.myapplication.MainActivity;
import com.example.jack.myapplication.Model.Item;
import com.example.jack.myapplication.Model.LineItem;
import com.example.jack.myapplication.R;
import com.example.jack.myapplication.Util.Constant;
import com.example.jack.myapplication.Util.Event.InternetEvent;
import com.example.jack.myapplication.Util.Event.MessageEvent;
import com.example.jack.myapplication.View.FloatingBall.FloatBall;
import com.example.jack.myapplication.View.FloatingBall.FloatBallMenu;
import com.squareup.picasso.RequestCreator;

import org.greenrobot.eventbus.EventBus;

import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

/**
 * 购物清单
 */
public class Fragment_cart extends android.support.v4.app.Fragment {
    private  final String TAG = "Fragment_cart";
    Context mContext;
    MaterialListView mListView;
    public static int first = 1;

    //悬浮窗口，不需要权限
    private FloatBallMenu menu;
    private FloatBall.SingleIcon singleIcon;
    private FloatBall mFloatBall;

    SwipeRefreshLayout sr_swipeMaterialListView;
    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        mListView = (MaterialListView) view.findViewById(R.id.material_cart_list);
        sr_swipeMaterialListView = (SwipeRefreshLayout) view.findViewById(R.id.swipe_MaterialListView);

        mListView.setItemAnimator(new SlideInLeftAnimator());
        mListView.getItemAnimator().setAddDuration(300);
        mListView.getItemAnimator().setRemoveDuration(300);
        init();
        initRefresh();

        menu = new FloatBallMenu();
        singleIcon = new FloatBall.SingleIcon(R.drawable.shop_basket, 1f, 0.3f);
        mFloatBall = new FloatBall.Builder(mContext.getApplicationContext()).menu(menu).icon(singleIcon).build();
        mFloatBall.setLayoutGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        mFloatBall.show();
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
    @Override
    public void onDestroyView(){
        super.onDestroyView();
        mFloatBall.dismiss();
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            //相当于Fragment的onResume
            Log.i(TAG,"可见");
            first++;

        } else {
            //相当于Fragment的onPause
            Log.i(TAG,"不可见");

        }
    }
}
