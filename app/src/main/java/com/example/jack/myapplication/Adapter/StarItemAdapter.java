package com.example.jack.myapplication.Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jack.myapplication.Fragment.Fragment_buy;
import com.example.jack.myapplication.Model.BulkItem;
import com.example.jack.myapplication.Model.Item;
import com.example.jack.myapplication.Model.Item;
import com.example.jack.myapplication.R;
import com.example.jack.myapplication.Util.Event.MessageEvent;
import com.example.jack.myapplication.Util.Util;
import com.facebook.common.util.UriUtil;
import com.facebook.drawee.view.SimpleDraweeView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * 收藏商品的适配器
 * C
 */
public class StarItemAdapter extends ArrayAdapter<Item> {
    private List<Item> items;
    private int resId;
    private Context mContext;

    public StarItemAdapter(Context context, int resId, List<Item> objects){
        super(context,resId,objects);
        mContext = context;
        items = objects;
        this.resId = resId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        final Item item = getItem(position); //得到Item
        View view;
        ViewHolder viewHolder;
        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resId,null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }else {
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();
        }
        
        Uri temp =  Uri.parse(item.getImage());
        viewHolder.simpleDraweeView.setImageURI(temp);
        viewHolder.tv_good_name.setText(item.getName());
        viewHolder.add_2_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment_buy.planItems.add(item);
                Toast toast= Toast.makeText(mContext,
                        item.getName() + "已加入购物计划", Toast.LENGTH_SHORT);
                //放在左上角。如果你想往右边移动，将第二个参数设为>0；往下移动，增大第三个参数；后两个参数都是像素
                toast.setGravity(Gravity.TOP| Gravity.LEFT, 100, 100);
                toast.show();
            }
        });
        return view;
    }

    class ViewHolder{

        private SimpleDraweeView simpleDraweeView;
        private TextView tv_good_name;
        private TextView add_2_cart;

        public ViewHolder(View view){
            simpleDraweeView = (SimpleDraweeView) view.findViewById(R.id.list_item_image);
            tv_good_name = (TextView) view.findViewById(R.id.tv_gd_name);
            add_2_cart = (TextView) view.findViewById(R.id.add_2_cart);
        }
    }
}
