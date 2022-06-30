package com.SoftEng.wemeet.DataModel;

import android.net.Uri;

import com.SoftEng.wemeet.R;

public class UserContainer{
    private String userEmail, usename, password ; //kalau private, error (minimal setter getter)
    private Boolean hasImage ;
    public UserContainer(){

    }

    public UserContainer(String userEmail, String username, String password, Boolean hasImage){
        this.userEmail = userEmail ;
        this.usename = username ;
        this.password = password ;
        this.hasImage = hasImage ;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUsename() {
        return usename;
    }

    public void setUsename(String usename) {
        this.usename = usename;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getHasImage() {
        return hasImage;
    }

    public void setHasImage(Boolean hasImage) {
        this.hasImage = hasImage;
    }
}
