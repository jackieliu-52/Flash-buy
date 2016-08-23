package com.example.jack.myapplication.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jack.myapplication.R;
import com.litesuits.common.utils.ClipboardUtil;
import com.litesuits.common.utils.DisplayUtil;
import com.litesuits.common.utils.NotificationUtil;
import com.litesuits.common.utils.VibrateUtil;

/**
 * 因为虽然这边用viewpager去管理，而且指示器用的是弱引用去管理，但是还是不需要
 * 用单例模式去管理（其实也不知道怎么管理，除非换一个其他指示器库）
 * 因为将它放在三个pager的中间，一开始在看第一个pager的时候就已经开始预加载了，
 * 同时因为Fragment_buy是单例模式，所以这个fragment相当于一个单例模式（很奇怪）
 * Created by Jack on 2016/8/5.
 */
public class Fragment_map extends android.support.v4.app.Fragment {
    final private String TAG = "Fragment_map";
    private Context mContext;

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        mContext = context;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        Log.i(TAG,"onCreateView");

        //获得剪贴板最后一条信息
        Log.i(TAG,ClipboardUtil.getLatestText(mContext));
        //震动测试
  //    VibrateUtil.vibrate(mContext,1000);
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
            Log.i(TAG,"v");
        } else {
            //相当于Fragment的onPause
            Log.i(TAG,"in");
        }
    }
}
