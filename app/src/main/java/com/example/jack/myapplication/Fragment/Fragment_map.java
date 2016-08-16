package com.example.jack.myapplication.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jack.myapplication.R;

/**
 * 因为虽然这边用viewpager去管理，而且指示器用的是弱引用去管理，但是还是不需要
 * 用单例模式去管理（其实也不知道怎么管理，除非换一个其他指示器库）
 * 因为将它放在三个pager的中间，一开始在看第一个pager的时候就已经开始预加载了，
 * 同时因为Fragment_buy是单例模式，所以这个fragment相当于一个单例模式（很奇怪）
 * Created by Jack on 2016/8/5.
 */
public class Fragment_map extends android.support.v4.app.Fragment {
    final private String TAG = "Fragment_map";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        Log.i(TAG,"onCreateView");
        return inflater.inflate(R.layout.fragment_map, container, false);
    }
    @Override
    public void onResume(){
        super.onResume();
        Log.i(TAG,"onResume");
    }
    @Override
    public void onPause(){
        super.onPause();
        Log.i(TAG,"onPause");
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            //相当于Fragment的onResume
        } else {
            //相当于Fragment的onPause
        }
    }
}
