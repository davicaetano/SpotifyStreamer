package com.example.android.spotifystreamer;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Pager;



/////////////////////////////////////////////////////////////////////////////////////////////


public class ArtistFragment extends Fragment {
    private ArtistAdapter artistAdapter;
    private ArrayList<Artist> artistArrayList = new ArrayList<Artist>();
    public Toast toast;
    private Intent intent = null;

    /////////////////////////////////////////////////////////////////////////////////////////////

    public ArtistFragment() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.setRetainInstance(true);

        toast= new Toast(getActivity());
        View rootView = inflater.inflate(R.layout.fragment_artist, container, false);

        ListView listView = (ListView)rootView.findViewById(R.id.listview_artist);

        artistAdapter = new ArtistAdapter(getActivity(),R.layout.list_item_artist,artistArrayList);

        listView.setAdapter(artistAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String artist_id = artistAdapter.getItem(position).getId();
                String artist_name = artistAdapter.getItem(position).getArtist_name();
                String[] output = new String[]{artist_id, artist_name};
                    intent = new Intent(getActivity(), TrackMain.class).putExtra("strings", output);
                    startActivity(intent);
            }
        });

        TextView textView = (TextView)rootView.findViewById(R.id.editText_artist);
        textView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == KeyEvent.KEYCODE_ENTER||actionId == EditorInfo.IME_ACTION_DONE){
                    updateArtist();
                }
                return false;
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


    public void updateArtist(){
        FetchArtistTask artist_task = new FetchArtistTask(getActivity());
        EditText mEdit = (EditText) getActivity().findViewById(R.id.editText_artist);
        String search = mEdit.getText().toString();
        artist_task.execute(search);
    }


    /////////////////////////////////////////////////////////////////////////////////////////////
    //  Innner classes
    ///
    ///
    ///
    //
    //
    /////////////////////////////////////////////////////////////////////////////////////////////

    public class FetchArtistTask extends AsyncTask<String,Integer,ArrayList<kaaes.spotify.webapi.android.models.Artist>> {
        private SpotifyApi api = new SpotifyApi();
        private SpotifyService spotify;
        private Context mContext;
        private String erro = "";

        public FetchArtistTask(Context context){
            mContext = context;
        }

        @Override
        protected ArrayList<kaaes.spotify.webapi.android.models.Artist> doInBackground(String... params){
            ArrayList<kaaes.spotify.webapi.android.models.Artist> mList = new ArrayList<kaaes.spotify.webapi.android.models.Artist>();
            try{
                spotify = api.getService();
                ArtistsPager results = spotify.searchArtists(params[0]);
                Pager<kaaes.spotify.webapi.android.models.Artist> mPager = results.artists;
                mList = (ArrayList)mPager.items;
            }
            catch (Exception e){
                erro = "net";
            }
            return mList;
        }

        @Override
        protected void onPostExecute(ArrayList<kaaes.spotify.webapi.android.models.Artist> artists) {
            super.onPostExecute(artists);
            artistAdapter.clear();
            toast.cancel();
            if (artists.size() > 0) {
                for (kaaes.spotify.webapi.android.models.Artist artist : artists) {
                    Artist newone = new Artist();
                    newone.setArtist_name(artist.name);
                    newone.setId(artist.id);
                    if (artist.images.size() > 0) {
                        //The code below is to get the lighter image that is best to this phone.
                        //For example, for a phone xhdpi it's needed an image with at least
                        //64* 2 = 128 pixels to cover the ImageView
                        //Note that I use the R.dimen.artist_height variable to don't
                        //hard code the size of thImageView
                        float height = getResources().getDimension(R.dimen.artist_height);
                        for (int i = artist.images.size()-1; i >= 0;i--) {
                            if (((float)artist.images.get(i).height >= height ) || i == 0){
                                newone.setImage(Uri.parse(artist.images.get(i).url));
                                break;
                            }
                        }
                    } else {

                    }
                    artistAdapter.add(newone);
                }
            }else{
                String message = erro.equals("net") ? getString(R.string.connexion_error) : getString(R.string.artist_not_found);
                toast.makeText(mContext, message, toast.LENGTH_SHORT).show();
            }
        }

    }
}