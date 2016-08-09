package com.example.jack.myapplication.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jack.myapplication.R;
import com.github.mrengineer13.snackbar.SnackBar;

/**
 * Created by Jack on 2016/8/8.
 */
public class Fragment_item extends Fragment {

    private final String TAG = "Fragment_item";
    private Toolbar toolbar = null;
    /**
     * 单例对象实例
     */
    private static Fragment_item instance = null;

    /**
     * 对外接口
     * @return Fragment_item
     */
    public static Fragment_item GetInstance()
    {
        if(instance == null)
            instance = new Fragment_item();
        return instance;
    }
    @Override
    public void onResume() {

        super.onResume();
        //这里可以让Fragment直接监听按钮事件
//        getView().setFocusableInTouchMode(true);
//        getView().requestFocus();
//        getView().setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//
//                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){
//
//                    // handle back button
//
//                    return true;
//
//                }
//
//                return false;
//            }
//        });
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_item, container, false);
        Log.i(TAG,"onCreateView");
        toolbar = (Toolbar) view.findViewById(R.id.item_toolbar);

        Log.i(TAG,"onCreateView1");
        //设置新的toolbar
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setHasOptionsMenu(true);

        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.e(TAG, "onCreateOptionsMenu()");
        menu.clear();
        inflater.inflate(R.menu.menu_item, menu);
    }
}
