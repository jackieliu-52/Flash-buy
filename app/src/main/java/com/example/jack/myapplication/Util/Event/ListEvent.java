package com.example.jack.myapplication.Util.Event;

import android.view.View;

/**
 * Created by Jack on 2016/8/10.
 */
public class ListEvent {
    public final String message;

    public View mView;

    public ListEvent(String message) {
        this.message = message;
    }

    public ListEvent(String message, View view) {
        this.message = message;
        mView = view;
    }
}
