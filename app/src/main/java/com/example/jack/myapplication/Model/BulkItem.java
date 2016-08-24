package com.example.jack.myapplication.Model;

import android.util.Log;

import com.example.jack.myapplication.Util.Util;
import com.litesuits.common.utils.NumberUtil;

/**
 * 散装商品
 */
public class BulkItem {
    private String name;
    private String image; //图片
    private double price; //单价
    private int discount;  //折扣，10表示没有折扣，5表示五折
    private double weight = 0; //重量，散装商品需要用到的属性
    private int  shelfTime;  //保质期,一般是天数,计算到期日期的时候处理有点麻烦
    private String produceTime;  //生产日期
    private String endTime;   //到期日期
    private String attr1;  //特征属性：比如避光存放
    private String attr2 = ""; //保留
    private double sum; //总价
    public BulkItem() {
        this("","",0,10,0,10,"","","冷藏");
    }

    public BulkItem(String name, String image, double price, int discount, double weight, int shelfTime, String produceTime, String endTime, String attr1) {
        this.name = name;
        this.image = image;
        this.price = price;
        this.discount = discount;
        this.weight = weight;
        this.shelfTime = shelfTime;
        this.produceTime = produceTime;
        this.endTime = endTime;
        this.attr1 = attr1;
    }
    //得到到期时间和总价
    public void jisuan(){
        String[] temp = produceTime.split("/");
//        Log.i("temp",temp[0]);
        //突然发现这里有点麻烦，暂时定每个月为30号，不然还需要循环处理
        int div = shelfTime / 30;
        if(div > 1){
            //月份
            temp[1] = NumberUtil.convertToInteger(temp[1]) + div + "";
            int days = NumberUtil.convertToInteger(temp[2]) + (shelfTime%30);
            if(days > Util.Months.get(temp[1])){  //跳转下个月
                days -= Util.Months.get(temp[1]);
                //月份+1
                temp[1] = NumberUtil.convertToInteger(temp[1]) + 1 + "";
                temp[2] = NumberUtil.convertToInteger(temp[2]) + days + "";
            }
        }else{
            int days = NumberUtil.convertToInteger(temp[2]) + shelfTime;
            if(days > Util.Months.get(temp[1])){  //跳转下个月
                days -= Util.Months.get(temp[1]);
                //月份+1
                temp[1] = NumberUtil.convertToInteger(temp[1]) + 1 + "";
                temp[2] = NumberUtil.convertToInteger(temp[2]) + days + "";
            }
        }

        for(int i= 0;i< 3;i++)
            endTime += temp[i];
        //得到总价
        sum = price * weight;
    }
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public int getShelfTime() {
        return shelfTime;
    }

    public void setShelfTime(int shelfTime) {
        this.shelfTime = shelfTime;
    }

    public String getProduceTime() {
        return produceTime;
    }

    public void setProduceTime(String produceTime) {
        this.produceTime = produceTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getAttr1() {
        return attr1;
    }

    public void setAttr1(String attr1) {
        this.attr1 = attr1;
    }

    public String getAttr2() {
        return attr2;
    }

    public void setAttr2(String attr2) {
        this.attr2 = attr2;
    }
}
