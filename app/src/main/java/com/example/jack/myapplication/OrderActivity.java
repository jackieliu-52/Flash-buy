package com.example.jack.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.CircularProgressButton;
import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.card.CardProvider;
import com.dexafree.materialList.card.action.TextViewAction;
import com.dexafree.materialList.view.MaterialListAdapter;
import com.dexafree.materialList.view.MaterialListView;
import com.example.jack.myapplication.Model.Item;
import com.example.jack.myapplication.Model.LineItem;
import com.example.jack.myapplication.Model.Order;
import com.example.jack.myapplication.Util.Event.MessageEvent;
import com.squareup.picasso.RequestCreator;

import org.greenrobot.eventbus.EventBus;

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
                    EventBus.getDefault().post(new MessageEvent("支付成功！"));
                }else{
                    mContentRequestBtn.setProgress(-1);   //支付失败
                    EventBus.getDefault().post(new MessageEvent("支付成功！"));
                }
            }
        });
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
