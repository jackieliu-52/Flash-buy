package com.example.jack.myapplication.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jack.myapplication.R;

/**
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
}
