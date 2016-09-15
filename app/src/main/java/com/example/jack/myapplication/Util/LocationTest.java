package com.example.jack.myapplication.Util;

import android.graphics.PointF;
import android.util.Log;

import com.example.jack.myapplication.Model.Round;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * beacon定位工具类
 */
public class LocationTest {
    public static String TAG = "Location";

    /**
     * 三角形质心定位算法实现
     * Triangle centroid location
     * @param r1 坐标1为圆心,距离为半径
     * @param r2
     * @param r3
     * @return
     */
    public static PointF tcl(Round r1, Round r2, Round r3) {
        PointF p1 = null;// 有效交叉点1
        PointF p2 = null;// 有效交叉点2
        PointF p3 = null;// 有效交叉点3
        PointF zx=new PointF();//计算三点质心
        List<PointF> jds1 = jd(r1.getX(), r1.getY(), r1.getR(), r2.getX(), r2.getY(), r2.getR());// r1,r2交点
        if (jds1 != null && !jds1.isEmpty()) {
            for (PointF jd : jds1) {//有交点
                if (p1==null&&Math.pow(jd.x-r3.getX(),2) + Math.pow(jd.y-r3.getY(),2) <= Math.pow(r3.getR(),2)) {
                    p1 = jd;
                }else if(p1!=null){
                    if(Math.pow(jd.x-r3.getX(),2) + Math.pow(jd.y-r3.getY(),2)<= Math.pow(r3.getR(),2)){
                        if(Math.sqrt(Math.pow(jd.x-r3.getX(),2) + Math.pow(jd.y-r3.getY(),2))>Math.sqrt(Math.pow(p1.x-r3.getX(),2) + Math.pow(p1.y-r3.getY(),2))){
                            p1 = jd;
                        }
                    }
                }
            }
        } else {//没有交点定位错误
            Log.i(TAG,"1 。 2 error");
            return null;
        }
        List<PointF> jds2 = jd(r1.getX(), r1.getY(), r1.getR(), r3.getX(), r3.getY(), r3.getR());// r1,r3交点
        if (jds2 != null && !jds2.isEmpty()) {
            for (PointF jd : jds2) {//有交点
                if (p2==null&&Math.pow(jd.x-r2.getX(),2) + Math.pow(jd.y-r2.getY(),2) <= Math.pow(r2.getR(),2)) {
                    p2 = jd;

                }else if(p2!=null){
                    if(Math.pow(jd.x-r2.getX(),2) + Math.pow(jd.y-r2.getY(),2) <= Math.pow(r2.getR(),2)){
                        if(Math.pow(jd.x-r2.getX(),2) + Math.pow(jd.y-r2.getY(),2)>Math.sqrt(Math.pow(p2.x-r2.getX(),2) + Math.pow(p2.y-r2.getY(),2))){
                            p2 = jd;
                        }
                    }
                }
            }
        } else {//没有交点定位错误
            Log.i(TAG,"1 。 3 error");
            return null;
        }
        List<PointF> jds3 = jd(r2.getX(), r2.getY(), r2.getR(), r3.getX(), r3.getY(), r3.getR());// r2,r3交点
        if (jds3 != null && !jds3.isEmpty()) {
            for (PointF jd : jds3) {//有交点
                if (Math.pow(jd.x-r1.getX(),2) + Math.pow(jd.y-r1.getY(),2) <= Math.pow(r1.getR(),2)) {
                    p3 = jd;
                }else if(p3!=null){
                    if(Math.pow(jd.x-r1.getX(),2) + Math.pow(jd.y-r1.getY(),2) <= Math.pow(r1.getR(),2)){
                        if(Math.pow(jd.x-r1.getX(),2) + Math.pow(jd.y-r1.getY(),2)>Math.sqrt(Math.pow(p3.x-r1.getX(),2) + Math.pow(p3.y-r1.getY(),2))){
                            p3 = jd;
                        }
                    }
                }
            }
        } else {//没有交点定位错误
            Log.i(TAG,"2 。 3 error");
            return null;
        }
        Log.i(TAG,"success");
        if(p1 == null || p2 == null || p3 == null){
            Log.i(TAG,"error");
            return  null;
        }

        zx.x=(p1.x+p2.x+p3.x)/3; //质心
        zx.y=(p1.y+p2.y+p3.y)/3;
        return zx;
    }
    
    /**
     * 计算两个圆的交点
     * @param x1
     * @param y1
     * @param r1
     * @param x2
     * @param y2
     * @param r2
     * @return
     */
    public static List<PointF> jd(float x1, float y1, float r1, float x2, float y2, float r2) {

        Map<Integer, float[]> p = new HashMap<>();
        float d = (float)Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));// 两圆心距离
        if (Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2)) < (r1 + r2)) {// 两圆向交

        }
        List<PointF>points  =new ArrayList<PointF>();//交点坐标
        PointF coor;
        if (d > r1 + r2 || d < Math.abs(r1 - r2)) {//相离或内含
            return null;
        } else if (x1 == x2 && y1 == y2) {//同心圆
            return null;// 同心圆 )
        }
        else if (y1 == y2 && x1 != x2) {
            float a = ((r1 * r1 - r2 * r2) - (x1 * x1 - x2 * x2)) / (2 * x2 - 2 * x1);
            if (d == Math.abs(r1 - r2) || d == r1 + r2) {// 只有一个交点时
                coor=new PointF();
                coor.x=a;
                coor.y=y1;
                points.add(coor);
            } else{// 两个交点
                float t = r1 * r1 - (a - x1) * (a - x1);
                coor=new PointF();
                coor.x=a;
                coor.y= y1 + (float)Math.sqrt(t);
                points.add(coor);
                coor=new PointF();
                coor.x=a;
                coor.y= y1 - (float)Math.sqrt(t);
                points.add(coor);
            }
        } else if (y1 != y2) {
            float k, disp;
            k = (2 * x1 - 2 * x2) / (2 * y2 - 2 * y1);
            disp = ((r1 * r1 - r2 * r2) - (x1 * x1 - x2 * x2) - (y1 * y1 - y2 * y2)) / (2 * y2 - 2 * y1);// 直线偏移量
            float a, b, c;
            a = (k * k + 1);
            b = (2 * (disp - y1) * k - 2 * x1);
            c = (disp - y1) * (disp - y1) - r1 * r1 + x1 * x1;
            float disc;
            disc = b * b - 4 * a * c;// 一元二次方程判别式
            if (d == Math.abs(r1 - r2) || d == r1 + r2) {
                coor=new PointF();
                coor.x=(-b) / (2 * a);;
                coor.y= k * coor.x + disp;
                points.add(coor);
            } else {
                coor=new PointF();
                coor.x= (float)((-b) + Math.sqrt(disc)) / (2 * a);
                coor.y= k * coor.x + disp;
                points.add(coor);
                coor=new PointF();
                coor.x= (float)((-b) - Math.sqrt(disc)) / (2 * a);
                coor.y= k * coor.x + disp;
                points.add(coor);
            }
        }

        return points;
    }
}
