package com.example.jack.myapplication.Fragment;

import android.content.Context;
import android.support.v4.app.Fragment;

/**
 * BaseFragment，使用懒加载
 */
public  class BaseFragment extends Fragment {
    protected Context mContext;
    protected boolean isVisible;

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        mContext = context;
    }

}
