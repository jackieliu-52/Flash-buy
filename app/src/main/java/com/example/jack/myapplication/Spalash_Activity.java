package com.example.jack.myapplication;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.example.jack.myapplication.Util.Util;
import com.facebook.common.util.UriUtil;
import com.facebook.drawee.view.SimpleDraweeView;


/**
 * 开场动画
 */
public class Spalash_Activity extends AppCompatActivity {

    SimpleDraweeView dvSpalash;

    TextView tvAuthor;

    private Context mContext;
    private Animation animation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spalash);
        dvSpalash = (SimpleDraweeView) findViewById(R.id.dv_spalash);
        tvAuthor = (TextView) findViewById(R.id.tv_author) ;
        mContext = Spalash_Activity.this;
        init();
    }

    private void init() {
        initAnimation();
    }

    private void initAnimation() {
        animation = AnimationUtils.loadAnimation(this, R.anim.anim_splash);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                startActivity(new Intent(mContext, LogInActivity.class));
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        int resId = Util.stringToId(mContext,"spalash_pic");
        Uri uri = new Uri.Builder()
                .scheme(UriUtil.LOCAL_RESOURCE_SCHEME) // "res"
                .path(String.valueOf(resId))
                .build();
        dvSpalash.setImageURI(uri);
        tvAuthor.setText("jack");
        //启动动画
        dvSpalash.startAnimation(animation);
    }

}
