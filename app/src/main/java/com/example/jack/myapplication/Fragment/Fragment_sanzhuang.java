package com.example.jack.myapplication.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jack.myapplication.R;
import com.jawnnypoo.physicslayout.PhysicsFrameLayout;

/**
 * 散装商品页面
 */
public class Fragment_sanzhuang extends Fragment {


    private static Fragment_sanzhuang instance = null;
    private Context mContext;
    //散装商品的数量
    int num;


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

        return view;
    }

}
