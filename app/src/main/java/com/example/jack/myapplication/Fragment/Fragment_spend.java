package com.example.jack.myapplication.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jack.myapplication.R;

/**
 * 显示这个月花费了多少钱
 */
public class Fragment_spend extends Fragment {
    private static Fragment_spend instance = null;
    /**
     * 对外接口
     *
     * @return Fragment_spend
     */
    public static Fragment_spend GetInstance() {
        if (instance == null)
            instance = new Fragment_spend();
        return instance;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_spend, container, false);
        return view;
    }

}
