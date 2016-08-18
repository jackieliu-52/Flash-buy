package com.example.jack.myapplication;

import android.app.Application;

import com.avos.avoscloud.AVOSCloud;
import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * 初始化
 * Created by Jack on 2016/8/8.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate(){
        super.onCreate();
        Fresco.initialize(this);
        AVOSCloud.initialize(this,"FjLwORtACC64HFAKWNvA4Nh7-gzGzoHsz","5LTRklNRhPEbAFLh8SayIpaW");
    }
}
