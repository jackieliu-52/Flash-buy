package com.example.jack.myapplication.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.example.jack.myapplication.Model.Aller_father;
import com.example.jack.myapplication.Model.Allergen;
import com.example.jack.myapplication.R;
import com.example.jack.myapplication.View.ViewHolder.AllerHolder;
import com.example.jack.myapplication.View.ViewHolder.AllergenHolder;

import java.util.List;

/**
 * 可扩展的ExpandView
 */
public class ExpandViewAdapter extends ExpandableRecyclerAdapter<AllerHolder,AllergenHolder> {
    private LayoutInflater mInflator;

    public ExpandViewAdapter(Context context, @NonNull List<? extends ParentListItem> parentItemList) {
        super(parentItemList);
        mInflator = LayoutInflater.from(context);
    }

    // onCreate ...
    @Override
    public AllerHolder onCreateParentViewHolder(ViewGroup parentViewGroup) {
        View recipeView = mInflator.inflate(R.layout.aller_view, parentViewGroup, false);
        return new AllerHolder(recipeView);
    }

    @Override
    public AllergenHolder onCreateChildViewHolder(ViewGroup childViewGroup) {
        View ingredientView = mInflator.inflate(R.layout.allergen_view, childViewGroup, false);
        return new AllergenHolder(ingredientView);
    }

    // onBind ...
    @Override
    public void onBindParentViewHolder(AllerHolder allerHolder, int position, ParentListItem parentListItem) {
        Aller_father aller_father = (Aller_father) parentListItem;
        allerHolder.bind(aller_father);
    }

    @Override
    public void onBindChildViewHolder(AllergenHolder allergenHolder, int position, Object childListItem) {
        Allergen allergen = (Allergen) childListItem;
        allergenHolder.bind(allergen);
    }


}
