package com.example.jack.myapplication.Fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.card.CardProvider;
import com.dexafree.materialList.card.action.TextViewAction;
import com.dexafree.materialList.view.MaterialListView;
import com.example.jack.myapplication.Model.Item;
import com.example.jack.myapplication.R;
import com.example.jack.myapplication.Util.Util;
import com.squareup.picasso.RequestCreator;

import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

/**
 * Created by Jack on 2016/8/19.
 */
public class Fragment_search extends Fragment {
    MaterialListView mListView;
    TextView textView;
    public static List<Item> items;
    private Context mContext;
    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        mContext = context;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view =inflater.inflate(R.layout.fragment_account, container, false);

        mListView = (MaterialListView) view.findViewById(R.id.material_search);

        mListView.setItemAnimator(new SlideInLeftAnimator());
        mListView.getItemAnimator().setAddDuration(300);
        mListView.getItemAnimator().setRemoveDuration(300);
        if(items.get(0) != null){
             init();
        }else {
            mListView.setVisibility(View.INVISIBLE);
            textView = (TextView) view.findViewById(R.id.search_error);
            textView.setVisibility(View.VISIBLE);
        }
        return view;
    }

    private void init(){
        for(Item item:items){
            String image = item.getImage();
            if(image.equals("")) {
                int resId = Util.stringToId(mContext,"yibao");
                image = Uri.parse("android.resource://" + getContext().getPackageName()
                        + "/" + resId).toString();
            }
            final CardProvider provider = new Card.Builder(mContext)
                    .setTag(item)
                    .withProvider(new CardProvider<>())
                    .setLayout(R.layout.material_basic_image_buttons_card_layout)
                    .setTitle(item.getName())
                    .setTitleGravity(Gravity.START)
                    .setSubtitle(item.getCategory())
                    .setSubtitleGravity(Gravity.START)
                    .setSubtitleResourceColor(R.color.pink)
                    .setDescription(item.getSize() +"\n"+ item.getDiscount() + "折")
                    .setDescriptionGravity(Gravity.START)
                    .setDrawable(image)
                    .setDrawableConfiguration(new CardProvider.OnImageConfigListener() {
                        @Override
                        public void onImageConfigure(@NonNull RequestCreator requestCreator) {
                            requestCreator.fit();
                        }
                    })
                    .addAction(R.id.right_text_button, new TextViewAction(mContext)
                            .setText(item.isStar()? "已收藏":"未收藏")
                            .setTextResourceColor(R.color.black_button)
                    )
                    .addAction(R.id.left_text_button, new TextViewAction(mContext)
                            .setText(item.realPrice()+"元")
                            .setTextResourceColor(R.color.orange_button)
                    );
        }
    }
}
