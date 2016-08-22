package com.example.jack.myapplication.Fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.card.CardProvider;
import com.dexafree.materialList.card.OnActionClickListener;
import com.dexafree.materialList.card.action.TextViewAction;
import com.dexafree.materialList.listeners.OnDismissCallback;
import com.dexafree.materialList.listeners.RecyclerItemClickListener;
import com.dexafree.materialList.view.MaterialListView;
import com.example.jack.myapplication.Model.Order;
import com.example.jack.myapplication.Model.User;
import com.example.jack.myapplication.R;
import com.litesuits.common.assist.Toastor;
import com.squareup.picasso.RequestCreator;


import java.util.Iterator;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

/**
 *
 * Created by Jack on 2016/8/5.
 */
public class Fragment_list extends android.support.v4.app.Fragment {
    final private String TAG = "Fragment_list";
    private Context mContext = null;
    private MaterialListView mListView;
    private Toastor toastor= null;

    @Override
    public void onAttach(Context context){

        super.onAttach(context);
        this.mContext = context;
        toastor = new Toastor(mContext);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_list, container, false);


        mListView = (MaterialListView) view.findViewById(R.id.material_listview);

        mListView.setItemAnimator(new SlideInLeftAnimator());
        mListView.getItemAnimator().setAddDuration(300);
        mListView.getItemAnimator().setRemoveDuration(300);
        Log.i(TAG,"onCreateView");


        for(Order order: User.getOrders()){
            fillArray(order);
        }

        // Set the dismiss listener
        mListView.setOnDismissCallback(new OnDismissCallback() {
            @Override
            public void onDismiss(@NonNull Card card, int position) {
                // Show a toast
                toastor.showSingletonToast("You have dismissed a " + card.getTag());
            }
        });

        // Add the ItemTouchListener
        mListView.addOnItemTouchListener(new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull Card card, int position) {
                toastor.showSingletonToast("单机" + card.getTag());
            }

            @Override
            public void onItemLongClick(@NonNull Card card, int position) {
                //只有被卖出的商品才可以打印发票
                boolean isSold = false;
                Iterator<Order> sListIterator = User.orders.iterator();
                while(sListIterator.hasNext()){
                    Order e = sListIterator.next();
                    if(e.getOrderId().equals(card.getTag())){
                        isSold = e.getStatus() == 1;
                    }
                }
                if(isSold) {
                    showDialog();
                }
        //        toastor.showSingletonToast("changji" + card.getTag());
            }
        });
        return view;
    }

    /**
     * 这是兼容的 AlertDialog
     */
    private void showDialog() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(mContext);
        builder.setTitle("发票打印");
        builder.setMessage("您是否要打印发票?");
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(mContext, "打印发票", Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }

    private void fillArray(final Order order) {
        if(order.getStatus() == 1) {
            final CardProvider provider = new Card.Builder(mContext)
                    .setTag(order.getOrderId())
                    .withProvider(new CardProvider<>())
                    .setLayout(R.layout.material_basic_image_buttons_card_layout)
                    .setTitle(order.getSm_name() + " ：已支付")
                    .setTitleGravity(Gravity.START)
                    .setDescription("总价： "+order.getPayment())
                    .setDescriptionGravity(Gravity.START)
                    .setDrawable(R.drawable.dog)
                    .setDrawableConfiguration(new CardProvider.OnImageConfigListener() {
                        @Override
                        public void onImageConfigure(@NonNull RequestCreator requestCreator) {
                            requestCreator.fit();
                        }
                    })
                    .addAction(R.id.left_text_button, new TextViewAction(mContext)
                            .setText("打印发票")
                            .setTextResourceColor(R.color.black_button)
                            .setListener(new OnActionClickListener() {
                                @Override
                                public void onActionClicked(View view, Card card) {
                                    //发票打印
                                    showDialog();
                                }
                            }))
                    .addAction(R.id.right_text_button, new TextViewAction(mContext)
                            .setText("查看明细")
                            .setTextResourceColor(R.color.orange_button)
                            .setListener(new OnActionClickListener() {
                                @Override
                                public void onActionClicked(View view, Card card) {
                                   //查看明细
                                }
                            }));
            provider.setDividerVisible(true);
            Card card2 = provider.endConfig().build();
            mListView.getAdapter().add(card2);
        }
        else {
            final CardProvider provider = new Card.Builder(mContext)
                    .setTag(order.getOrderId())
                    .setDismissible()
                    .withProvider(new CardProvider<>())
                    .setLayout(R.layout.material_basic_image_buttons_card_layout)
                    .setTitle(order.getSm_name() + " ：未支付")
                    .setTitleGravity(Gravity.START)
                    .setDescription("总价： "+order.getPayment())
                    .setDescriptionGravity(Gravity.START)
                    .setDrawable(R.drawable.dog)
                    .setDrawableConfiguration(new CardProvider.OnImageConfigListener() {
                        @Override
                        public void onImageConfigure(@NonNull RequestCreator requestCreator) {
                            requestCreator.fit();
                        }
                    })
                    .addAction(R.id.left_text_button, new TextViewAction(mContext)
                            .setText("支付")
                            .setTextResourceColor(R.color.black_button)
                            .setListener(new OnActionClickListener() {
                                @Override
                                public void onActionClicked(View view, Card card) {
                                    //跳转支付
                                    toastor.showSingletonToast("暂时未开发支付功能");
                                    String temp = card.getProvider().getTitle();
                                    temp = temp.substring(0,temp.length()-4);
                                    card.getProvider().setTitle(temp + "：已支付");
                                    order.setStatus(1);
                                }
                            }))
                    .addAction(R.id.right_text_button, new TextViewAction(mContext)
                            .setText("取消订单")
                            .setTextResourceColor(R.color.orange_button)
                            .setListener(new OnActionClickListener() {
                                @Override
                                public void onActionClicked(View view, Card card) {
                                    //这个
                                    toastor.showSingletonToast("您已经取消订单了"
                                            + card.getProvider().getTitle());
                                    Iterator<Order> sListIterator = User.orders.iterator();
                                    while(sListIterator.hasNext()){
                                        Order e = sListIterator.next();
                                        if(e.getOrderId().equals(card.getTag())){
                                            Log.i("取消订单",card.getTag().toString());
                                            sListIterator.remove();
                                        }
                                    }
                                    card.dismiss();
                                }
                            }));
            provider.setDividerVisible(true);
            Card card2 = provider.endConfig().build();
            mListView.getAdapter().add(card2);
        }


    }

}
