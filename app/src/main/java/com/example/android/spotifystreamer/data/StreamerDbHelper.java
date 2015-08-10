package com.example.android.spotifystreamer.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class StreamerDbHelper extends SQLiteOpenHelper{

    static final String DATABASE_NAME = "streamer.db";
    private static final int DATABASE_VERSION = 1;

    public StreamerDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_TRACK_TABLE = "CREATE TABLE " + StreamerContract.TrackEntry.TABLE_NAME + " (" +
                StreamerContract.TrackEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                StreamerContract.TrackEntry.COLUMN_TRACK_ID + "  TEXT NOT NULL, " +
                StreamerContract.TrackEntry.COLUMN_ARTIST_ID + "  TEXT NOT NULL, " +
                StreamerContract.TrackEntry.COLUMN_ARTIST_NAME + "  TEXT NOT NULL, " +
                StreamerContract.TrackEntry.COLUMN_TRACK_NAME + " TEXT NOT NULL, " +
                StreamerContract.TrackEntry.COLUMN_ALBUM_NAME + " TEXT NOT NULL, " +
                StreamerContract.TrackEntry.COLUMN_IMAGE + " TEXT NOT NULL, " +
                StreamerContract.TrackEntry.COLUMN_TRACK_URL + " TEXT NOT NULL, " +
                StreamerContract.TrackEntry.COLUMN_DURATION + " INTEGER NOT NULL, " +
                StreamerContract.TrackEntry.COLUMN_COUNTRY + " INTEGER NOT NULL " +
                ");";

        final String SQL_CREATE_ARTIST_TABLE = "CREATE TABLE " + StreamerContract.ArtistEntry.TABLE_NAME + " (" +
                StreamerContract.ArtistEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                StreamerContract.ArtistEntry.COLUMN_ARTIST_ID + "  TEXT NOT NULL, " +
                StreamerContract.ArtistEntry.COLUMN_IMAGE + "  TEXT NOT NULL, " +
                StreamerContract.ArtistEntry.COLUMN_ARTIST_NAME + "  TEXT NOT NULL" +
                ");";

        db.execSQL(SQL_CREATE_TRACK_TABLE);
        db.execSQL(SQL_CREATE_ARTIST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + StreamerContract.TrackEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + StreamerContract.ArtistEntry.TABLE_NAME);
        onCreate(db);
    }
}