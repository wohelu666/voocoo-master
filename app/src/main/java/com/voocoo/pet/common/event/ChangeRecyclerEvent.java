package com.voocoo.pet.common.event;

import java.util.ArrayList;

public class ChangeRecyclerEvent {
    public ArrayList<Integer> recycler;

    public ChangeRecyclerEvent(ArrayList<Integer> recycler) {
        this.recycler = recycler;
    }
}
