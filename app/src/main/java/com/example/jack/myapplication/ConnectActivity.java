package com.example.jack.myapplication;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

import com.example.jack.myapplication.Util.Event.MessageEvent;
import com.example.jack.myapplication.Util.InternetUtil;
import com.skyfishjy.library.RippleBackground;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

/**
 * 连接手机时动画的Activity
 */
public class ConnectActivity extends AppCompatActivity {
    private Context mContext;
    private ImageView foundDevice;
    private RippleBackground rippleBackground;
    private ConnectTask mConnectTask = null;
    String text;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_connect);

        Intent intent = getIntent();
        text = intent.getExtras().getString("1");


        String[] temp = text.split(":");
        text = temp[2];
        mContext = this;
        foundDevice=(ImageView)findViewById(R.id.foundDevice);
        rippleBackground = (RippleBackground)findViewById(R.id.content);

        rippleBackground.startRippleAnimation(); //开始动画效果
//        foundDevice();
        mConnectTask = new ConnectTask();  //开始异步任务
        mConnectTask.execute((Void)null);
    }


    private void foundDevice(){
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(400);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        ArrayList<Animator> animatorList=new ArrayList<Animator>();
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(foundDevice, "ScaleX", 0f, 1.2f, 1f);
        animatorList.add(scaleXAnimator);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(foundDevice, "ScaleY", 0f, 1.2f, 1f);
        animatorList.add(scaleYAnimator);
        animatorSet.playTogether(animatorList);
        foundDevice.setVisibility(View.VISIBLE);
        animatorSet.start();
    }


    public class ConnectTask extends AsyncTask<Void, Void, Boolean> {


        //post数据给服务器
        @Override
        protected Boolean doInBackground(Void... params) {
            try{
                Thread.sleep(3000);
            }catch (Exception e){
                e.printStackTrace();
            }
            return InternetUtil.postStr(" ",InternetUtil.args3 + text +"&userId=9&password=9");   //发送给服务器
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if(success){
                rippleBackground.stopRippleAnimation(); //结束动画
                EventBus.getDefault().post(new MessageEvent("绑定成功！"));
                finish();
            }else{
                rippleBackground.stopRippleAnimation(); //结束动画
                EventBus.getDefault().post(new MessageEvent("信息不能传输给服务器"));
                finish();
            }
        }
    }
}
