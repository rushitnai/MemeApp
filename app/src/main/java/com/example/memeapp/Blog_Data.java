package com.example.memeapp;

public class Blog_Data {

    private String caption;
    private String username;
    private String image;

    public Blog_Data(){

    }
    public Blog_Data(String username, String caption, String image) {
        this.username = username;
        this.caption = caption;
        this.image = image;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

}
