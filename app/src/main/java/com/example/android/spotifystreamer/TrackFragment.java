package com.example.android.spotifystreamer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;


/**
 * A placeholder fragment containing a simple view.
 */
public class TrackFragment extends Fragment {
    private String artist_id = "";
    private String artist_name = "";
    private ArrayList<Track> songArrayList = new ArrayList<Track>();
    private TrackAdapter trackAdapter;
    private boolean started = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        this.setRetainInstance(true);
        View rootView = inflater.inflate(R.layout.fragment_song, container, false);

        Intent intent = getActivity().getIntent();
        if(intent != null && intent.hasExtra("strings")){
            String[] extra = intent.getStringArrayExtra("strings");
            artist_id = extra[0];
            artist_name = extra[1];
            getActivity().setTitle(artist_name);
        }



        ListView listView = (ListView)rootView.findViewById(R.id.listview_song);
        trackAdapter = new TrackAdapter(getActivity(),R.layout.list_item_song, songArrayList);

        listView.setAdapter(trackAdapter);
        if (started == false) {
            update();
            started = true;
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
        return rootView;
    }



    /////////////////////////////////////////////////////////////////////////////////////////////
    //
    ///
    ///
    ///
    //
    //
    /////////////////////////////////////////////////////////////////////////////////////////////

    private void update(){

        FetchSongTask fetchSongTask = new FetchSongTask();
        fetchSongTask.execute(artist_id);
    }

    /////////////////////////////////////////////////////////////////////////////////////////////
    //  Innner classes
    ///
    ///
    ///
    //
    //
    /////////////////////////////////////////////////////////////////////////////////////////////

    public class FetchSongTask extends AsyncTask<String,Void,ArrayList<kaaes.spotify.webapi.android.models.Track>> {
        private SpotifyApi api = new SpotifyApi();
        private SpotifyService spotify;
        private String erro = "";


        @Override
        protected ArrayList<kaaes.spotify.webapi.android.models.Track> doInBackground(String... params){
            ArrayList<kaaes.spotify.webapi.android.models.Track> mList = new ArrayList<kaaes.spotify.webapi.android.models.Track>();
            try {
                SpotifyApi api = new SpotifyApi();
                SpotifyService spotify = api.getService();

                String country = Locale.getDefault().getCountry();
                if (country.equals("")) {
                    country = "US";
                }
                Map<String, Object> ct = new HashMap<String, Object>();
                ct.put("country", "US");
                List<kaaes.spotify.webapi.android.models.Track> results = spotify.getArtistTopTrack(params[0], ct).tracks;
                mList = (ArrayList) results;
            }catch (Exception e){
                erro = "net";
            }

            return mList;
        }

        @Override
        protected void onPostExecute(ArrayList<kaaes.spotify.webapi.android.models.Track> tracks) {
            super.onPostExecute(tracks);
            trackAdapter.clear();

            if(tracks.size() > 0){
                for(kaaes.spotify.webapi.android.models.Track track : tracks){
                    Track newone = new Track();
                    newone.setSong(track.name);
                    newone.setId(track.id);
                    newone.setAlbum(track.album.name);
                    if(track.album.images.size() > 0){
                        //The code below is to get the lighter image that is best to this phone.
                        //For example, for a phone xhdpi it's needed an image with at least
                        //64* 2 = 128 pixels to cover the ImageView
                        //Note that I use the R.dimen.artist_height variable to don't
                        //hard code the size of thImageView
                        float height = getResources().getDimension(R.dimen.album_height);
                        for (int i = track.album.images.size()-1; i >= 0;i--) {
                            if (((float)track.album.images.get(i).height >= height ) || i == 0){
                                newone.setImage(Uri.parse(track.album.images.get(i).url));
                                break;
                            }
                        }
                    }else{

                    }
                    trackAdapter.add(newone);
                }
            }else{
                String message = (erro == "net")?getString(R.string.connexion_error):String.format(getString(R.string.artist_does_not_have_song), artist_name);
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                alert.setTitle("Spotify Streamer");
                alert.setMessage(message);
                alert.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().finish();
                    }
                });
                alert.show();

            }
        }
    }
}