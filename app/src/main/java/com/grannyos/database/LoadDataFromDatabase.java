package com.grannyos.database;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.util.Log;

import com.grannyos.database.pojo.AlbumData;
import com.grannyos.database.pojo.EventData;
import com.grannyos.database.pojo.PhotoData;
import com.grannyos.database.pojo.RelativesData;

import java.io.File;
import java.util.ArrayList;

/**
 *  Class for relation between database and java object
 */

public class LoadDataFromDatabase {


    private static final String                 TAG = "LoadDataFromDatabase";
    private DatabaseHelper                      dbHelper;
    private SQLiteDatabase                      db;
    private Cursor                              cursor;
    private String                              checkId;
    private static ArrayList<RelativesData>     relativesData = new ArrayList<>();
    private static ArrayList<EventData>         eventData = new ArrayList<>();
    private static ArrayList<String>            albumResource = new ArrayList<>();
    private static ArrayList<AlbumData>         albumData = new ArrayList<>();
    private static ArrayList<PhotoData>         photoData= new ArrayList<>();
    private static ArrayList<String>            online = new ArrayList<>();
    private String                              checkAlbumId;
    private SharedPreferences                   sharedPreferences;
    private ContentValues                       contentValues;


    public static ArrayList<RelativesData> getRelativeData(){
        if(relativesData == null){
            Log.d(TAG, "relatives data == null");
        }
        return relativesData;
    }

    public static ArrayList<EventData> getEventData(){
        if(eventData == null){
            Log.d(TAG, "relatives data == null");
        }
        return eventData;
    }

    public static ArrayList<AlbumData> getAlbumData(){
        if(albumData == null){
            Log.d(TAG, "relatives data == null");
        }
        return albumData;
    }

    public static ArrayList<String> getAlbumResource(){
        if(albumResource == null){
            Log.d(TAG, "relatives data == null");
        }
        return albumResource;
    }

    public static ArrayList<PhotoData> getPhotoData(){
        if(photoData == null){
            Log.d(TAG, "relatives data == null");
        }
        return photoData;
    }

    public static ArrayList<String> getOnlineData(){
        if(online == null){
            Log.d(TAG, "relatives data == null");
        }
        return online;
    }


    // Constructor is get data from needed table

    public LoadDataFromDatabase(String checkString, Activity activity, String checkAlbumId){
        dbHelper = new DatabaseHelper(activity, "database.db", null, 1);
        db = dbHelper.getReadableDatabase();
        this.checkAlbumId = checkAlbumId;
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        if(checkString.equals("relatives")){
            getDataRelatives();
        }
        if(checkString.equals("event")){
            getDataEvent();
        }
        if(checkString.equals("album")){
            getDataAlbum();
        }
        if(checkString.equals("photo")){
            getDataPhoto();
        }
    }

    //Constructor is save desired data in database

    public LoadDataFromDatabase(String checkString, Context context,  ContentValues values){
        dbHelper = new DatabaseHelper(context, "database.db", null, 1);
        db = dbHelper.getWritableDatabase();
        this.contentValues = values;
        if(checkString.equals("relatives")){
            saveRelatives();
        }
        if(checkString.equals("album")){
            saveAlbum();
        }
        if(checkString.equals("photo")){
            savePhoto();
        }
        if(checkString.equals("event")){
            saveEvent();
        }
    }

    //Constructor for update row

    public LoadDataFromDatabase(Context context, String table, ContentValues updateValues, String updateId){
        dbHelper = new DatabaseHelper(context, "database.db", null, 1);
        db = dbHelper.getWritableDatabase();
        updateTableById(table, updateValues, updateId);
    }

    //Constructor for delete file and row from database

    public LoadDataFromDatabase(Context context, String table, String column, String rowImage, String deleteId){
        dbHelper = new DatabaseHelper(context, "database.db", null, 1);
        db = dbHelper.getWritableDatabase();
        deleteFileRowById(table,column, deleteId, rowImage);
    }

    private void saveRelatives(){
        db.beginTransaction();
        try {
            db.replace(DatabaseHelper.TABLE_RELATIVES, null, contentValues);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        dbHelper.close();
        db.close();
    }

    private void saveAlbum(){
        db.beginTransaction();
        try {
            db.replace(DatabaseHelper.TABLE_ALBUM, null, contentValues);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        dbHelper.close();
        db.close();
    }

    private void savePhoto(){
        db.beginTransaction();
        try {
            db.replace(DatabaseHelper.TABLE_PHOTO, null, contentValues);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        dbHelper.close();
        db.close();
    }

    private void saveEvent(){
        db.beginTransaction();
        try {
            db.replace(DatabaseHelper.TABLE_EVENT, null, contentValues);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        dbHelper.close();
        db.close();
    }


    private void getDataRelatives(){
        online.clear();
        relativesData.clear();
        String[] columns = new String[]{ DatabaseHelper.RELATIVES_FIRST_NAME, DatabaseHelper.RELATIVES_LAST_NAME, DatabaseHelper.RELATIVES_ICON, DatabaseHelper.RELATIVES_ID};
        cursor=db.query(DatabaseHelper.TABLE_RELATIVES,columns,null,null,null,null, DatabaseHelper.RELATIVES_FIRST_NAME + " COLLATE NOCASE ASC");
        if (cursor .moveToFirst()) {
            while (!cursor.isAfterLast()) {
                RelativesData relatives = new RelativesData(cursor.getString(cursor.getColumnIndex(DatabaseHelper.RELATIVES_FIRST_NAME)),cursor.getString(cursor.getColumnIndex(DatabaseHelper.RELATIVES_LAST_NAME)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.RELATIVES_ICON)),cursor.getString(cursor.getColumnIndex(DatabaseHelper.RELATIVES_ID)));
                relativesData.add(relatives);
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        dbHelper.close();
    }

    private void getDataEvent(){
        eventData.clear();
        cursor=db.rawQuery("select * from " + DatabaseHelper.TABLE_PHOTO + " asc limit 1", null);
        if (cursor .moveToFirst()) {
            checkId=cursor.getString(cursor.getColumnIndex(DatabaseHelper.ASSET_ALBUM_ID));
            cursor.moveToNext();
        }
        cursor.close();
        cursor = db.query(DatabaseHelper.TABLE_EVENT, null, null, null, null, null, null);
        if (cursor .moveToFirst()) {
            while (!cursor.isAfterLast()) {
                Log.d(TAG, "event data" + cursor.getString(cursor.getColumnIndex(DatabaseHelper.EVENT_DATE)) + " " +cursor.getString(cursor.getColumnIndex(DatabaseHelper.EVENT_TITLE)));
                EventData event = new EventData(cursor.getString(cursor.getColumnIndex(DatabaseHelper.EVENT_DATE)),cursor.getString(cursor.getColumnIndex(DatabaseHelper.EVENT_TITLE)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.EVENT_ID)), cursor.getString(cursor.getColumnIndex(DatabaseHelper.EVENT_ALBUM_ASSET_ID)));
                eventData.add(event);
                cursor.moveToNext();
            }
        }
        cursor.close();
        String[] columnsAlbumId={DatabaseHelper.ASSET_ALBUM_ID, DatabaseHelper.ASSET_RESOURCE};
        cursor = db.query(DatabaseHelper.TABLE_PHOTO, columnsAlbumId, null, null, null, null, null);
        if (cursor .moveToFirst()) {
            while (!cursor.isAfterLast()) {
                if(cursor.getString(cursor.getColumnIndex(DatabaseHelper.ASSET_ALBUM_ID)).equals(checkId)) {
                    Log.d(TAG,"add event resource" + cursor.getString(cursor.getColumnIndex(DatabaseHelper.ASSET_RESOURCE)));
                    albumResource.add(cursor.getString(cursor.getColumnIndex(DatabaseHelper.ASSET_RESOURCE)));
                }
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        dbHelper.close();
    }

    private void getDataAlbum(){
        String checkId;
        albumData.clear();
        String myId = sharedPreferences.getString("myId","");
        Log.d(TAG, "my own id " + sharedPreferences.getString("myId", ""));
        cursor=db.rawQuery("select * from " + DatabaseHelper.TABLE_PHOTO + " asc limit 1", null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                checkId=cursor.getString(cursor.getColumnIndex(DatabaseHelper.ASSET_ALBUM_ID));
                Log.d(TAG, "checkId " + checkId);
                cursor.moveToNext();
                break;
            }
        }
        cursor.close();
        String[] columns = new String[]{ DatabaseHelper.ALBUM_COVER, DatabaseHelper.ALBUM_COVER_TITLE, DatabaseHelper.ALBUM_ID};
        cursor=db.query(DatabaseHelper.TABLE_ALBUM,columns,null,null,null,null,null);
        if (cursor .moveToFirst()) {
            while (!cursor.isAfterLast()) {
                if(!myId.equals(cursor.getString(cursor.getColumnIndex(DatabaseHelper.ALBUM_ID)))) {
                    Log.d(TAG, "in the album " + cursor.getString(cursor.getColumnIndex(DatabaseHelper.ALBUM_COVER)) + " " +cursor.getString(cursor.getColumnIndex(DatabaseHelper.ALBUM_COVER_TITLE)) + " " +
                            cursor.getString(cursor.getColumnIndex(DatabaseHelper.ALBUM_ID)));
                    AlbumData album = new AlbumData(cursor.getString(cursor.getColumnIndex(DatabaseHelper.ALBUM_COVER)), cursor.getString(cursor.getColumnIndex(DatabaseHelper.ALBUM_COVER_TITLE)),
                            cursor.getString(cursor.getColumnIndex(DatabaseHelper.ALBUM_ID)));
                    albumData.add(album);
                }
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        dbHelper.close();
    }

    private void getDataPhoto() {
        photoData.clear();
        cursor = db.query(DatabaseHelper.TABLE_PHOTO, null, null, null, null, null, null);
        Log.d(TAG, "cursor count " + cursor.getCount());
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                if (cursor.getString(cursor.getColumnIndex(DatabaseHelper.ASSET_ALBUM_ID)).equals(checkAlbumId)) {
                    PhotoData photo = new PhotoData();
                    if (cursor.getString(cursor.getColumnIndex(DatabaseHelper.ASSET_TITLE)) == null ||
                            cursor.getString(cursor.getColumnIndex(DatabaseHelper.ASSET_TITLE)).equals("")) {
                        photo.setAssetTitle("empty");
                    } else {
                        photo.setAssetTitle(cursor.getString(cursor.getColumnIndex(DatabaseHelper.ASSET_TITLE)));
                    }
                    photo.setAssetResource(cursor.getString(cursor.getColumnIndex(DatabaseHelper.ASSET_RESOURCE)));
                    photo.setAssetType(cursor.getString(cursor.getColumnIndex(DatabaseHelper.ASSET_TYPE)));
                    photoData.add(photo);
                }
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        dbHelper.close();
    }

    private void updateTableById(String table, ContentValues updateValues, String updateId){
        db.beginTransaction();
        try {
            db.update(table, updateValues, "id = ?", new String[]{updateId});
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        dbHelper.close();
        db.close();
    }

    private boolean deleteFileRowById(String table, String column, String deleteId, String rowImage) {
        String path;
        String[] columns = new String[]{column};
        Cursor c = db.query(table, columns, column + "=" + deleteId, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
            path = c.getString(c.getColumnIndex(rowImage));
            c.close();
        } else {
            db.close();
            dbHelper.close();
            return false;
        }
        db.delete(table, column + "=?", new String[]{deleteId});
        if (table.equals(DatabaseHelper.TABLE_ALBUM)) {
            String[] photoColumn = new String[]{DatabaseHelper.ASSET_ALBUM_ID, DatabaseHelper.ASSET_RESOURCE};
            Cursor cursor = db.query(DatabaseHelper.TABLE_PHOTO, photoColumn, DatabaseHelper.ASSET_ALBUM_ID + "=" + deleteId, null, null, null, null);
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    File deletePhoto = new File(cursor.getString(cursor.getColumnIndex("resource")));
                    if (deletePhoto.exists())
                        deletePhoto.delete();
                    cursor.moveToNext();
                }
            }
            cursor.close();
        }
        db.close();
        dbHelper.close();
        File deleteFile = new File(path);
        return deleteFile.exists() && deleteFile.delete();
    }
}
