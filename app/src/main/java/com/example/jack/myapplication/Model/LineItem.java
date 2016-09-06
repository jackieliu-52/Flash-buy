package com.example.jack.myapplication.Model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Jack on 2016/8/10.
 */
public class LineItem implements Parcelable {
    private Item item; //商品
    private String Iid;  //商品ID
    private String orderId; //订单ID
    private int num; //商品数量
    private double unitPrice; //商品总价
    //新增
    public boolean isBulk = false; //是否是散装商品

    public LineItem() {
    }

    public LineItem(BulkItem item) {
        isBulk = true;
        this.item = item;
    }
    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public String getIid() {
        return Iid;
    }

    public void setIid(String iid) {
        Iid = iid;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    /**
     * 这不是普通的get方法
     * @return
     */
    public double getUnitPrice() {
        if(isBulk){
            unitPrice = ((BulkItem)item).getSum();
        }else {
            unitPrice = item.realPrice() * num;
        }
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.item, flags);
        dest.writeString(this.Iid);
        dest.writeString(this.orderId);
        dest.writeInt(this.num);
        dest.writeDouble(this.unitPrice);
    }



    protected LineItem(Parcel in) {
        this.item = in.readParcelable(Item.class.getClassLoader());
        this.Iid = in.readString();
        this.orderId = in.readString();
        this.num = in.readInt();
        this.unitPrice = in.readDouble();
    }

    public static final Parcelable.Creator<LineItem> CREATOR = new Parcelable.Creator<LineItem>() {
        @Override
        public LineItem createFromParcel(Parcel source) {
            return new LineItem(source);
        }

        @Override
        public LineItem[] newArray(int size) {
            return new LineItem[size];
        }
    };
}
