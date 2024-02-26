package com.example.gamepetcat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Maid {
    private List<Integer> maidLooks = new ArrayList<Integer>();
    private int currentLookIndex = 0;

    public Maid(int... maidLooks){
        for (int look : maidLooks){
            this.maidLooks.add(look);
        }
    }

    public int nextLook(){
        int look = maidLooks.get(currentLookIndex);
        //change currentLookIndex and make it in allowed range of maidLooks
        currentLookIndex = (currentLookIndex + 1) % maidLooks.size();
        return look;
    }
}
