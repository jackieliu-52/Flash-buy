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
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jack.myapplication.R;
import com.facebook.drawee.view.SimpleDraweeView;
import com.github.mrengineer13.snackbar.SnackBar;

/**
 * Created by Jack on 2016/8/8.
 */
public class Fragment_item extends Fragment {

    private final String TAG = "Fragment_item";
    private SimpleDraweeView sd_good;
    private TextView tv_name;
    private ImageView iv_star;
    private TextView tv_company;
    private TextView tv_source;
    private TextView tv_size;
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
        sd_good = (SimpleDraweeView) view.findViewById(R.id.sd_good);
        tv_name = (TextView) view.findViewById(R.id.tv_name);
        iv_star = (ImageView) view.findViewById(R.id.iv_star);
        tv_company = (TextView) view.findViewById(R.id.tv_company);
        tv_source = (TextView) view.findViewById(R.id.tv_source);
        tv_size = (TextView) view.findViewById(R.id.tv_size);


        //新建一个菜单
        setHasOptionsMenu(true);

        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.e(TAG, "onCreateOptionsMenu()");
        menu.clear();
        inflater.inflate(R.menu.menu_item, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }
}
