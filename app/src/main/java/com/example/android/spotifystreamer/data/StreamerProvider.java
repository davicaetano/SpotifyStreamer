package com.example.android.spotifystreamer.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.preference.PreferenceManager;

public class StreamerProvider extends ContentProvider {
    private StreamerDbHelper streamerDbHelper;

    static UriMatcher buildUriMatcher(){
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = StreamerContract.CONTENT_AUTHORITY;
        matcher.addURI(authority,StreamerContract.PATH_TRACK + "/*",1);
        matcher.addURI(authority,StreamerContract.PATH_ARTIST,2);
        matcher.addURI(authority,StreamerContract.PATH_ARTIST +"/*",3);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        streamerDbHelper = new StreamerDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        UriMatcher matcher = buildUriMatcher();
        switch (matcher.match(uri)){
            case 1:return StreamerContract.TrackEntry.CONTENT_TYPE;
            case 2:return StreamerContract.ArtistEntry.CONTENT_TYPE;
            //case 3:return StreamerContract.ArtistEntry.CONTENT_ITEM_TYPE;
        }
        return null;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        UriMatcher matcher = buildUriMatcher();
        SQLiteDatabase db = streamerDbHelper.getReadableDatabase();
        Cursor cursor = null;
        int mat = matcher.match(uri);
        switch (mat) {
            case 1: {
                String artist_id = StreamerContract.TrackEntry.getArtistIdFromUri(uri);
                selection = StreamerContract.TrackEntry.TABLE_NAME+ "." + StreamerContract.TrackEntry.COLUMN_ARTIST_ID + " = ? AND "
                    + StreamerContract.TrackEntry.TABLE_NAME + "." + StreamerContract.TrackEntry.COLUMN_COUNTRY + " = ? ";
                selectionArgs = new String[]{artist_id, PreferenceManager.getDefaultSharedPreferences(getContext()).getString("location","US")};
                cursor = db.query(StreamerContract.TrackEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case 2: {
                cursor = db.query(StreamerContract.ArtistEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        UriMatcher matcher = buildUriMatcher();
        int cont = -1;
        SQLiteDatabase db = streamerDbHelper.getReadableDatabase();
        int mat = matcher.match(uri);
        switch (mat) {
            case 1: {
                String artist_id = StreamerContract.TrackEntry.getArtistIdFromUri(uri);
                selection = StreamerContract.TrackEntry.TABLE_NAME+ "." + StreamerContract.TrackEntry.COLUMN_ARTIST_ID + " = ? AND "
                        + StreamerContract.TrackEntry.TABLE_NAME + "." + StreamerContract.TrackEntry.COLUMN_COUNTRY + " = ? ";
                selectionArgs = new String[]{artist_id, PreferenceManager.getDefaultSharedPreferences(getContext()).getString("location","US")};
                cont = db.delete(StreamerContract.TrackEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case 2: {
                cont = db.delete(StreamerContract.ArtistEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return cont;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        UriMatcher matcher = buildUriMatcher();
        Long saida = -1L;
        SQLiteDatabase db = streamerDbHelper.getReadableDatabase();
        switch (matcher.match(uri)) {
            case 1: {
                values.put(StreamerContract.TrackEntry.COLUMN_COUNTRY,PreferenceManager.getDefaultSharedPreferences(getContext()).getString("location","US"));
                saida = db.insert(StreamerContract.TrackEntry.TABLE_NAME, null, values);
                break;
            }
            case 2: {
                saida = db.insert(StreamerContract.ArtistEntry.TABLE_NAME, null, values);
                break;
            }
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return StreamerContract.TrackEntry.buildTrackUri(saida);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}