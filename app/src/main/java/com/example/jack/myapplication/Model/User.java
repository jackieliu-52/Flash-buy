package com.example.jack.myapplication.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * 用户类
 */
public class User {
    public static ArrayList<Order> orders = new ArrayList<>(); //用户所有订单
    private String id;     //手机号码
    private String password = "";    //暂时无用
    private String name = "";   //姓名
    private String sex = "女"; //性别，默认为女
    private int age = 18;  //年龄，默认18岁
    private double remaining = 0; //余额
    //新增
    private double spend = 0 ; //已消费金额
    private String mail = ""; //邮箱
    private int frequency = 1 ; //发送总结账单的频率，默认一周一次
    private boolean sendMail = true; //是否发送账单，默认为true
    private boolean allergic = true; //是否启动过敏源提醒，默认开启
    private ArrayList<String>  sources = new ArrayList<>();    //过敏源列表
    private ArrayList<Item> starItems = new ArrayList<>(); //收藏的商品
    private boolean isLike = true; //是否打开猜你喜欢
    private ArrayList<Item> like = new ArrayList<>(); //猜你喜欢的商品

    public double getSpend() {
        return spend;
    }

    public void setSpend(double spend) {
        this.spend = spend;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public boolean isSendMail() {
        return sendMail;
    }

    public void setSendMail(boolean sendMail) {
        this.sendMail = sendMail;
    }

    public boolean isAllergic() {
        return allergic;
    }

    public void setAllergic(boolean allergic) {
        this.allergic = allergic;
    }

    public ArrayList<String> getSources() {
        return sources;
    }

    public void setSources(ArrayList<String> sources) {
        this.sources = sources;
    }

    public ArrayList<Item> getStarItems() {
        return starItems;
    }

    public void setStarItems(ArrayList<Item> starItems) {
        this.starItems = starItems;
    }

    public boolean isLike() {
        return isLike;
    }

    public void setLike(boolean like) {
        isLike = like;
    }

    public ArrayList<Item> getLike() {
        return like;
    }

    public void setLike(ArrayList<Item> like) {
        this.like = like;
    }

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

    public User() {
    }

    public User(String id) {
        this.id = id;
    }

}
