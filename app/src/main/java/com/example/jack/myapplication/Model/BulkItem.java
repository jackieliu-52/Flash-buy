package com.example.jack.myapplication.Model;


import android.os.Parcel;

import com.example.jack.myapplication.Util.Util;
import com.litesuits.common.utils.NumberUtil;

/**
 * 散装商品
 */
public class BulkItem extends Item {
    private double weight = 0; //重量，散装商品需要用到的属性
    private int  shelfTime = 10;  //保质期,一般是天数,计算到期日期的时候处理有点麻烦
    private String produceTime = Util.getBefoceTime(20);  //生产日期
    private String endTime;   //到期日期
    private String attr1;  //特征属性：比如避光存放
    private String attr2 = ""; //保留
    private double sum; //总价

    public BulkItem() {
        super();
        //只采用默认配置参数
        init(weight,shelfTime,produceTime,"","","",0);
    }

    public BulkItem(String name, String image, double price) {
        super(name, image, price);
        //有名字，图片，价格，但是采用默认配置参数
        init(weight,shelfTime,produceTime,"","","",0);
    }

    public BulkItem(double weight, int shelfTime, String produceTime, String endTime, String attr1, String attr2, double sum) {
        //隐式调用了父类的无参构造函数，super()
        init(weight,shelfTime,produceTime,endTime,attr1,attr2,sum);
    }

    public BulkItem(double weight, int shelfTime, String produceTime) {
        init(weight,shelfTime,produceTime,"","","",0);
    }

    private void init(double weight, int shelfTime, String produceTime, String endTime, String attr1, String attr2, double sum){
        this.weight = weight;
        this.shelfTime = shelfTime;
        this.produceTime = produceTime;
        this.endTime = endTime;
        this.attr1 = attr1;
        this.attr2 = attr2;
        this.sum = sum;
    }

    //得到到期时间和总价
    public void jisuan(){
        String[] temp = produceTime.split("/");
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
        sum = getSum();
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

    public double getSum() {
        return realPrice() * weight;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeDouble(this.weight);
        dest.writeInt(this.shelfTime);
        dest.writeString(this.produceTime);
        dest.writeString(this.endTime);
        dest.writeString(this.attr1);
        dest.writeString(this.attr2);
        dest.writeDouble(this.sum);
    }

    protected BulkItem(Parcel in) {
        super(in);
        this.weight = in.readDouble();
        this.shelfTime = in.readInt();
        this.produceTime = in.readString();
        this.endTime = in.readString();
        this.attr1 = in.readString();
        this.attr2 = in.readString();
        this.sum = in.readDouble();
    }

    public static final Creator<BulkItem> CREATOR = new Creator<BulkItem>() {
        @Override
        public BulkItem createFromParcel(Parcel source) {
            return new BulkItem(source);
        }

        @Override
        public BulkItem[] newArray(int size) {
            return new BulkItem[size];
        }
    };
}

