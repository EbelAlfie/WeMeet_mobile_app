package com.SoftEng.wemeet.DataModel;

import java.io.Serializable;

public class WebinarContainer implements Serializable {
    private String webinarTitle ;
    private String webinarspeaker ;
    private String pamphlet ;
    private String webinarDate ;
    private String webinarTime ;
    private String webinarFee ;
    private String webinarLink ;
    private String webinarCertStatus;

    public WebinarContainer(){} //wajib  does not define a no-argument constructor. If you are using ProGuard, make sure these constructors are not stripped." Tried everything, no idea why it happen.


    public WebinarContainer( String webinarTitle, String webinarTime, String speaker, String pamphlet, String webinarLink, String webinarFee, String webinarDate, String webinarCertStatus) {
        this.webinarspeaker = speaker ;
        this.webinarTitle = webinarTitle;
        this.pamphlet = pamphlet ;
        this.webinarDate = webinarDate;
        this.webinarTime = webinarTime;
        this.webinarFee = webinarFee;
        this.webinarLink = webinarLink;
        this.webinarCertStatus =webinarCertStatus ;
    }

    public String getWebinarTitle() {
        return webinarTitle;
    }

    public void setWebinarTitle(String webinarTitle) {
        this.webinarTitle = webinarTitle;
    }

    public String getWebinarspeaker() {
        return webinarspeaker;
    }

    public void setWebinarspeaker(String webinarspeaker) {
        this.webinarspeaker = webinarspeaker;
    }

    public String getWebinarDate() {
        return webinarDate;
    }

    public void setWebinarDate(String webinarDate) {
        this.webinarDate = webinarDate;
    }

    public String getWebinarTime() {
        return webinarTime;
    }

    public void setWebinarTime(String webinarTime) {
        this.webinarTime = webinarTime;
    }

    public String getWebinarFee() {
        return webinarFee;
    }

    public void setWebinarFee(String webinarFee) {
        this.webinarFee = webinarFee;
    }

    public String getWebinarLink() {
        return webinarLink;
    }

    public void setWebinarLink(String webinarLink) {
        this.webinarLink = webinarLink;
    }

    public String getWebinarCertStatus() {
        return webinarCertStatus;
    }

    public void setWebinarCertStatus(String webinarCertStatus) {
        this.webinarCertStatus = webinarCertStatus;
    }

    public String getPamphlet() {
        return pamphlet;
    }

    public void setPamphlet(String pamphlet) {
        this.pamphlet = pamphlet;
    }
}
