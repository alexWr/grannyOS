package com.grannyos.database.pojo;


public class RelativesData {

    private String firstName;
    private String lastName;
    private String avatar;
    private String relativesId;
    private int    missing;

    public RelativesData(String firstName, String lastName, String avatar, String relativesId,int missing){
        this.firstName = firstName;
        this.lastName = lastName;
        this.avatar = avatar;
        this.relativesId = relativesId;
        this.missing = missing;
    }

    public String getFirstName(){
        return firstName;
    }

    public String getLastName(){
        return lastName;
    }

    public String getAvatar(){
        return avatar;
    }

    public String getRelativesId(){
        return relativesId;
    }

    public int getMissing(){
        return missing;
    }
}
