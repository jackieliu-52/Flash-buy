package com.example.jack.myapplication.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.jack.myapplication.Model.BulkItem;
import com.example.jack.myapplication.R;
import com.example.jack.myapplication.Util.Util;
import com.jawnnypoo.physicslayout.Physics;
import com.jawnnypoo.physicslayout.PhysicsFrameLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * 散装商品页面
 */
public class Fragment_sanzhuang extends Fragment {
    private String TAG = "散装商品";

    private static Fragment_sanzhuang instance = null;
    private Context mContext;
    //散装商品的数量
    int num;
    public static ArrayList<BulkItem> items = new ArrayList<>();
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
                    items.remove(releasedView.getId()); //根据下标删除这个对象
                    num = physicsLayout.getChildCount();
                }
            }
        });
        return view;
    }

    private void init(){
        BulkItem item = new BulkItem();
        item.setName("青菜");
        item.setPrice(2.33);
        item.setWeight(1);
        item.setImage("1");
        item.setAttr1("闭光存储");
        //5天以前生产
        item.setProduceTime(Util.getBefoceTime(5));
        //获得到期时间
        item.jisuan();

        BulkItem item1 = new BulkItem();
        item1.setName("花生");
        item1.setPrice(5.00);
        item1.setWeight(2.33);
        item1.setImage("3");
        item1.setAttr1("冷藏");
        item1.setShelfTime(10);
        //19天前生产
        item1.setProduceTime(Util.getBefoceTime(19));
        item1.jisuan();

        items.add(item);
        items.add(item1);


        for(int i = 0;i < 2; i++){
            BulkItem bulkItem = items.get(i);
            ImageView imageView = new ImageView(mContext);
            LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(
                    getResources().getDimensionPixelSize(R.dimen.square_size),
                    getResources().getDimensionPixelSize(R.dimen.square_size));
            imageView.setLayoutParams(llp);
            imageView.setId(num);
            Picasso.with(mContext)
                    .load("http://lorempixel.com/200/200/cats/" + bulkItem.getImage())
                    .placeholder(R.drawable.ic_launcher)
                    .into(imageView);

            physicsLayout.addView(imageView);
            num = physicsLayout.getChildCount();
        }

    }


}
