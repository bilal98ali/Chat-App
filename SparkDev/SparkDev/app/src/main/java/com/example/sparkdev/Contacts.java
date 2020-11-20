package com.example.sparkdev;

public class Contacts {

    public String fullname, bio, imageurl;

    //default constructor
    public Contacts(){

    }

    public Contacts(String fullname, String bio, String imageurl) {
        this.fullname = fullname;
        this.bio = bio;
        this.imageurl = imageurl;
    }

    public String getName() {
        return fullname;
    }

    public void setName(String fullname) {
        this.fullname = fullname;
    }

    public String getStatus() {
        return bio;
    }

    public void setStatus(String bio) {
        this.bio = bio;
    }

    public String getImage() {
        return imageurl;
    }

    public void setImage(String imageurl) {
        this.imageurl = imageurl;
    }
}
