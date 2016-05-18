package com.grannyos.database.pojo;


public class EventData {

    private String calendarDate;
    private String calendarTitle;
    private String calendarId;
    private String albumAssetId;

    public EventData(String calendarDate, String calendarTitle, String calendarId, String albumAssetId){
        this.calendarDate = calendarDate;
        this.calendarTitle = calendarTitle;
        this.calendarId = calendarId;
        this.albumAssetId = albumAssetId;
    }

    public String getCalendarDate(){
        return calendarDate;
    }

    public String getCalendarTitle(){
        return calendarTitle;
    }

    public String getCalendarId(){
        return calendarId;
    }

    public String getAlbumAssetId(){
        return albumAssetId;
    }

}
