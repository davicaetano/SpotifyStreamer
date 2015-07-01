package com.example.android.spotifystreamer.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import java.util.HashSet;

/**
 * Created by davi on 6/29/15.
 */
public class DataTest extends AndroidTestCase{
    //void delete_Database(){
    //    mContext.deleteDatabase(StreamerDbHelper.DATABASE_NAME);
    //}

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        //delete_Database();
    }

    public void testCreateDb() throws Throwable {
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(StreamerContract.TrackEntry.TABLE_NAME);
        tableNameHashSet.add(StreamerContract.ArtistEntry.TABLE_NAME);
        mContext.deleteDatabase(StreamerDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new StreamerDbHelper(mContext).getWritableDatabase();
        assertTrue(db.isOpen());

        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue(cursor.moveToFirst());

        do{
            Log.v("davi",cursor.getString(0));
            tableNameHashSet.remove(cursor.getString(0));
        }while(cursor.moveToNext());

        assertTrue(tableNameHashSet.isEmpty());


    }
    public void testReadDb(){

    }
}
