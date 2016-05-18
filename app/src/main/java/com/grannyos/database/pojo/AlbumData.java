package com.grannyos.database.pojo;


public class AlbumData {

    private String cover;
    private String coverTitle;
    private String albumId;

    public AlbumData(String cover, String coverTitle, String albumId){
        this.cover = cover;
        this.coverTitle = coverTitle;
        this.albumId = albumId;
    }

    public String getCover(){
        return cover;
    }

    public String getCoverTitle(){
        return coverTitle;
    }

    public String getAlbumId(){
        return albumId;
    }
}
