package com.zgq.customview.twodimentionaltable.twodimentionaltable;

import android.view.View;

import java.util.Stack;

public class RecyclePool {

    private Stack<View>[] poolViews;
    private int mSize;

    public RecyclePool(int length) {
        mSize = length;
        poolViews = new Stack[length];
        for(int i = 0; i<length; i++) {
            poolViews[i] = new Stack<>();
        }
    }

    public void put(View view, int type){
        poolViews[type].push(view);
    }

    public View get(int type){
        if(poolViews[type].size()<=0){
            return null;
        }
        return poolViews[type].pop();
    }
}
