package com.example.jack.myapplication.Util.Location;

import com.example.jack.myapplication.Model.iBeaconView;

import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;

import java.util.ArrayList;
import java.util.List;

/**
 * 定位算法
 */
public class LocationHelper {
    static List<double[]> history = new ArrayList<>();  //历史数据
    static int index = 0; //下标


    static double[][] positions;
    static {
        positions = new double[][] { {524,326}, {122,471},{108,192}}; //三个beacon的位置
    }

    /**
     * 定位算法，精度大概是2-3米
     * @param beacons 设备
     * @param distances 设备到手机的距离
     * @return
     */
    public static double[] getLocation(List<iBeaconView> beacons,double[] distances){
        double[] centroid;
        double[] knnResult;
        double[] Rssi;
        //首先通过最小二乘法获取三角定位的结果，
        //结果本来应该是一个范围，但是这里只取这个范围的圆心
        NonLinearLeastSquaresSolver solver = new NonLinearLeastSquaresSolver(new TrilaterationFunction(positions, distances), new LevenbergMarquardtOptimizer());
        LeastSquaresOptimizer.Optimum optimum = solver.solve();
        centroid = optimum.getPoint().toArray();
        Rssi = convert(beacons);

        knnResult = Knn.formain(Rssi);

        double[] result = better(knnResult,centroid);
        history.add(result);
        index++;

        return result;
    }


    private static double[] convert(List<iBeaconView> beacons){
        double[] temp = new double[beacons.size()];
        for(int i = 0 ; i < beacons.size();i++)
            temp[i] = beacons.get(i).rssi;
        return temp;
    }

    /**
     * 判断两种定位结果哪种更好，
     * 这里只根据历史数据去判断，将来可以考虑综合方向传感器和加速度传感器来配合改良算法
     * @param l1 knn结果
     * @param l2 三角定位结果
     * @return
     */
    private static double[] better(double[] l1,double[] l2){
        double[] result;
        //knn没有找到好的结果，直接用三角定位结果
        if(l1[0] == -1 && l1[1] == -1)
            return l2;
        result = likely(l1) > likely(l2) ? l2 : l1;
        return result;
    }

    /**
     * 根据历史记录判断定位
     * @param location
     * @return
     */
    private static double likely(double[] location){
        double x = (location[0] - history.get(index-1)[0]);
        double y = (location[1] - history.get(index-1)[1]);
        double dis = Math.sqrt((Math.pow(x,2) + Math.pow(y,2)));
        if(dis >= 240)
            return Double.MAX_VALUE;

        return dis;
    }
}
