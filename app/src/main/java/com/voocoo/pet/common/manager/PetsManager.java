package com.voocoo.pet.common.manager;


import com.voocoo.pet.entity.Pet;

import java.util.ArrayList;
import java.util.List;

public class PetsManager {

    private static volatile PetsManager instance;
    private List<Pet> PetList = new ArrayList<>();

    public static PetsManager getInstance() {
        if (instance == null) {
            synchronized (PetsManager.class) {
                if (instance == null) {
                    instance = new PetsManager();
                }
            }
        }
        return instance;
    }


    private PetsManager() {

    }

    public void deletePet() {
        PetList.clear();
    }

    public void savePet(Pet Pet) {
        PetList.add(Pet);
    }

    public void setPetList(List<Pet> list) {
        PetList.clear();
        PetList.addAll(list);
    }

    public List<Pet> getPetList() {
        return PetList;
    }

    public Pet getFromId(int id) {
        for (int i = 0; i < getPetList().size(); i++) {
            if (getPetList().get(i).petId == id) {
                return getPetList().get(i);
            }
        }
        return null;
    }
}


