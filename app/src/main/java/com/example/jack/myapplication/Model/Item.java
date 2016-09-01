package com.example.jack.myapplication.Model;

import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * 商品类
 */
public class Item implements Parcelable {
    private String name;
    private String Pid;  //小类ID
    private String Iid;  //商品ID
    private String image;  //图片路径
    private String company = "无"; //公司
    private double price;
    private String size; //规格
    private String bar_code; //条形码
    private String EPC;  //标签的EPC
    private int staus; //商品状态，0-未售出，1-已售出
    private int discount;  //折扣，10表示没有折扣，5表示五折
    private boolean isStar;  //是否被收藏，True表示被收藏了
    private String source; //来源，中国还是外国?
    private String category = "未知"; //种类
    private int storage = 0;  //存储量


    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public double realPrice(){

        return price * (discount/10.0) ;
    }

    public boolean isStar() {
        return isStar;
    }

    public void setStar(boolean star) {
        isStar = star;
    }

    public String getEPC() {
        return EPC;
    }

    public void setEPC(String EPC) {
        this.EPC = EPC;
    }

    public int getStaus() {
        return staus;
    }

    public void setStaus(int staus) {
        this.staus = staus;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public String getBar_code() {
        return bar_code;
    }

    public void setBar_code(String bar_code) {
        this.bar_code = bar_code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPid() {
        return Pid;
    }

    public void setPid(String pid) {
        Pid = pid;
    }

    public String getIid() {
        return Iid;
    }

    public void setIid(String iid) {
        Iid = iid;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }




    public Item() {
        this("名称", "0", "00", "", "", 0, "", "233", 0, 10);
    }

    public Item(String name, String pid, String iid, String image, String company, double price, String size) {
        this(name, pid, iid, image, company, price, size, "233", 0, 10);
    }

    public Item(String name, String image, double price) {
        this(name,"","",image,"无",price,"未知","","",0,10,false,"未知");
    }

    public Item (InternetItem internetItem){
        this.name = internetItem.getName();
        this.Pid = "";
        this.Iid = "";
        this.image = "";
        this.company = internetItem.getCompany();
        this.price = internetItem.getPrice();
        if(!internetItem.getSpec().equals(""))
            this.size = internetItem.getSpec();
        else
            this.size = "未知";
        this.EPC = "";
        this.staus = 0;
        this.discount = 10;
        this.isStar = false;
        this.bar_code = internetItem.getPrefix();  //条形码前缀
        this.source = internetItem.getCountry();
    }
    public Item(String name, String pid, String iid, String image, String company, double price, String size, String EPC, int staus, int discount) {
       this(name,pid,iid,image,company,price,size,"",EPC,staus,discount,false,"未知");
    }

    public Item(String name, String pid, String iid, String image, String company, double price, String size, String bar_code, String EPC, int staus, int discount, boolean isStar, String source) {
        this.name = name;
        Pid = pid;
        Iid = iid;
        this.image = image;
        this.company = company;
        this.price = price;
        this.size = size;
        this.bar_code = bar_code;
        this.EPC = EPC;
        this.staus = staus;
        this.discount = discount;
        this.isStar = isStar;
        this.source = source;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.Pid);
        dest.writeString(this.Iid);
        dest.writeString(this.image);
        dest.writeString(this.company);
        dest.writeDouble(this.price);
        dest.writeString(this.size);
        dest.writeString(this.bar_code);
        dest.writeString(this.EPC);
        dest.writeInt(this.staus);
        dest.writeInt(this.discount);
        dest.writeByte(this.isStar ? (byte) 1 : (byte) 0);
        dest.writeString(this.source);
        dest.writeString(this.category);
        dest.writeInt(this.storage);
    }

    protected Item(Parcel in) {
        this.name = in.readString();
        this.Pid = in.readString();
        this.Iid = in.readString();
        this.image = in.readString();
        this.company = in.readString();
        this.price = in.readDouble();
        this.size = in.readString();
        this.bar_code = in.readString();
        this.EPC = in.readString();
        this.staus = in.readInt();
        this.discount = in.readInt();
        this.isStar = in.readByte() != 0;
        this.source = in.readString();
        this.category = in.readString();
        this.storage = in.readInt();
    }

}
