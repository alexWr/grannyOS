package com.grannyos.network;


import android.content.ContentValues;

import com.google.gson.annotations.SerializedName;
import com.grannyos.database.DatabaseHelper;

import java.util.List;

/**
 * Class is response from different request to web server
 */

public class ResponseRest {

    //register

    public class RegisterUserResponse{

        public RegisterUserResponse(){}

        @SerializedName("token")
        private String token;

        public String getToken(){
            return token;
        }
    }

    //login

    public class LoginUserResponse{

        public LoginUserResponse(){}

        @SerializedName("session")
        private String session;

        public String getSession(){
            return session;
        }
    }

    //get profile info

    public class ProfileInfoResponse{

        public ProfileInfoResponse(){}

        @SerializedName("id")
        private String id;

        public String getMyId(){
            return id;
        }
    }

    //Get List Relatives

    public class ListRelativesResponse {

        public ListRelativesResponse(){}

        @SerializedName("id")
        public String relativesId;
        @SerializedName("firstName")
        public String firstName;
        @SerializedName("lastName")
        public String lastName;
        @SerializedName("avatar")
        public String avatar;

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


        public ContentValues getValues() {
            ContentValues values = new ContentValues();

            values.put(DatabaseHelper.RELATIVES_ID, relativesId);
            if(firstName==null){
                firstName = "someone";
            }
            if(lastName==null){
                lastName = "someone";
            }
            values.put(DatabaseHelper.RELATIVES_FIRST_NAME, firstName);
            values.put(DatabaseHelper.RELATIVES_LAST_NAME, lastName);
            values.put(DatabaseHelper.RELATIVES_MISSING_CALL, 0);

            return values;
        }

    }

    //Get all albums

    public class albumListResponse {

        public albumListResponse(){}

        @SerializedName("id")
        public String albumId;
        @SerializedName("cover")
        public String cover;
        @SerializedName("coverTitle")
        public String coverTitle;
        @SerializedName("provider")
        public String provider;
        @SerializedName("updatedAt")
        public String updatedAt;
        @SerializedName("createdAt")
        public String createdAt;
        @SerializedName("assets")
        public List<Assets> assets;

        public String getAlbumId(){
            return albumId;
        }

        public String getProvider(){
            return provider;
        }

        public List<Assets> getAssets() {
            return assets;
        }

        public String getCover() {
            return cover;
        }

        public String getCoverTitle() {
            return coverTitle;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public ContentValues getValues() {
            ContentValues values = new ContentValues();

            values.put(DatabaseHelper.ALBUM_ID, albumId);
            values.put(DatabaseHelper.ALBUM_COVER_TITLE, coverTitle);
            values.put(DatabaseHelper.ALBUM_PROVIDER, provider);
            values.put(DatabaseHelper.ALBUM_CREATED_AT, createdAt);
            values.put(DatabaseHelper.ALBUM_UPDATED_AT, updatedAt);

            return values;
        }

        public class Assets {
            public Assets() {
            }

            @SerializedName("id")
            public String assetId;
            @SerializedName("title")
            public String title;
            @SerializedName("resource")
            public String resource;
            @SerializedName("type")
            public String type;

            public String getTitle() {
                return title;
            }

            public String getResource() {
                    return resource;
                }

            public String getType() {
                return type;
            }

            public String getAssetId() {
                return assetId;
            }

            public ContentValues getValues() {
                ContentValues values = new ContentValues();

                values.put(DatabaseHelper.ASSET_ID, assetId);
                values.put(DatabaseHelper.ASSET_TITLE, title);
                values.put(DatabaseHelper.ASSET_TYPE, type);

                return values;
            }
        }
    }

    // get list of events

    public class calendarEventsResponse {

        public calendarEventsResponse(){}

        @SerializedName("id")
        public String eventId;
        @SerializedName("title")
        public String title;
        @SerializedName("date")
        public String date;
        @SerializedName("albumAssetId")
        public String albumAssetId;

        public String getEventId(){
            return eventId;
        }
        public String getTitle(){
            return title;
        }
        public String getDate(){
            return date;
        }
        public String getAlbumAssetId(){
            return albumAssetId;
        }

        public ContentValues getValues() {
            ContentValues values = new ContentValues();

            values.put(DatabaseHelper.EVENT_ID, eventId);
            values.put(DatabaseHelper.EVENT_TITLE, title);
            values.put(DatabaseHelper.EVENT_DATE, date);
            if(albumAssetId.equals("")){
                albumAssetId="none";
            }
            values.put(DatabaseHelper.EVENT_ALBUM_ASSET_ID, albumAssetId);

            return values;
        }
    }

}
