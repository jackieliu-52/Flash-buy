package com.example.jack.myapplication.Behavior;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.AttributeSet;
import android.view.View;


public class MyBehavior extends CoordinatorLayout.Behavior {
    private int toolbarHeight;

    public MyBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.toolbarHeight = 45;
    }


    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return (dependency instanceof Snackbar.SnackbarLayout) || (dependency instanceof AppBarLayout);
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View fab, View dependency) {
        boolean returnValue = super.onDependentViewChanged(parent, fab, dependency);
        if (dependency instanceof AppBarLayout) {
            CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
            int fabBottomMargin = lp.bottomMargin;
            int distanceToScroll = fab.getHeight() + fabBottomMargin;
            float ratio = (float)dependency.getY()/(float)toolbarHeight;
            fab.setTranslationY(-distanceToScroll * ratio);
        }
        return returnValue;
    }
}
