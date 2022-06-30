package com.SoftEng.wemeet.DataModel;

public class WebinarFrontContainer {
    private String ID ;
    private String title ;
    private String Pamphlet ; //bentuknya URL
    private String hasPamphlet ; //Boolean

    public WebinarFrontContainer(){}

    public WebinarFrontContainer(String ID, String title, String Pamphlet, String hasPamphlet) {
        this.ID = ID ;
        this.title = title;
        this.Pamphlet = Pamphlet ;
        this.hasPamphlet = hasPamphlet ;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getHasPamphlet() {
        return hasPamphlet;
    }

    public void setHasPamphlet(String hasPamphlet) {
        this.hasPamphlet = hasPamphlet;
    }

    public String getPamphlet() {
        return Pamphlet;
    }

    public void setPamphlet(String pamphlet) {
        Pamphlet = pamphlet;
    }
}
