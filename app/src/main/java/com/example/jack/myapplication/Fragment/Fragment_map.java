package com.example.jack.myapplication.Fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.jack.myapplication.Model.Round;
import com.example.jack.myapplication.Model.iBeaconView;
import com.example.jack.myapplication.R;
import com.example.jack.myapplication.Util.Event.MessageEvent;
import com.example.jack.myapplication.Util.Event.PlanBuyEvent;
import com.example.jack.myapplication.Util.Location;
import com.litesuits.common.utils.ClipboardUtil;
import com.litesuits.common.utils.DisplayUtil;
import com.litesuits.common.utils.NotificationUtil;
import com.litesuits.common.utils.VibrateUtil;
import com.onlylemi.mapview.library.MapView;
import com.onlylemi.mapview.library.MapViewListener;
import com.onlylemi.mapview.library.layer.BitmapLayer;
import com.onlylemi.mapview.library.layer.LocationLayer;
import com.onlylemi.mapview.library.layer.MarkLayer;
import com.onlylemi.mapview.library.layer.RouteLayer;
import com.onlylemi.mapview.library.test.TestData;
import com.onlylemi.mapview.library.utils.MapUtils;
import com.skybeacon.sdk.RangingBeaconsListener;
import com.skybeacon.sdk.ScanServiceStateCallback;
import com.skybeacon.sdk.locate.SKYBeacon;
import com.skybeacon.sdk.locate.SKYBeaconManager;
import com.skybeacon.sdk.locate.SKYRegion;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 因为虽然这边用viewpager去管理，而且指示器用的是弱引用去管理，但是还是不需要
 * 用单例模式去管理（其实也不知道怎么管理，除非换一个其他指示器库）
 * 因为将它放在三个pager的中间，一开始在看第一个pager的时候就已经开始预加载了，
 * 同时因为Fragment_buy是单例模式，所以这个fragment相当于一个单例模式（很奇怪）
 * Created by Jack on 2016/8/5.
 */
public class Fragment_map extends android.support.v4.app.Fragment  implements SensorEventListener  {
    final private String TAG = "Fragment_map";
    private Context mContext;

    private static final SKYRegion ALL_SEEKCY_BEACONS_REGION = new SKYRegion("rid_all", null, null, null, null);
    private SKYBeaconManager skyBeaconManager;

    public static List<Double> distances = new ArrayList<>(); //与各个ibeacon之间的距离
    private static final Double MAX = Double.MAX_VALUE;  //不能检测时的距离

    private MapView mapView;

    private MarkLayer markLayer;
    private RouteLayer routeLayer;
    private LocationLayer locationLayer;

    private List<PointF> nodes;    //辅助点
    private List<PointF> nodesContract; //哪些辅助点到哪些辅助点之间是可达的
    private List<PointF> marks;   //货品的点
    private List<String> marksName;  //货品名称
    public static List<Boolean> chosed = TestData.getChosed();

    private PointF location; //当前定位的坐标
    private List<iBeaconView> beacons; //所有beacons
    private List<Round> mRounds;  //所有的rounds

    private Timer timer = null;
    private TimerTask timerTask = null;
    private boolean visible; //是否可见
    private boolean openSensor = true; //是否打开传感器,默认打开
    private SensorManager sensorManager; //传感器


    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        mContext = context;
    }
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        Log.i(TAG,"onCreateView");
        initBeacon();
        initMapDatas(); //初始化地图数据

        mapView = (MapView) view.findViewById(R.id.mapview);
        loadMap();

        //震动测试
  //    VibrateUtil.vibrate(mContext,1000);


        return view;
    }

    /**
     * 加载地图
     */
    private void loadMap(){
        Bitmap bitmap = null;

        try {
            bitmap = BitmapFactory.decodeStream(mContext.getAssets().open("test.png"));
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG,"get map error[picture]");
        }
        mapView.loadMap(bitmap);
        mapView.setMapViewListener(new MapViewListener() {
            @Override
            public void onMapLoadSuccess() {
                routeLayer = new RouteLayer(mapView);
                mapView.addLayer(routeLayer);

                markLayer = new MarkLayer(mapView, marks, marksName);
                markLayer.setChosed(chosed);   //被选中的mark字体变红

                mapView.addLayer(markLayer);

                //点击函数
                markLayer.setMarkIsClickListener(new MarkLayer.MarkIsClickListener() {
                    @Override
                    public void markIsClick(int num) {
                        PointF target = new PointF(marks.get(num).x, marks.get(num).y);       //获得被触摸的点

                        chosed.set(num,true);  //设置被选中
                        //marks.get(4)为起点
                        List<Integer> routeList = MapUtils.getShortestDistanceBetweenTwoPoints
                                (marks.get(4), target, nodes, nodesContract);
                        routeLayer.setNodeList(nodes);
                        routeLayer.setRouteList(routeList);
                        mapView.refresh();
                    }
                });

                //绘制beacon
                Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                BitmapLayer bitmapLayer = new BitmapLayer(mapView, bmp);
                bitmapLayer.setLocation(beacons.get(0).location);
                mapView.addLayer(bitmapLayer);

                mapView.refresh();   //draw地图
            }

            @Override
            public void onMapLoadFail() {
            }
        });
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
    }
    /**
     * 加载地图数据
     */
    private void initMapDatas(){
        nodes = TestData.getNodesList();
        nodesContract = TestData.getNodesContactList();
        marks = TestData.getMarks();    //点
        marksName = TestData.getMarksName();

        MapUtils.init(nodes.size(), nodesContract.size());
    }

    /**
     * 初始化距离，设置beacon管理
     */
    private void initBeacon(){
        beacons = new ArrayList<>();
        mRounds = new ArrayList<>();
        for(int i = 0 ;i < 3;i++){
            distances.add(MAX);
        }
        iBeaconView iBeaconView1 = new iBeaconView();
        iBeaconView1.location = new PointF(524,326);
        iBeaconView iBeaconView2 = new iBeaconView();
        iBeaconView2.location = new PointF(122,471);
        iBeaconView iBeaconView3 = new iBeaconView();
        iBeaconView3.location = new PointF(108,192);
        beacons.add(iBeaconView1);
        beacons.add(iBeaconView2);
        beacons.add(iBeaconView3);
        //圆的初始化
        mRounds.add(new Round(iBeaconView1.location.x,iBeaconView1.location.y,(float)MAX.doubleValue()));
        mRounds.add(new Round(iBeaconView2.location.x,iBeaconView2.location.y,(float)MAX.doubleValue()));
        mRounds.add(new Round(iBeaconView3.location.x,iBeaconView3.location.y,(float)MAX.doubleValue()));

        SKYBeaconManager.getInstance().init(mContext);
        SKYBeaconManager.getInstance().setCacheTimeMillisecond(3000);
        SKYBeaconManager.getInstance().setScanTimerIntervalMillisecond(2000);
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.i(TAG,"onResume");
        startRanging();   //开始扫描
    }
    @Override
    public void onPause(){
        super.onPause();
        Log.i(TAG,"onPause");
        stopRanging();  //关闭扫描
        stopTimer();  //关闭计时器
        sensorManager.unregisterListener(this); //关闭传感器
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            //相当于Fragment的onResume
            Log.i(TAG,"v");
            visible = true;
        } else {
            //相当于Fragment的onPause
            Log.i(TAG,"in");
            visible = false;
        }
    }

    private void startTimer() {
        stopTimer();
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
               //这里面进行定位操作
                double d1 = distances.get(0);
                double d2 = distances.get(1);
                double d3 = distances.get(2);
                Log.i("Location","d1: " + d1 + "米  d2:  " + d2 + "米  d3:  " + d3);
                boolean flag;
                flag = ((d1 != MAX) && (d2 != MAX) && (d3 != MAX));
                if(flag){
                    //开始定位
                    Log.i(TAG,"开始定位");
                    //beacon布置高度大概是3.5米，考虑到人的身高，所以大概是2.5米
                    //但是由于距离精读不够准确所以降低到1.8m
                    if(d1 <= 1.8){
                        location = beacons.get(0).location;
                        locationLayer.setCurrentPosition(location);
                        mapView.refresh();           //刷新
                        return;
                    }
                    if(d2 <= 1.8){
                        location = beacons.get(1).location;
                        locationLayer.setCurrentPosition(location);
                        mapView.refresh();           //刷新
                        return;
                    }
                    if(d3 <= 1.8){
                        location = beacons.get(2).location;
                        locationLayer.setCurrentPosition(location);
                        mapView.refresh();           //刷新
                        return;
                    }
                    mRounds.get(0).setR((float)Math.sqrt(Math.pow(d1,2) -Math.pow(2.5,2)));
                    mRounds.get(1).setR((float)Math.sqrt(Math.pow(d2,2) -Math.pow(2.5,2)));
                    mRounds.get(2).setR((float)Math.sqrt(Math.pow(d3,2) -Math.pow(2.5,2)));
                    //获得定位信息
                    location = Location.tcl(mRounds.get(0),mRounds.get(1),mRounds.get(2));
                    if(location != null) {
                        locationLayer.setCurrentPosition(location);
                        mapView.refresh();           //刷新
                    }
                } else{
                    Log.i(TAG,"有ibeacon没有定位信息");
                    EventBus.getDefault().post(new MessageEvent("定位失败！请检查蓝牙是否打开"));
                }
            }
        };
        timer.schedule(timerTask, 0, 2500);  //2.5s进行一次定位操作
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
    }


    private void startRanging() {
        //开始服务

        SKYBeaconManager.getInstance().startScanService(new ScanServiceStateCallback() {

            @Override
            public void onServiceDisconnected() {
                // TODO Auto-generated method stub
                Log.i(TAG,"onServiceDisconnected");
            }

            @Override
            public void onServiceConnected() {
                // TODO Auto-generated method stub
                Log.i(TAG,"onServiceConnected");
                SKYBeaconManager.getInstance().startRangingBeacons(null);
            }
        });


        SKYBeaconManager.getInstance().setRangingBeaconsListener(new RangingBeaconsListener() {
            // 单id beacons扫描结果处理
            @Override
            public void onRangedBeacons(SKYRegion beaconRegion, @SuppressWarnings("rawtypes") List beaconList) {
                // TODO Auto-generated method stub
                if(beaconList.size() == 0)
                    EventBus.getDefault().post(new MessageEvent("没有检测到蓝牙设备"));

                for (int i = 0; i < beaconList.size(); i++) {
                    iBeaconView beacon = new iBeaconView();
                    beacon.mac = ((SKYBeacon) beaconList.get(i)).getDeviceAddress();
                    beacon.rssi = ((SKYBeacon) beaconList.get(i)).getRssi();
                    beacon.isMultiIDs = false;
                    beacon.detailInfo = ((SKYBeacon) beaconList.get(i)).getProximityUUID() + "\r\nMajor: " + String.valueOf(((SKYBeacon) beaconList.get(i)).getMajor()) + "\tMinir: "
                            + String.valueOf(((SKYBeacon) beaconList.get(i)).getMinor()) + "\r\n";
                    beacon.detailInfo += "version: " + String.valueOf(((SKYBeacon) beaconList.get(i)).getHardwareVersion()) + "."
                            + String.valueOf(((SKYBeacon) beaconList.get(i)).getFirmwareVersionMajor()) + "." + String.valueOf(((SKYBeacon) beaconList.get(i)).getFirmwareVersionMinor());
                    //获得距离
                    double distance = ((SKYBeacon) beaconList.get(i)).getDistance();
                    //因为这里只有三个beacon，所以可以直接处理序号问题
                    //但是这里需要处理一下没有检测到的情况，也就是说distance为-1.0米的情况
                    switch (((SKYBeacon) beaconList.get(i)).getMinor()){
                        case 1:
                            if(distance != -1.0)
                                distances.set(0,distance);
                            break;
                        case 2:
                            if(distance != -1.0)
                                distances.set(1,distance);
                            break;
                        case 3:
                            if(distance != -1.0)
                                distances.set(2,distance);
                            break;
                    }
                }
            }
            // 多id beacons扫描结果处理，我们不适用
            @Override
            public void onRangedBeaconsMultiIDs(SKYRegion beaconRegion, @SuppressWarnings("rawtypes") List beaconMultiIDsList) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onRangedNearbyBeacons(SKYRegion beaconRegion, List beaconList) {
                // TODO Auto-generated method stub

            }
        });
    }

    /**
     * 停止扫描
     */
    private void stopRanging() {
        SKYBeaconManager.getInstance().stopScanService();
        SKYBeaconManager.getInstance().stopRangingBeasons(null);
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void plan(PlanBuyEvent planBuyEvent){
        if(planBuyEvent.message.equals("initMap")){
            //阻塞直到加载完全
            while (true){
                if(visible){

                    //设置定位的点
                    locationLayer = new LocationLayer(mapView, new PointF(650, 760));  //起点
                    locationLayer.setOpenCompass(true);
                    locationLayer.setCompassIndicatorCircleRotateDegree(60);  //罗盘
                    locationLayer.setCompassIndicatorArrowRotateDegree(-30);  //方向
                    mapView.addLayer(locationLayer);

                    //注册传感器
                    sensorManager.registerListener(this, sensorManager.getDefaultSensor
                            (Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_NORMAL);

                    mapView.refresh();   //draw地图
                    startTimer();  //开始计时器
                    break; //跳出循环
                }
            }
        }
    }

    /**
     * 根据方向传感器的值来绘制罗盘的方向
     * @param event
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (mapView.isMapLoadFinish() && openSensor && visible) {
            float mapDegree = 0; // the rotate between reality map to northern
            float degree = 0;
            if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
                degree = event.values[0];
            }

            locationLayer.setCompassIndicatorCircleRotateDegree(-degree);
            locationLayer.setCompassIndicatorArrowRotateDegree(mapDegree + mapView
                    .getCurrentRotateDegrees() + degree);
            mapView.refresh();
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}
