package com.example.jack.myapplication.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Jack on 2016/8/10.
 */
public class Order implements Parcelable {
    private ArrayList<LineItem> lineItems; //所有的LineItems
    private String order_Id;
    private String user_Id;
    private String order_date;
    private String pay_way; //支付方式
    private String supermarket_name; //超市名称
    private double payment; //支付总额
    private int status; //订单状态,0表示未支付，1表示支付
    private int cartNum;  //购物车编号
    private String EPCArray;    //EPC码的列表


    public Order(ArrayList<LineItem> lineItems, String orderId, String userId, String orderDate, String pay_way, String sm_name, double payment, int status) {
        this.lineItems = lineItems;
        this.order_Id = orderId;
        this.user_Id = userId;
        this.order_date = orderDate;
        this.pay_way = pay_way;
        this.supermarket_name = sm_name;
        this.payment = payment;
        this.status = status;
    }

    public int getCartNum() {
        return cartNum;
    }

    public void setCartNum(int cartNum) {
        this.cartNum = cartNum;
    }

    public String getEPCArray() {
        return EPCArray;
    }

    public void setEPCArray(String EPCArray) {
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
        return order_Id;
    }

    public void setOrderId(String orderId) {
        this.order_Id = orderId;
    }

    public String getUserId() {
        return user_Id;
    }

    public void setUserId(String userId) {
        this.user_Id = userId;
    }

    public String getOrderDate() {
        return order_date;
    }

    public void setOrderDate(String orderDate) {
        this.order_date = orderDate;
    }

    public String getPay_way() {
        return pay_way;
    }

    public void setPay_way(String pay_way) {
        this.pay_way = pay_way;
    }

    public String getSm_name() {
        return supermarket_name;
    }

    public void setSm_name(String sm_name) {
        this.supermarket_name = sm_name;
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
        dest.writeString(this.order_Id);
        dest.writeString(this.user_Id);
        dest.writeString(this.order_date);
        dest.writeString(this.pay_way);
        dest.writeString(this.supermarket_name);
        dest.writeDouble(this.payment);
        dest.writeInt(this.status);
        dest.writeInt(this.cartNum);
        dest.writeString(this.EPCArray);
    }

    protected Order(Parcel in) {
        this.lineItems = in.createTypedArrayList(LineItem.CREATOR);
        this.order_Id = in.readString();
        this.user_Id = in.readString();
        this.order_date = in.readString();
        this.pay_way = in.readString();
        this.supermarket_name = in.readString();
        this.payment = in.readDouble();
        this.status = in.readInt();
        this.cartNum = in.readInt();
        this.EPCArray = in.readString();
    }

    public static final Creator<Order> CREATOR = new Creator<Order>() {
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
