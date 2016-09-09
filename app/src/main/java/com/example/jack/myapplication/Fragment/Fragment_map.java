package com.example.jack.myapplication.Fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jack.myapplication.Model.iBeaconView;
import com.example.jack.myapplication.R;
import com.example.jack.myapplication.Util.Event.MessageEvent;
import com.litesuits.common.utils.ClipboardUtil;
import com.litesuits.common.utils.DisplayUtil;
import com.litesuits.common.utils.NotificationUtil;
import com.litesuits.common.utils.VibrateUtil;
import com.onlylemi.mapview.library.MapView;
import com.onlylemi.mapview.library.MapViewListener;
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
public class Fragment_map extends android.support.v4.app.Fragment {
    final private String TAG = "Fragment_map";
    private Context mContext;

    private static final SKYRegion ALL_SEEKCY_BEACONS_REGION = new SKYRegion("rid_all", null, null, null, null);
    private SKYBeaconManager skyBeaconManager;

    public static List<Double> distances = new ArrayList<>(); //与各个ibeacon之间的距离
    private static final Double MAX = Double.MAX_VALUE;  //不能检测到距离

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
    private List<PointF> beacons; //所有beacons的坐标
    private Timer timer = null;
    private TimerTask timerTask = null;

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        mContext = context;
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

                        List<Integer> routeList = MapUtils.getShortestDistanceBetweenTwoPoints
                                (marks.get(4), target, nodes, nodesContract);
                        routeLayer.setNodeList(nodes);
                        routeLayer.setRouteList(routeList);
                        mapView.refresh();
                    }
                });

                //定位的点
                locationLayer = new LocationLayer(mapView, new PointF(650, 760));
                locationLayer.setOpenCompass(true);
                locationLayer.setCompassIndicatorCircleRotateDegree(60);  //罗盘
                locationLayer.setCompassIndicatorArrowRotateDegree(-30);  //方向
                mapView.addLayer(locationLayer);

                mapView.refresh();   //draw地图
            }

            @Override
            public void onMapLoadFail() {
            }
        });

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
        for(int i = 0 ;i < 3;i++){
            distances.add(MAX);
        }
        SKYBeaconManager.getInstance().init(mContext);
        SKYBeaconManager.getInstance().setCacheTimeMillisecond(3000);
        SKYBeaconManager.getInstance().setScanTimerIntervalMillisecond(2000);
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.i(TAG,"onResume");
        startRanging();   //开始扫描
        startTimer();  //开始计时器
    }
    @Override
    public void onPause(){
        super.onPause();
        Log.i(TAG,"onPause");
        stopRanging();  //关闭扫描
        stopTimer();  //关闭计时器
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            //相当于Fragment的onResume
            Log.i(TAG,"v");
        } else {
            //相当于Fragment的onPause
            Log.i(TAG,"in");
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
                double d2 = distances.get(0);
                double d3 = distances.get(0);
                boolean flag = true;
                flag = ((d1 == MAX) && (d2 == MAX) && (d3 == MAX));
                if(flag){
                    //开始定位

                }
            }
        };
        timer.schedule(timerTask, 0, 10000);  //10s进行一次定位操作
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
                    switch (((SKYBeacon) beaconList.get(i)).getMinor()){
                        case 1:
                            distances.set(0,distance);
                            break;
                        case 2:
                            distances.set(1,distance);
                            break;
                        case 3:
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

    private void stopRanging() {
        SKYBeaconManager.getInstance().stopScanService();
        SKYBeaconManager.getInstance().stopRangingBeasons(null);
    }

}
