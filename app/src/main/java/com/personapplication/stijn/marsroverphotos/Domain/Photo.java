package com.personapplication.stijn.marsroverphotos.Domain;

public class Photo {
    private int ID;
    private int sol;
    private String fullCameraName;
    private String earthDate;
    private String shortCameraName;
    private Rover rover;

    private String img_url;

    //Object that holds information for a single photo
    public Photo(int ID, int sol, String fullCameraName, String shortCameraName, String earthDate, String img_url, Rover rover) {
        this.ID = ID;
        this.sol = sol;
        this.fullCameraName = fullCameraName;
        this.shortCameraName = shortCameraName;
        this.earthDate = earthDate;
        this.rover = rover;
        this.img_url = img_url;
    }

    public String getShortCameraName() {
        return shortCameraName;
    }

    public int getID() {
        return ID;
    }

    public int getSol() {
        return sol;
    }

    public String getFullCameraName() {
        return fullCameraName;
    }

    public String getEarthDate() {
        return earthDate;
    }

    public Rover getRover() {
        return rover;
    }

    public String getImg_url() {
        return img_url;
    }
}
