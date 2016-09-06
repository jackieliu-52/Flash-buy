package com.example.jack.myapplication.Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.jack.myapplication.Model.BulkItem;
import com.example.jack.myapplication.Model.LineItem;
import com.example.jack.myapplication.R;
import com.example.jack.myapplication.Util.Util;
import com.facebook.common.util.UriUtil;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

/**
 * 每一个商品的适配器
 * Created by Jack on 2016/8/11.
 */
public class ItemAdapter extends ArrayAdapter<LineItem> {
    private List<LineItem> items;
    private int resId;
    private Context mContext;

    public ItemAdapter(Context context, int resId, List<LineItem> objects){
        super(context,resId,objects);
        mContext = context;
        items = objects;
        this.resId = resId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LineItem item = getItem(position); //得到Item
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

        int picId = Util.stringToId(mContext,item.getItem().getImage());
        Uri uri = new Uri.Builder()
                .scheme(UriUtil.LOCAL_RESOURCE_SCHEME) // "res"
                .path(String.valueOf(picId))
                .build();

        Uri temp =  Uri.parse(item.getItem().getImage());
        viewHolder.simpleDraweeView.setImageURI(temp);
        viewHolder.tv_good_name.setText(item.getItem().getName());
        if(item.isBulk){
            viewHolder.tv_num.setText("   ×  " + ((BulkItem)item.getItem()).getWeight() +"kg");
        }
        else{
            viewHolder.tv_num.setText("   ×  " + item.getNum());
        }
        viewHolder.item_1price.setText("单价："+item.getItem().realPrice());
        viewHolder.item_all_price.setText("总价："+item.getUnitPrice());
        return view;
    }

    class ViewHolder{

        private SimpleDraweeView simpleDraweeView;
        private TextView tv_good_name;
        private TextView tv_num;
        private TextView item_1price;
        private TextView item_all_price;

        public ViewHolder(View view){
            simpleDraweeView = (SimpleDraweeView) view.findViewById(R.id.list_item_view);
            tv_good_name = (TextView) view.findViewById(R.id.tv_good_name);
            tv_num = (TextView) view.findViewById(R.id.tv_num);
            item_1price = (TextView) view.findViewById(R.id.item_1price);
            item_all_price = (TextView) view.findViewById(R.id.item_all_price);
        }
    }
}
