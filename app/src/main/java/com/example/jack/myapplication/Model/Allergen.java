package com.example.jack.myapplication.Model;

/**
 *
 */
public class Allergen {
    private String name;
    private int id;  //如果父类是1，子类也以1开始，比如1，1
    public boolean isChosed = false; //是否被选中

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Allergen(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
