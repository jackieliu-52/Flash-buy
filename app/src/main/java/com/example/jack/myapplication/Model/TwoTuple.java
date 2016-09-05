package com.example.jack.myapplication.Model;

/**
 * 二元组
 */
public class TwoTuple<A,B> {
    public  A first;
    public final B second;

    public TwoTuple(A a, B b) {
        this.first = a;
        this.second = b;
    }
}
