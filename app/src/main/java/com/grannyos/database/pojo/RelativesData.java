package com.grannyos.database.pojo;


public class RelativesData {

    private String firstName;
    private String lastName;
    private String avatar;
    private String relativesId;

    public RelativesData(String firstName, String lastName, String avatar, String relativesId){
        this.firstName = firstName;
        this.lastName = lastName;
        this.avatar = avatar;
        this.relativesId = relativesId;
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
}
