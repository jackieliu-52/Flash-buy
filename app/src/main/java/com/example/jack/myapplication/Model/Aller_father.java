package com.example.jack.myapplication.Model;

import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;

import java.util.List;

/**
 * 过敏源的概括
 *   ①奶类，特别是牛奶；
     ②禽蛋类；
     ③鱼类（包括海水鱼、淡水鱼）；
     ④甲壳类水生动物（虾、对虾、螃蟹、大小龙虾、蛤蜊等）；
     ⑤花生；
     ⑥大豆；
     ⑦坚果类（杏仁、核桃、腰果、榛子、松子、栗子等）；
     ⑧小麦。
 */
public class Aller_father  implements ParentListItem {

    private String name;
    private int id;  //1表示奶类，一次类推


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private List<Allergen> mAllergens;

    public Aller_father(List<Allergen> allergens) {
        mAllergens = allergens;
    }

    @Override
    public List getChildItemList() {
        return mAllergens;
    }

    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }

    public Aller_father(String name, int id, List<Allergen> mAllergens) {
        this.name = name;
        this.id = id;
        this.mAllergens = mAllergens;
    }
}
