package com.grannyos.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Database helper for save data to local storage
 */

public class DatabaseHelper extends SQLiteOpenHelper {


    public static final String TABLE_RELATIVES = "relatives";
    public static final String TABLE_ALBUM = "album";
    public static final String TABLE_EVENT = "event";
    public static final String TABLE_PHOTO = "photo";


    public static final String RELATIVES_ID = "id";
    public static final String RELATIVES_FIRST_NAME = "firstName";
    public static final String RELATIVES_LAST_NAME = "lastName";
    public static final String RELATIVES_ICON= "image";
    public static final String RELATIVES_MISSING_CALL= "missing";


    public static final String ALBUM_ID = "id";
    public static final String ALBUM_COVER = "cover";
    public static final String ALBUM_COVER_TITLE= "coverTitle";
    public static final String ALBUM_PROVIDER= "provider";
    public static final String ALBUM_CREATED_AT = "createdAt";
    public static final String ALBUM_UPDATED_AT= "updatedAt";

    public static final String ASSET_ID = "id";
    public static final String ASSET_ALBUM_ID = "idAlbum";
    public static final String ASSET_TITLE = "title";
    public static final String ASSET_RESOURCE = "resource";
    public static final String ASSET_TYPE = "type";

    public static final String EVENT_ID= "id";
    public static final String EVENT_TITLE = "title";
    public static final String EVENT_DATE= "date";
    public static final String EVENT_ALBUM_ASSET_ID= "albumAssetId";


    private static final String CREATE_TABLE_RELATIVES = "create table if not exists "
            + TABLE_RELATIVES + " (" + RELATIVES_ID + " text primary key, "
            +  RELATIVES_FIRST_NAME + " text not null, "  + RELATIVES_LAST_NAME + " text, " + RELATIVES_ICON +
            " text, " + RELATIVES_MISSING_CALL + " integer);";

    private static final String CREATE_TABLE_ALBUM = "create table if not exists "
            + TABLE_ALBUM + " (" + ALBUM_ID + " text primary key, "
            +  ALBUM_COVER + " text, "  + ALBUM_COVER_TITLE + " text, " + ALBUM_PROVIDER + " text, " +
            ALBUM_CREATED_AT + " text, "  + ALBUM_UPDATED_AT + " text);";

    private static final String CREATE_TABLE_PHOTO = "create table if not exists "
            + TABLE_PHOTO + " (" + ASSET_ID + " text primary key, "
            + ASSET_ALBUM_ID + " text, " +  ASSET_TITLE + " text, "  + ASSET_RESOURCE + " text, " + ASSET_TYPE + " text);";

    private static final String CREATE_TABLE_EVENT = "create table if not exists "
            + TABLE_EVENT + " (" + EVENT_ID + " text primary key, "
            +  EVENT_TITLE + " text, "  + EVENT_DATE + " text, " + EVENT_ALBUM_ASSET_ID +
            " text);";


    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_RELATIVES);
        db.execSQL(CREATE_TABLE_PHOTO);
        db.execSQL(CREATE_TABLE_EVENT);
        db.execSQL(CREATE_TABLE_ALBUM);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE_RELATIVES);
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE_PHOTO);
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE_EVENT);
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE_ALBUM);
        onCreate(db);
    }
}
