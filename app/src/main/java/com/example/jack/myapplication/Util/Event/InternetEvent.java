package com.example.jack.myapplication.Util.Event;

import com.example.jack.myapplication.Util.Constant;

/**
 * Created by Jack on 2016/8/12.
 */
public class InternetEvent {
    public String message;
    public int type;     //

    public InternetEvent(String message) {
        this(message, Constant.REQUEST_INTERNET_BAR);
    }

    public InternetEvent(String message, int type) {
        this.message = message;
        this.type = type;
    }
}
