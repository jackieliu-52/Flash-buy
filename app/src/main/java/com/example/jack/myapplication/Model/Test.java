package com.example.jack.myapplication.Model;

/**
 * Created by Jack on 2016/8/21.
 */
public class Test {
    private String name;
    private String pwd;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public Test(String name, String pwd) {
        this.name = name;
        this.pwd = pwd;
    }
}
