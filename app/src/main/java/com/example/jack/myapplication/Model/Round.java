package com.example.jack.myapplication.Model;

/**
 * 圆
 */
public class Round {
    //圆心
    private float x;
    private float y;

    private float R;  //半径

    public Round(float x, float y, float r) {
        this.x = x;
        this.y = y;
        R = r;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getR() {
        return R;
    }

    public void setR(float r) {
        R = r;
    }
}
