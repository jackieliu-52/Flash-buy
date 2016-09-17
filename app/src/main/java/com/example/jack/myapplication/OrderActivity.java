package com.example.jack.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.dd.CircularProgressButton;
import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.card.CardProvider;
import com.dexafree.materialList.card.action.TextViewAction;
import com.dexafree.materialList.view.MaterialListAdapter;
import com.dexafree.materialList.view.MaterialListView;
import com.example.jack.myapplication.Fragment.Fragment2;
import com.example.jack.myapplication.Fragment.Fragment_buy;
import com.example.jack.myapplication.Model.BulkItem;
import com.example.jack.myapplication.Model.Item;
import com.example.jack.myapplication.Model.LineItem;
import com.example.jack.myapplication.Model.Order;
import com.example.jack.myapplication.Model.TwoTuple;
import com.example.jack.myapplication.Util.Event.MessageEvent;
import com.example.jack.myapplication.View.Recyclerview.CommonRecyclerAdapter;
import com.example.jack.myapplication.View.Recyclerview.CommonRecyclerViewHolder;
import com.example.jack.myapplication.View.SmoothCheckBox;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

/**
 * 订单的Activity
 */
public class OrderActivity extends AppCompatActivity {
    @BindView(R.id.order_id)
    TextView mOrderId;
    @BindView(R.id.order_status)
    TextView mOrderStatus;
    @BindView(R.id.order_money)
    TextView mOrderMoney;
    @BindView(R.id.order_list)
    MaterialListView mOrderList;
    @BindView(R.id.content_request_btn)
    CircularProgressButton mContentRequestBtn;

    private Toolbar toolbar;

    private Context mContext;
    private Order mOrder;
    private MaterialListAdapter mListAdapter;
    private List<TwoTuple<Boolean,BulkItem>> mItemList;  //散装商品

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_order);
        ButterKnife.bind(this);
        // Handle Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("订单详情");   //设置标题
        //set the back arrow in the toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent().getParcelableExtra("order") != null) {
            mOrder = getIntent().getParcelableExtra("order");
        }
        init();


        mContentRequestBtn.setIndeterminateProgressMode(true);
        mContentRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mContentRequestBtn.getProgress() == 0){
                    mContentRequestBtn.setProgress(100);   //支付完成
                    showDialog();
                    EventBus.getDefault().post(new MessageEvent("支付成功！"));
                }else{
                    mContentRequestBtn.setProgress(-1);   //支付失败
                    EventBus.getDefault().post(new MessageEvent("支付成功！"));
                }
            }
        });
    }
    /**
     * 这是兼容的 AlertDialog
     */
    private void showDialog() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(mContext);
        builder.setTitle("支付成功！");
        builder.setMessage("您是否需要导入散装商品？");
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mItemList = new ArrayList<>();
                for(LineItem lineItem:mOrder.getLineItems()){
                    if(lineItem.isBulk){
                        mItemList.add(new TwoTuple<>(false,(BulkItem) lineItem.getItem())); //加入
                    }
                }
                if(mItemList.size() != 0){
                    CommonRecyclerAdapter<TwoTuple<Boolean,BulkItem>> adapter;
                    adapter = new CommonRecyclerAdapter<TwoTuple<Boolean, BulkItem>>(mContext,R.layout.plan_items,mItemList) {
                        @Override
                        public void convert(final CommonRecyclerViewHolder holder, TwoTuple<Boolean, BulkItem> o) {
                            Item item = o.second;
                            ((TextView)holder.getView(R.id.tv_item_name)).setTextAppearance(mContext,R.style.PlanTextStyle);
                            holder.setText(R.id.tv_item_name, item.getName());

                            //加载图片
                            Picasso.with(mContext)
                                    .load(item.getImage())
                                    .placeholder(R.drawable.ic_launcher)
                                    .into(((ImageView) holder.getView(R.id.ci_image)));

                            final SmoothCheckBox scb = (SmoothCheckBox) holder.getView(R.id.scb);

                            holder.setOnClickListener(R.id.tv_item_name, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    int pos = holder.getLayoutPosition();  //获得下标
                                    mItemList.get(pos).first = !mItemList.get(pos).first;
                                    scb.setChecked(mItemList.get(pos).first, true);//播放动画
                                }
                            });

                            holder.getView(R.id.v_color).setVisibility(View.GONE);  //去除
                            holder.getView(R.id.tv_item_date).setVisibility(View.GONE);  //去除

                            scb.setChecked(o.first);
                            scb.setOnCheckedChangeListener(new SmoothCheckBox.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(SmoothCheckBox checkBox, boolean isChecked) {
                                    int pos = holder.getLayoutPosition();  //获得下标
                                    mItemList.get(pos).first = isChecked;
                                    //提升一个视觉的差异
                                    if(isChecked) {
                                        holder.getView(R.id.v_color).setVisibility(View.INVISIBLE);
                                    }
                                    else {
                                        holder.getView(R.id.v_color).setVisibility(View.GONE);
                                    }
                                }
                            });
                        }
                    };

                    new MaterialDialog.Builder(mContext)
                            .title("生鲜蔬菜")
                            .adapter(adapter, null)
                            .positiveText("加入购物车")
                            .negativeText("取消")
                            .negativeColorRes(R.color.orange_button)
                            .onAny(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    if(which == DialogAction.POSITIVE){
                                        //被选中的物品加入购物车
                                        for(TwoTuple<Boolean,BulkItem> o: mItemList){
                                            if(o.first) {
                                                BulkItem copyOne = o.second;
                                                Fragment2.myItems.add(new TwoTuple<>(false,copyOne));   //加入购物车
                                            }
                                        }
                                        mItemList.clear();  //清空整个列表
                                    }
                                }
                            })
                            .show();
                }else {
                    EventBus.getDefault().post(new MessageEvent("您没有可以导入的散装商品"));
                }
            }
        });
        builder.show();
    }

    private void init() {
        if (mOrder.getOrderId() != null)
            mOrderId.setText("订单号：" + mOrder.getOrderId());
        mOrderStatus.setText(mOrder.getStatus() == 1 ? "完成" : "未完成");
        mOrderMoney.setText("总金额：" + mOrder.getPayment() + "元");

        mOrderList.setItemAnimator(new SlideInLeftAnimator());
        mOrderList.getItemAnimator().setAddDuration(300);
        mOrderList.getItemAnimator().setRemoveDuration(300);

        for (LineItem lineItem : mOrder.getLineItems()) {
            fillList(lineItem);
        }
    }

    private void fillList(LineItem lineItem) {
        Item item = lineItem.getItem();

        //因为加了一个LineItem，所以有点bug要处理
        if (item.getName().equals(""))
            return;

        String descri;
        if (item.getSize().equals("") || item.getSize().equals("未知"))
            descri = "未知";
        else
            descri = item.getSize();

        final CardProvider provider = new Card.Builder(this)
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
                .addAction(R.id.right_text_button, new TextViewAction(this)
                        .setText("×  " + lineItem.getNum())
                        .setTextResourceColor(R.color.black_button)
                )
                .addAction(R.id.left_text_button, new TextViewAction(this)
                        .setText(item.realPrice() + "元")

                        .setTextResourceColor(R.color.orange_button)
                );
        provider.setDividerVisible(false);
        Card card = provider.endConfig().build();
        mOrderList.getAdapter().add(card);

    }
}
