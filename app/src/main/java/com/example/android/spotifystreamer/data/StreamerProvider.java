package com.example.android.spotifystreamer.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;


// Please don't consider this code. I'm trying to do it by myself just to learn how to work
// with Sqlite and Content Provider. This is not part of my final code.



/**
 * Created by davi on 6/29/15.
 */
public class StreamerProvider extends ContentProvider {
    private UriMatcher uriMatcher = buildUriMatcher();
    private StreamerDbHelper dbHelper;
    @Override
    public boolean onCreate() {
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    private UriMatcher buildUriMatcher(){
        return null;
    }
}
