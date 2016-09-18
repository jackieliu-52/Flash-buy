package com.example.jack.myapplication.Fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jack.myapplication.MainActivity;
import com.example.jack.myapplication.Model.BulkItem;
import com.example.jack.myapplication.Model.LineItem;
import com.example.jack.myapplication.R;
import com.jawnnypoo.physicslayout.Physics;
import com.jawnnypoo.physicslayout.PhysicsFrameLayout;
import com.jawnnypoo.physicslayout.PhysicsLinearLayout;
import com.squareup.picasso.Picasso;

import cn.timeface.widget.drawabletextview.DrawableTextView;

/**
 * 散装商品页面
 */
public class Fragment_sanzhuang extends Fragment {
    private String TAG = "散装商品";

    private static Fragment_sanzhuang instance = null;
    private Context mContext;
    //散装商品的数量
    int num;

    @Override
    public void onStart(){
        super.onStart();
        Log.i(TAG,"onStart");
    }

    @Override
    public void onStop(){
        super.onStop();
        Log.i(TAG,"onStop");
    }

    @Override
    public void onAttach(Context mContext){
        super.onAttach(mContext);
        this.mContext = mContext;
    }
    public static Fragment_sanzhuang GetInstance() {
        if (instance == null)
            instance = new Fragment_sanzhuang();
        return instance;
    }


    PhysicsFrameLayout physicsLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sanzhuang, container, false);
        num = 0;
        physicsLayout = (PhysicsFrameLayout) view.findViewById(R.id.physics_layout);
        //物品可以拖动
        physicsLayout.getPhysics().enableFling();

        init();
        num = physicsLayout.getChildCount();

        physicsLayout.getPhysics().setOnFlingListener(new Physics.OnFlingListener(){
            @Override
            public void onGrabbed(View grabbedView){

            }
            @Override
            public void onReleased(View releasedView){
                //这里最好使用绝对坐标，因为getTop()在FrameLayout中是有问题
                int[] lo = new  int[2] ;
                releasedView.getLocationOnScreen(lo);//获取在整个屏幕内的绝对坐标
       //         Log.i(TAG,"view "+ lo [0] +" "+ lo[1]);

                //经过试验测量得出
                if( lo[1] < 650){
                    physicsLayout.removeView(releasedView);
//                    MainActivity.cart.remove( MainActivity.bulkItems.get(releasedView.getId()));
                    MainActivity.bulkItems.remove(releasedView.getId()); //根据下标删除这个对象

//                    //先把散装商品全部剔除
//                    for(LineItem i:MainActivity.cart){
//                        if(i.isBulk)
//                            MainActivity.cart.remove(i);
//                    }
//
//                    //把散装商品添加入cart
//                    for(BulkItem i:MainActivity.bulkItems){
//                        MainActivity.cart.add(new LineItem(i));
//                    }
                    Log.i(TAG,releasedView.getId() + "被删除了！");
                    num = physicsLayout.getChildCount();
                }
            }
        });
        return view;
    }

    private void init(){
//        for(LineItem lineItem: MainActivity.cart){
//            if(lineItem.isBulk)
//                MainActivity.bulkItems.add((BulkItem) lineItem.getItem());
//        }
        for(BulkItem bulkItem : MainActivity.bulkItems){
            LinearLayout physicsLinearLayout = new LinearLayout(mContext);
            physicsLinearLayout.setId(num);              //设置ID，方便后面删除
            physicsLinearLayout.setOrientation(LinearLayout.VERTICAL);
            //设置LinearLayout属性(宽和高)
            LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            //将以上的属性赋给LinearLayout
            physicsLinearLayout.setLayoutParams(layoutParams);


            LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(
                    getResources().getDimensionPixelSize(R.dimen.square_size),
                    getResources().getDimensionPixelSize(R.dimen.square_size));

            TextView textView = new TextView(mContext);
            textView.setText(bulkItem.getName());
            textView.setTextSize(14);
            textView.setLayoutParams(layoutParams);

            ImageView imageView = new ImageView(mContext);

            imageView.setLayoutParams(llp);


            Picasso.with(mContext)
                    .load(bulkItem.getImage())
                    .placeholder(R.drawable.ic_launcher)
                    .into(imageView);

            physicsLinearLayout.addView(imageView);
            physicsLinearLayout.addView(textView);

            physicsLayout.addView(physicsLinearLayout);
            num = physicsLayout.getChildCount();
        }

    }


}
