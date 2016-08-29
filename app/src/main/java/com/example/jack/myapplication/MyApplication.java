package com.example.jack.myapplication;

import android.app.Application;
import android.os.Environment;

import com.avos.avoscloud.AVOSCloud;
import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.litesuits.common.utils.DisplayUtil;

import java.io.File;

/**
 * 初始化
 * Created by Jack on 2016/8/8.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate(){
        super.onCreate();
       // Fresco.initialize(this);
        AVOSCloud.initialize(this,"FjLwORtACC64HFAKWNvA4Nh7-gzGzoHsz","5LTRklNRhPEbAFLh8SayIpaW");

        DiskCacheConfig diskCacheConfig = DiskCacheConfig.newBuilder(this)
                .setBaseDirectoryPath(new File(Environment.getExternalStorageDirectory().getAbsoluteFile(),"Jack"))
                .setBaseDirectoryName("fresco_disk")
                .setMaxCacheSize(200*1024*1024)//200MB
                .build();
        ImagePipelineConfig imagePipelineConfig = ImagePipelineConfig.newBuilder(this)
                .setMainDiskCacheConfig(diskCacheConfig)
                .build();
        Fresco.initialize(this, imagePipelineConfig);

        //打印屏幕信息
        DisplayUtil.printDisplayInfo(this);

    }
}
