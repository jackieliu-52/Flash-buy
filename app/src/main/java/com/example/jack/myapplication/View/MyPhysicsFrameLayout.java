package com.example.jack.myapplication.View;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;

import com.jawnnypoo.physicslayout.PhysicsFrameLayout;

/**
 * 改造轮子
 */
public class MyPhysicsFrameLayout extends PhysicsFrameLayout {

    @Override
    protected  void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);

    }
    public MyPhysicsFrameLayout(Context context) {
        super(context);
    }

    public MyPhysicsFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyPhysicsFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    @TargetApi(21)
    public MyPhysicsFrameLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
