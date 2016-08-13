package com.example.jack.myapplication.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.jack.myapplication.R;
import com.example.jack.myapplication.Util.Event.MessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by Jack on 2016/8/1.
 */
public class Fragment2 extends android.support.v4.app.Fragment{
    /**
     * 单例对象实例
     */
    private static Fragment2 instance = null;

    /**
     * 对外接口
     * @return Fragment2
     */
    public static Fragment2 GetInstance()
    {
        if(instance == null)
            instance = new Fragment2();
        return instance;
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(getUserVisibleHint()){
            Log.i("F2","可见");
        }
        else {
            Log.i("F2","不可见");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        Log.i("F2","onCreate");
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        Log.i("F2","onCreateView");
        return inflater.inflate(R.layout.fragment_2, container, false);
    }
    @Override
    public void onStart() {
        super.onStart();

        Log.i("F2","onStart");
    }
    @Override
    public void onResume() {
        super.onResume();
        Log.i("F2","onResume");
    }
    @Override
    public void onPause() {
        super.onPause();
        Log.i("F2","onPause");
    }
    @Override
    public void onStop() {
        Log.i("F2","onStop");
        super.onStop();
    }
    @Override
    public void onDestroyView(){
        Log.i("F2","onDestroyView");
        super.onDestroyView();
    }
    @Override
    public void onDestroy(){
        Log.i("F2","onDestroy");
        super.onDestroy();
    }

    @Override
    public void onDetach(){
        Log.i("F2","onDetach");
        super.onDetach();
        instance = null;
    }
    @Override
    public String toString()
    {
        return "f2";
    }


}
