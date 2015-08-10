package com.example.android.spotifystreamer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.example.android.spotifystreamer.data.StreamerContract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;

public class TrackAsyncTask extends AsyncTask<String,Void,String[]> {
    private SpotifyApi api;
    private SpotifyService spotify;
    private String erro = "";
    private Activity mActivity;

    public TrackAsyncTask(Activity activity){mActivity = activity;}

    private String RecordOnDB (ArrayList<kaaes.spotify.webapi.android.models.Track> list, String[] params){
        ContentValues cv;
        Uri uri = StreamerContract.TrackEntry.buildTrackByArtist(params[0]);
        Cursor cursor = mActivity.getContentResolver().query(uri,null,null,null,null,null);
        if (list.size() > 0) {
            if (cursor.getCount() > 0) {
                mActivity.getContentResolver().delete(uri,null,null);
            }
            for (kaaes.spotify.webapi.android.models.Track cada : list) {
                cv = new ContentValues();
                cv.put(StreamerContract.TrackEntry.COLUMN_TRACK_NAME, cada.name);
                cv.put(StreamerContract.TrackEntry.COLUMN_ALBUM_NAME, cada.album.name);
                cv.put(StreamerContract.TrackEntry.COLUMN_TRACK_URL, cada.preview_url);
                cv.put(StreamerContract.TrackEntry.COLUMN_DURATION, cada.duration_ms);
                cv.put(StreamerContract.TrackEntry.COLUMN_TRACK_ID, cada.id);
                cv.put(StreamerContract.TrackEntry.COLUMN_ARTIST_ID, params[0]);
                cv.put(StreamerContract.TrackEntry.COLUMN_ARTIST_NAME, params[1]);
                if (cada.album.images.size() > 0) {
                    float height = mActivity.getResources().getDimension(R.dimen.album_height);
                    for (int i = cada.album.images.size() - 1; i >= 0; i--) {
                        if (((float) cada.album.images.get(i).height >= height) || i == 0) {
                            cv.put(StreamerContract.TrackEntry.COLUMN_IMAGE, cada.album.images.get(i).url);
                            break;
                        }
                    }
                } else {
                    cv.put(StreamerContract.TrackEntry.COLUMN_IMAGE, "");
                }
                mActivity.getContentResolver().insert(uri, cv);
            }
        }
        if (list.size() == 0 && cursor.getCount() == 0){
            return "1";//The list view is empty. The activity will show a error message onPostExecute
        }
        return "0";
    }

    @Override
    protected String[] doInBackground(String... params){
        ArrayList<kaaes.spotify.webapi.android.models.Track> mList = new ArrayList<kaaes.spotify.webapi.android.models.Track>();
        api = new SpotifyApi();
        spotify = api.getService();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mActivity);
        String country = prefs.getString("location","US");
        Map<String, Object> ct = new HashMap<String, Object>();
        ct.put("country", country);
        try {
            List<Track> results = spotify.getArtistTopTrack(params[0], ct).tracks;
            mList = (ArrayList) results;
        }catch (Exception e){
//            Log.e("davi","-->" +e.toString()+"<--");
            if(e.toString().equals("retrofit.RetrofitError: 400 Bad Request")){
                erro = "country";
            }else {
                erro = "net";
            }
        }
        return (new String[]{RecordOnDB(mList, params),params[1]});
    }

    @Override
    protected void onPostExecute(String[] par) {
        if (par[0].equals("1")) {
            super.onPostExecute(par);
            String message;
            if(erro.equals("net") ){
                message = mActivity.getString(R.string.connexion_error);
            }else if(erro.equals("country")){
                message = mActivity.getString(R.string.country_error);
            }else{
                message = String.format(mActivity.getString(R.string.artist_does_not_have_song), par[1]);
            }
            AlertDialog.Builder alert = new AlertDialog.Builder(mActivity);
            alert.setTitle("Spotify Streamer");
            alert.setMessage(message);
            alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    mActivity.finish();
                }
            });
            alert.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {mActivity.finish();
                }
            });
            alert.show();
        }
    }
}