package com.example.jack.myapplication.Util.Event;

import com.example.jack.myapplication.Model.Item;

/**
 * Created by Jack on 2016/8/2.
 */
public class MessageEvent {
    public final String message;
    public  Item item = null;
    public MessageEvent(String message) {
        this.message = message;
    }

    public MessageEvent(String message,Item item){this.message=message; this.item = item;}
}
