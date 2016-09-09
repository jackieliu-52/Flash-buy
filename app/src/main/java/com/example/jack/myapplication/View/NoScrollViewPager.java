package com.example.jack.myapplication.View;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 在处理地图的时候发现有点滑动手势会有点影响体验
 * 自定义viewpager禁止滑动
 */
public class NoScrollViewPager extends ViewPager {

    private boolean mScrollble = false; //禁止滑动


    public NoScrollViewPager(Context context) {
        super(context);
    }

    public NoScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!mScrollble) {
            return false;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!mScrollble) {
            return false;
        }
        return super.onInterceptTouchEvent(ev);
    }

    public boolean isCanScrollble() {
        return mScrollble;
    }

    public void setCanScrollble(boolean scrollble) {
        this.mScrollble = scrollble;
    }
}
