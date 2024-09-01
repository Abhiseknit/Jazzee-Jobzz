package com.jazzee.jazzeejobzz;

public class User {
    private String userid;
    private String fullname;
    private String username;
    private String profileicon;
    private String profilecategory;
    private String about;
    private String otherdetails;
    private String country;

    // Default constructor required for calls to DataSnapshot.getValue(User.class)
    public User() {
    }

    // Getters and setters
    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfileicon() {
        return profileicon;
    }

    public void setProfileicon(String profileicon) {
        this.profileicon = profileicon;
    }

    public String getProfilecategory() {
        return profilecategory;
    }

    public void setProfilecategory(String profilecategory) {
        this.profilecategory = profilecategory;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getOtherdetails() {
        return otherdetails;
    }

    public void setOtherdetails(String otherdetails) {
        this.otherdetails = otherdetails;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
