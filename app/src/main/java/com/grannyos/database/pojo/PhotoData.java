package com.grannyos.database.pojo;


public class PhotoData {

    private String assetTitle;
    private String assetResource;
    private String assetType;

    public PhotoData(){
    }

    public String getAssetTitle(){
        return assetTitle;
    }

    public String getAssetResource(){
        return assetResource;
    }

    public String getAssetType(){
        return assetType;
    }

    public void setAssetTitle(String assetTitle){
        this.assetTitle = assetTitle;
    }

    public void setAssetResource(String assetResource){
        this.assetResource = assetResource;
    }

    public void setAssetType(String assetType){
        this.assetType = assetType;
    }

}
