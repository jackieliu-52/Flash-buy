package com.example.jack.myapplication.Model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Jack on 2016/8/10.
 */
public class LineItem implements Parcelable {
    private Item item; //商品
    private String goods_ID;  //商品ID
    private String order_ID; //订单ID
    private int number; //商品数量
    private double unitPrice; //商品总价
    //新增
    public boolean isBulk = false; //是否是散装商品

    private int id;
    public LineItem() {
    }

    public LineItem(BulkItem item) {
        isBulk = true;
        this.item = item;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isBulk() {
        return isBulk;
    }

    public void setBulk(boolean bulk) {
        isBulk = bulk;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getOrder_ID() {
        return order_ID;
    }

    public void setOrder_ID(String order_ID) {
        this.order_ID = order_ID;
    }

    public String getGoods_ID() {
        return goods_ID;
    }

    public void setGoods_ID(String goods_ID) {
        this.goods_ID = goods_ID;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public int getNum() {
        return number;
    }

    public void setNum(int num) {
        this.number = num;
    }

    /**
     * 这不是普通的get方法
     * @return
     */
    public double getUnitPrice() {
        if(isBulk){
            unitPrice = ((BulkItem)item).getSum();
        }else {
            unitPrice = item.realPrice() * number;
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
        dest.writeString(this.goods_ID);
        dest.writeString(this.order_ID);
        dest.writeInt(this.number);
        dest.writeDouble(this.unitPrice);
        dest.writeByte(this.isBulk ? (byte) 1 : (byte) 0);
        dest.writeInt(this.id);
    }

    protected LineItem(Parcel in) {
        this.item = in.readParcelable(Item.class.getClassLoader());
        this.goods_ID = in.readString();
        this.order_ID = in.readString();
        this.number = in.readInt();
        this.unitPrice = in.readDouble();
        this.isBulk = in.readByte() != 0;
        this.id = in.readInt();
    }

    public static final Creator<LineItem> CREATOR = new Creator<LineItem>() {
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
