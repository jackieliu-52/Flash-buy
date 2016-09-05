package com.example.jack.myapplication.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Jack on 2016/8/10.
 */
public class Order implements Parcelable {
    private ArrayList<LineItem> lineItems; //所有的LineItems
    private String orderId;
    private String userId;
    private String orderDate;
    private String pay_way; //支付方式
    private String sm_name; //超市名称
    private double payment; //支付总额
    private int status; //订单状态,0表示未支付，1表示支付
    private ArrayList<String> EPCArray;    //EPC码的列表


    public Order(ArrayList<LineItem> lineItems, String orderId, String userId, String orderDate, String pay_way, String sm_name, double payment, int status) {
        this.lineItems = lineItems;
        this.orderId = orderId;
        this.userId = userId;
        this.orderDate = orderDate;
        this.pay_way = pay_way;
        this.sm_name = sm_name;
        this.payment = payment;
        this.status = status;
    }

    public ArrayList<String> getEPCArray() {
        return EPCArray;
    }

    public void setEPCArray(ArrayList<String> EPCArray) {
        this.EPCArray = EPCArray;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public ArrayList<LineItem> getLineItems() {
        return lineItems;
    }

    public void setLineItems(ArrayList<LineItem> lineItems) {
        this.lineItems = lineItems;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getPay_way() {
        return pay_way;
    }

    public void setPay_way(String pay_way) {
        this.pay_way = pay_way;
    }

    public String getSm_name() {
        return sm_name;
    }

    public void setSm_name(String sm_name) {
        this.sm_name = sm_name;
    }

    /**
     * 这里不是普通的get方法，这是不好的写法，要改
     * @return
     */
    public double getPayment() {
        payment = 0;
        for(LineItem lineItem: lineItems){
            payment += lineItem.getUnitPrice();
        }
        return payment;
    }

    public void setPayment(double payment) {
        this.payment = payment;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.lineItems);
        dest.writeString(this.orderId);
        dest.writeString(this.userId);
        dest.writeString(this.orderDate);
        dest.writeString(this.pay_way);
        dest.writeString(this.sm_name);
        dest.writeDouble(this.payment);
        dest.writeInt(this.status);
        dest.writeStringList(this.EPCArray);
    }

    protected Order(Parcel in) {
        this.lineItems = in.createTypedArrayList(LineItem.CREATOR);
        this.orderId = in.readString();
        this.userId = in.readString();
        this.orderDate = in.readString();
        this.pay_way = in.readString();
        this.sm_name = in.readString();
        this.payment = in.readDouble();
        this.status = in.readInt();
        this.EPCArray = in.createStringArrayList();
    }

    public static final Parcelable.Creator<Order> CREATOR = new Parcelable.Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel source) {
            return new Order(source);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };
}
