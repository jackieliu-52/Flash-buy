package com.example.jack.myapplication.View.Transition;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.transition.ChangeBounds;
import android.transition.ChangeImageTransform;
import android.transition.ChangeTransform;
import android.transition.TransitionSet;
import android.util.AttributeSet;

/**
 * 转换动画
 *
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class ItemTransition extends TransitionSet {
    public ItemTransition() {
        init();
    }

    // 允许资源文件使用
    public ItemTransition(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * ChangeBounds: 视图的位置和大小;
     * ChangeTransform: 视图的比例(scale);
     * ChangeImageTransform: 图像的比例;
     */
    private void init() {
        setOrdering(ORDERING_TOGETHER);
        addTransition(new ChangeBounds()).
                addTransition(new ChangeTransform()).
                addTransition(new ChangeImageTransform());
    }
}
