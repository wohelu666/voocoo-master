package com.voocoo.pet.entity;

public class UpdateDiet {
    public int dietId;
    public int dietAmount;
    public String dietTag;
    public String dietTime;

    public UpdateDiet(int dietId, int dietAmount, String dietTag, String dietTime) {
        this.dietId = dietId;
        this.dietAmount = dietAmount;
        this.dietTag = dietTag;
        this.dietTime = dietTime;
    }
}
