package com.example.jack.myapplication.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Jack on 2016/8/10.
 */
public class User implements Parcelable {
    public static ArrayList<Order> orders = new ArrayList<>(); //用户所有订单
    private String id;     //手机号码
    private String password;
    private String name;   //姓名
    private String sex; //性别
    private int age;  //年龄
    private double remaining; //余额

    public static ArrayList<Order> getOrders() {
        return orders;
    }

    public static void setOrders(ArrayList<Order> orders) {
        User.orders = orders;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public double getRemaining() {
        return remaining;
    }

    public void setRemaining(double remaining) {
        this.remaining = remaining;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.password);
        dest.writeString(this.name);
        dest.writeString(this.sex);
        dest.writeInt(this.age);
        dest.writeDouble(this.remaining);
    }

    public User() {
    }

    protected User(Parcel in) {
        this.id = in.readString();
        this.password = in.readString();
        this.name = in.readString();
        this.sex = in.readString();
        this.age = in.readInt();
        this.remaining = in.readDouble();
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
