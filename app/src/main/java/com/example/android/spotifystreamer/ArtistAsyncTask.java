package com.example.android.spotifystreamer;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.android.spotifystreamer.data.StreamerContract;

import java.util.ArrayList;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Pager;

public class ArtistAsyncTask extends AsyncTask<String,Void,String> {
    private SpotifyApi api = new SpotifyApi();
    private SpotifyService spotify;
    private Context mContext;
    private String erro = "";

    public ArtistAsyncTask(Context context){
        mContext = context;
    }
    private String RecordOnDB(ArrayList<kaaes.spotify.webapi.android.models.Artist> mList) {
        if (mList.size() == 0) return "1";
        mContext.getContentResolver().delete(StreamerContract.ArtistEntry.CONTENT_URI,null,null);
        ContentValues cv = null;
        for(kaaes.spotify.webapi.android.models.Artist cada:mList) {
            cv = new ContentValues();
            cv.put(StreamerContract.ArtistEntry.COLUMN_ARTIST_ID,cada.id);
            cv.put(StreamerContract.ArtistEntry.COLUMN_ARTIST_NAME,cada.name);
            if (cada.images.size() > 0) {
                float height = mContext.getResources().getDimension(R.dimen.artist_height);
                for (int i = cada.images.size()-1; i >= 0;i--) {
                    if (((float)cada.images.get(i).height >= height ) || i == 0){
                        cv.put(StreamerContract.ArtistEntry.COLUMN_IMAGE,cada.images.get(i).url);
                        break;
                    }
                }
            } else {
                cv.put(StreamerContract.TrackEntry.COLUMN_IMAGE, "");
            }
            mContext.getContentResolver().insert(StreamerContract.ArtistEntry.CONTENT_URI, cv);
        }
        return "0";
    }
    @Override
    protected String doInBackground(String... params){
        ArrayList<kaaes.spotify.webapi.android.models.Artist> mList = new ArrayList<kaaes.spotify.webapi.android.models.Artist>();
        try{
            spotify = api.getService();
            ArtistsPager results = spotify.searchArtists(params[0]);
            Pager<Artist> mPager = results.artists;
            mList = (ArrayList)mPager.items;
        }
        catch (Exception e){
            erro = "net";
        }
        return (RecordOnDB(mList));
    }

    @Override
    protected void onPostExecute(String string) {
        super.onPostExecute(string);
        if (string == "1"){
            String message = erro.equals("net") ? mContext.getString(R.string.connexion_error) : mContext.getString(R.string.artist_not_found);
            Toast.makeText(mContext, message,Toast.LENGTH_SHORT).show();
        }
    }
}