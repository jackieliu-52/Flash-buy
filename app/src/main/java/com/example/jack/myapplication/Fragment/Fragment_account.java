package com.example.jack.myapplication.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jack.myapplication.R;

/**
 * Created by Jack on 2016/8/3.
 */
public class Fragment_account extends android.support.v4.app.Fragment {
    final private String TAG = "Fragment_account";
    /**
     * 单例对象实例
     */
    private static Fragment_account instance = null;

    /**
     * 对外接口
     * @return Fragment2
     */
    public static Fragment_account GetInstance()
    {
        if(instance == null)
            instance = new Fragment_account();
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        Log.i(TAG,"onCreate");
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        Log.i(TAG,"onCreateView");
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public String toString(){
        return "fragment_account";
    }
}
