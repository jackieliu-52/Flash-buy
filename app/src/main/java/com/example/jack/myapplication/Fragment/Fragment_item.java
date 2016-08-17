package com.example.jack.myapplication.Fragment;

import android.content.Context;
import android.net.Uri;
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
import android.widget.Toast;

import com.example.jack.myapplication.Model.Item;
import com.example.jack.myapplication.R;
import com.example.jack.myapplication.Util.Event.MessageEvent;
import com.example.jack.myapplication.Util.Util;
import com.facebook.common.util.UriUtil;
import com.facebook.drawee.view.SimpleDraweeView;
import com.github.mrengineer13.snackbar.SnackBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 展示物品的页面
 * 本来一开始是单例模式，但考虑它会经常刷新页面，没有必要考虑单例模式
 * 但是要注意资源的释放
 */
public class Fragment_item extends Fragment {

    private final String TAG = "Fragment_item";
    public static Item item;  //当前展示的Item
    private Context mContext;
    private SimpleDraweeView sd_good;
    private TextView tv_name;
    private ImageView iv_star;
    private TextView tv_company;
    private TextView tv_source;
    private TextView tv_size;


    @Override
    public void onAttach(Context context){
        Log.i(TAG,"onAttach");
        super.onAttach(context);
        this.mContext = context;
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
        int resId = Util.stringToId(mContext,"you");
        Uri uri = new Uri.Builder()
                .scheme(UriUtil.LOCAL_RESOURCE_SCHEME) // "res"
                .path(String.valueOf(resId))
                .build();
        //刷新UI
        sd_good.setImageURI(uri);
        tv_name.setText(item.getName());
        tv_company.setText(item.getCompany());
        tv_size.setText(item.getSize());
        tv_source.setText(item.getSource());

        //新建一个菜单
        setHasOptionsMenu(true);

        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.i(TAG, "onCreateOptionsMenu()");
        menu.clear();
        inflater.inflate(R.menu.menu_item, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public String toString(){
        return TAG;
    }


}
