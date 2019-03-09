package com.personapplication.stijn.marsroverphotos.Domain;

public class Rover {
    private String name;
    private int photoAmount;

    //Object that holds information for a Rover, is linked to a photo
    public Rover(String name, int photoAmount) {
        this.name = name;
        this.photoAmount = photoAmount;
    }

    public String getName() {
        return name;
    }

    public int getPhotoAmount() {
        return photoAmount;
    }
}
