package com.example.jack.myapplication.Fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.graphics.RectF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import com.example.jack.myapplication.Model.Item;
import com.example.jack.myapplication.Model.Round;
import com.example.jack.myapplication.Model.iBeaconView;
import com.example.jack.myapplication.R;
import com.example.jack.myapplication.Util.Event.MessageEvent;
import com.example.jack.myapplication.Util.Event.PlanBuyEvent;
import com.example.jack.myapplication.Util.Location.NonLinearLeastSquaresSolver;
import com.example.jack.myapplication.Util.Location.TrilaterationFunction;
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
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer.Optimum;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
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

    //圆心和距离
    double[][] positions;
    double[] dts;
    RectF mRectF,mRectF1,mRectF2,mRectF3,mRectF4; //四个矩形，用于防止判定定位在货架上

    //测试
    private Button mButton; //打印指纹
    private int times;  //打印次数
    private boolean test; //测试
    private TimerTask mTimerTask; //测试


    public static List<Integer> nums = new ArrayList<>();  //各个区域的数量
    BitmapLayer bitmapLayer1;
    BitmapLayer bitmapLayer2;
    BitmapLayer bitmapLayer3;
    Bitmap bmp1;
    Bitmap bmp2;
    Bitmap bmp3;

    static {
        for(int i =0;i < 3;i++){
            nums.add(0);
        }
    }
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
        mButton = (Button) view.findViewById(R.id.bt_test);
        setButtonListen();
        loadMap();

        loadMapDetails();
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
            bitmap = BitmapFactory.decodeStream(mContext.getAssets().open("map.png"));
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

//                //绘制beacon
//                Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
//                BitmapLayer bitmapLayer = new BitmapLayer(mapView, bmp);
//                bitmapLayer.setLocation(beacons.get(0).location);
//                mapView.addLayer(bitmapLayer);

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
     * 预购商品
     */
    private void loadMapDetails(){
        bmp3 =  BitmapFactory.decodeResource(getResources(), R.mipmap.n0);
        bmp1 =  BitmapFactory.decodeResource(getResources(), R.mipmap.n0);
        bmp2 =  BitmapFactory.decodeResource(getResources(), R.mipmap.n0);
        bitmapLayer1 = new BitmapLayer(mapView, bmp1);
        bitmapLayer1.setLocation(new PointF(485,477));
        bitmapLayer2 = new BitmapLayer(mapView, bmp2);
        bitmapLayer2.setLocation(new PointF(175,552));
        bitmapLayer3 = new BitmapLayer(mapView, bmp3);
        bitmapLayer3.setLocation(new PointF(175,250));

        mapView.addLayer(bitmapLayer1);
        mapView.addLayer(bitmapLayer2);
        mapView.addLayer(bitmapLayer3);
        mapView.refresh();   //draw地图
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
        positions = new double[][] { {524,326}, {122,471},{108,192}}; //三个beacon的位置
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

        mRectF = new RectF(0,0,750,760);    //整体
        mRectF1 = new RectF(120,0,175,600);  //左1
        mRectF2 = new RectF(176,0,230,600);  //左2
        mRectF3 = new RectF(430,50,485,650);  //右1
        mRectF4 = new RectF(486,50,540,650); //右2

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
                //首先看看还有那些商品没有被购买
                setMapDetail();
               //这里面进行定位操作
                double d1 = distances.get(0);
                double d2 = distances.get(1);
                double d3 = distances.get(2);
                dts = new double[] {d1 * 100,d2 * 100,d3 * 100};  //距离
                //Log.i("Location","d1: " + d1 + "米  d2:  " + d2 + "米  d3:  " + d3);
                boolean flag;
                flag = ((d1 != MAX) && (d2 != MAX) && (d3 != MAX));
                if(flag){
                    //开始定位
                    Log.i(TAG,"开始定位");
                    //beacon布置高度大概是3.5米，考虑到人的身高，所以大概是2.5米左右


                    //使用非线性最小二乘法获得定位结果
                    NonLinearLeastSquaresSolver solver = new NonLinearLeastSquaresSolver(new TrilaterationFunction(positions, dts), new LevenbergMarquardtOptimizer());
                    Optimum optimum = solver.solve();

                    // 获得定位点
                    double[] centroid = optimum.getPoint().toArray();


                    if(centroid != null) {
                        Log.i(TAG,"sucess");
                        if(!mRectF.contains((float)centroid[0],(float)centroid[1])){
                            //如果定位结果超出地图的范围，那么不绘制
                            return;
                        }
                        location = new PointF((float) centroid[0],(float) centroid[1]);

                        //如果定位在货架上
                        if(mRectF1.contains((float)centroid[0],(float)centroid[1])){
                            //如果在第一个矩形中
                            location = new PointF(55,299);
                        }
                        if(mRectF2.contains((float)centroid[0],(float)centroid[1])){
                            //如果在第一个矩形中
                            location = new PointF(270,300);
                        }
                        if(mRectF3.contains((float)centroid[0],(float)centroid[1])){
                            //如果在第一个矩形中
                            location = new PointF(400,300);
                        }
                        if(mRectF4.contains((float)centroid[0],(float)centroid[1])){
                            //如果在第一个矩形中
                            location = new PointF(640,300);
                        }
                    } else{
                        Log.i(TAG,"error 233");
                    }

                    //如果有定位信息，就进行定位
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
                    beacon.uuid = ((SKYBeacon) beaconList.get(i)).getMinor();
                    //获得距离
                    double distance = ((SKYBeacon) beaconList.get(i)).getDistance();


                    //因为这里只有三个beacon，所以可以直接处理序号问题
                    //但是这里需要处理一下没有检测到的情况，也就是说distance为-1.0米的情况
                    switch (beacon.uuid){
                        case 1:
                            if(distance != -1.0) {
                                beacons.set(0,beacon);
                                distances.set(0, distance);
                            }
                            break;
                        case 2:
                            if(distance != -1.0) {
                                distances.set(1, distance);
                                beacons.set(1,beacon);
                            }
                            break;
                        case 3:
                            if(distance != -1.0) {
                                beacons.set(2,beacon);
                                distances.set(2, distance);
                            }
                            break;
                    }
                }//for

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


                    startTimer();  //开始定位

                    setMapDetail();
                    mapView.refresh();   //draw地图
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

    /**
     * 设置预购商品
     */
    private void setMapDetail(){
        //遍历
        for(Item item:Fragment_buy.planItems){
            switch (item.getPid()){
                case "01":
                    nums.set(0,nums.get(0) + 1);
                    break;
                case "06":
                    nums.set(1,nums.get(1) + 1);
                    break;
                case "09":
                    nums.set(2,nums.get(2) + 1);
                    break;
            }
        }
        switch (nums.get(0)){
            case 1:
                bmp1 =  BitmapFactory.decodeResource(getResources(), R.mipmap.n1);
                break;
            case 2:
                bmp1 =  BitmapFactory.decodeResource(getResources(), R.mipmap.n2);
                break;
            case 3:
                bmp1 =  BitmapFactory.decodeResource(getResources(), R.mipmap.n3);
                break;
            case 4:
                bmp1 =  BitmapFactory.decodeResource(getResources(), R.mipmap.n4);
                break;
            default:
                bmp1 =  BitmapFactory.decodeResource(getResources(), R.mipmap.n0);
        }
        switch (nums.get(1)){
            case 1:
                bmp2 =  BitmapFactory.decodeResource(getResources(), R.mipmap.n1);
                break;
            case 2:
                bmp2 =  BitmapFactory.decodeResource(getResources(), R.mipmap.n2);
                break;
            case 3:
                bmp2 =  BitmapFactory.decodeResource(getResources(), R.mipmap.n3);
                break;
            case 4:
                bmp2 =  BitmapFactory.decodeResource(getResources(), R.mipmap.n4);
                break;
            default:
                bmp2 =  BitmapFactory.decodeResource(getResources(), R.mipmap.n0);
        }
        switch (nums.get(2)){
            case 1:
                bmp3 =  BitmapFactory.decodeResource(getResources(), R.mipmap.n1);
                break;
            case 2:
                bmp3 =  BitmapFactory.decodeResource(getResources(), R.mipmap.n2);
                break;
            case 3:
                bmp3 =  BitmapFactory.decodeResource(getResources(), R.mipmap.n3);
                break;
            case 4:
                bmp3 =  BitmapFactory.decodeResource(getResources(), R.mipmap.n4);
                break;
            default:
                bmp3 =  BitmapFactory.decodeResource(getResources(), R.mipmap.n0);
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**
     * 测试获得指印
     */
    private void setButtonListen(){
        test = false;
        times = 1;
        timer = new Timer();
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final List<Integer> rssis1 = new ArrayList<>();
                final List<Double> dis1 = new ArrayList<>();
                final List<Integer> rssis2 = new ArrayList<>();
                final List<Double> dis2 = new ArrayList<>();
                final List<Integer> rssis3 = new ArrayList<>();
                final List<Double> dis3 = new ArrayList<>();


                mTimerTask = new TimerTask() {
                    @Override
                    public void run() {

                        printFingerprint(rssis1,rssis2,rssis3,dis1,dis2,dis3);


                        //50的倍数
                        if(times % 50 == 0){
                            int rsum1 = 0;
                            double dsum1 = 0;
                            int rsum2 = 0;
                            double dsum2 = 0;
                            int rsum3 = 0;
                            double dsum3 = 0;
                            for(int i = 0; i< rssis1.size();i++){
                                rsum1 += rssis1.get(i);
                            }
                            for(int i = 0; i< dis1.size();i++){
                                dsum1 += dis1.get(i);
                            }
                            Log.i("rssi","1 avg:"+ (rsum1 * 1.0) / (rssis1.size() * 1.0) );
                            Log.i("dis","1 avg:"+ dsum1 / dis1.size());


                            for(int i = 0; i< rssis2.size();i++){
                                rsum2 += rssis2.get(i);
                            }
                            for(int i = 0; i< dis2.size();i++){
                                dsum2 += dis2.get(i);
                            }
                            Log.i("rssi","2 avg:"+ (rsum2 * 1.0) / (rssis2.size() * 1.0));
                            Log.i("dis","2 avg:"+ dsum2 / dis2.size());


                            for(int i = 0; i< rssis3.size();i++){
                                rsum3 += rssis3.get(i);
                            }
                            for(int i = 0; i< dis3.size();i++){
                                dsum3 += dis3.get(i);
                            }
                            Log.i("rssi","3 avg:"+ (rsum3 * 1.0) / (rssis3.size() * 1.0));
                            Log.i("dis","3 avg:"+ dsum3 / dis3.size());

                            //同时应该要清空List
                            rssis1.clear();
                            rssis2.clear();
                            rssis3.clear();
                            dis1.clear();
                            dis2.clear();
                            dis3.clear();
                            if(mTimerTask != null) {
                                mTimerTask.cancel();
                            }
                            EventBus.getDefault().post(new MessageEvent("success!"));



                        }//if == 50
                        times++;
                    }
                };

                timer.schedule(mTimerTask, 0, 500);  //0.5s进行指纹打印

            }
        });
    }

    /**
     * 打印指印
     */
    public void printFingerprint(List l1,List l2,List l3,List d1,List d2,List d3){
        //打印第几次了，预计每个位置打印50次取平均值
      //  Toast.makeText(mContext,"第" + times +"次打印",Toast.LENGTH_SHORT).show();
        for(int i = 0; i < 3 ; i++){
            iBeaconView beacon = beacons.get(i);
            double distance = distances.get(i);
            if(beacon.rssi != -1) {
                // Log.i("DEBUGLOCATION"+times, beacon.uuid + "||" +beacon.rssi + "||" + distance);

                switch (beacon.uuid){
                    case 1:
                        l1.add(beacon.rssi);
                        d1.add(distance);
                        break;
                    case 2:
                        l2.add(beacon.rssi);
                        d2.add(distance);
                        break;
                    case 3:
                        l3.add(beacon.rssi);
                        d3.add(distance);
                        break;
                }

            }

        }//for
    }

}
