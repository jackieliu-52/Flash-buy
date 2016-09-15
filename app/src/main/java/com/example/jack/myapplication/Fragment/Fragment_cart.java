package com.example.jack.myapplication.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.card.CardProvider;
import com.dexafree.materialList.card.action.TextViewAction;
import com.dexafree.materialList.view.MaterialListAdapter;
import com.dexafree.materialList.view.MaterialListView;
import com.example.jack.myapplication.MainActivity;
import com.example.jack.myapplication.Model.BulkItem;
import com.example.jack.myapplication.Model.Item;
import com.example.jack.myapplication.Model.LineItem;
import com.example.jack.myapplication.R;
import com.example.jack.myapplication.ScanActivity;
import com.example.jack.myapplication.Util.Constant;
import com.example.jack.myapplication.Util.Event.InternetEvent;
import com.example.jack.myapplication.Util.Event.MessageEvent;
import com.example.jack.myapplication.Util.InternetUtil;
import com.example.jack.myapplication.View.FloatingBall.FloatBall;
import com.example.jack.myapplication.View.FloatingBall.FloatBallMenu;
import com.squareup.picasso.RequestCreator;

import org.greenrobot.eventbus.EventBus;

import java.util.Timer;
import java.util.TimerTask;

import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

/**
 * 购物清单
 */
public class Fragment_cart extends android.support.v4.app.Fragment {
    private  final String TAG = "Fragment_cart";
    Context mContext;
    MaterialListView mListView;
    private MaterialListAdapter mListAdapter;

    public static int first = 1;   //是否是第一次进入,保存用户操作状态使用
    static boolean isMutiList = false;  //是否是多重列表
    //悬浮窗口，不需要权限
    private FloatBallMenu menu;
    private FloatBall.SingleIcon singleIcon;   //图标
    private FloatBall mFloatBall;

    SwipeRefreshLayout sr_swipeMaterialListView;


    private Timer timer = null;
    private TimerTask timerTask = null;

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
        mListAdapter = mListView.getAdapter();

        initRefresh();

        menu = new FloatBallMenu();
        singleIcon = new FloatBall.SingleIcon(R.drawable.shop_basket, 1f, 0.3f);
        mFloatBall = new FloatBall.Builder(mContext.getApplicationContext()).menu(menu).icon(singleIcon).width(100).height(100).build();
        mFloatBall.setLayoutGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL | Gravity.TOP);
        mFloatBall.show();


        //新建一个菜单
        setHasOptionsMenu(true);
        //如果不是第一次进入，那么保存用户的习惯，比如说商品排列方式
        if(first != 1){
            getActivity().invalidateOptionsMenu(); //重新绘制menu
        }

        first++;
        return view;
    }


    private void init() {
        for(LineItem lineItem: MainActivity.cart){
            Item item = lineItem.getItem();
            String num;

            //因为加了一个LineItem，所以有点bug要处理
            if(item.getName().equals(""))
                continue;

            String descri;
            if(item.getSize().equals("") || item.getSize().equals("未知"))
                descri = "未知";
            else
                descri = item.getSize();

            if (lineItem.isBulk){
                num = ((BulkItem)lineItem.getItem()).getWeight() +"kg";
                descri = ((BulkItem)lineItem.getItem()).getProduceTime() + "生产";
            } else{
                num = lineItem.getNum() + "";
            }
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
                            .setText("×  " + num)
                            .setTextResourceColor(R.color.black_button)
                    )
                    .addAction(R.id.left_text_button, new TextViewAction(mContext)
                            .setText(item.realPrice()+" 元")

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
                if(!MainActivity.TESTMODE) {
                    //获取信息，然后再刷新UI
                    EventBus.getDefault().post(new InternetEvent(InternetUtil.cartUrl, Constant.REQUEST_Cart));
                    EventBus.getDefault().post(new InternetEvent(InternetUtil.bulkUrl,Constant.REQUEST_Bulk));
                }

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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_cart, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if(!isMutiList){
            mListView.setLayoutManager(new StaggeredGridLayoutManager(1,
                    StaggeredGridLayoutManager.VERTICAL));
            menu.findItem(R.id.diff_mode).setIcon(R.drawable.ic_view_module_white_24dp);
        }else {
            mListView.setLayoutManager(new StaggeredGridLayoutManager(2,
                    StaggeredGridLayoutManager.VERTICAL));
            menu.findItem(R.id.diff_mode).setIcon(R.drawable.ic_view_stream_black_24dp);
        }
        super.onPrepareOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //handle the click on the back arrow click
        switch (item.getItemId()) {
            case R.id.diff_mode:
                if(!isMutiList){
                    isMutiList = true;
                    //设置两列
                    mListView.setLayoutManager(new StaggeredGridLayoutManager(2,
                            StaggeredGridLayoutManager.VERTICAL));
                    item.setIcon(R.drawable.ic_view_stream_black_24dp);
                }else {
                    isMutiList = false;
                    //设置为单列
                    mListView.setLayoutManager(new StaggeredGridLayoutManager(1,
                            StaggeredGridLayoutManager.VERTICAL));
                    item.setIcon(R.drawable.ic_view_module_white_24dp);

                }

                return true;
            case R.id.scan1:
                Intent intent =new Intent(getActivity(),ScanActivity.class);
                intent.putExtra(Constant.REQUEST_SCAN_MODE, Constant.REQUEST_SCAN_MODE_ALL_MODE);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            //相当于Fragment的onResume
            Log.i(TAG,"可见");
            startTimer();
        } else {
            //相当于Fragment的onPause
            Log.i(TAG,"不可见");
            stopTimer();
        }
    }

    private void startTimer() {
        stopTimer();
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                if(!MainActivity.TESTMODE) {
                    //获取信息，然后再刷新UI
                    EventBus.getDefault().post(new InternetEvent(InternetUtil.cartUrl, Constant.REQUEST_Cart));
                    EventBus.getDefault().post(new InternetEvent(InternetUtil.bulkUrl,Constant.REQUEST_Bulk));
                }
                //先清空所有
                mListView.getAdapter().clearAll();
                init();
            }
        };
        timer.schedule(timerTask,5000); //5s刷新一次
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
    }
}
