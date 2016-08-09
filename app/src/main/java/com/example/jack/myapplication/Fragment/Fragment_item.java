package com.example.jack.myapplication.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jack.myapplication.R;

/**
 * Created by Jack on 2016/8/8.
 */
public class Fragment_item extends Fragment {
    private final String TAG = "Fragment_item";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        Log.i(TAG,"onCreateView");
        return inflater.inflate(R.layout.fragment_item, container, false);
    }
}
