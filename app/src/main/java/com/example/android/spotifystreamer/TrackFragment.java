package com.example.android.spotifystreamer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.spotifystreamer.data.StreamerContract;

public class TrackFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private String artistId;
    private String artistName;
    private String countryCode = "";
    private TrackAdapter trackAdapter;

    private Boolean mBound = false;
    private PlayerService mService = null;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PlayerService.LocalBinder binder = (PlayerService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
            checkSett();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };
    private void disconnect(){
        getActivity().unbindService(mConnection);
        mBound = false;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_song, container, false);
        Bundle arguments = getArguments();
        if (arguments != null) {
            artistId = arguments.getString("artistId");
            artistName = arguments.getString("artistName");
            getActivity().setTitle(artistName);
        }
        ListView listView = (ListView) rootView.findViewById(R.id.listview_song);
        trackAdapter = new TrackAdapter(getActivity(), null, 0);

        listView.setAdapter(trackAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String uri = StreamerContract.TrackEntry.buildTrackByArtist(artistId).toString();
                ((clickTrackInterface)getActivity()).clickTrack(uri,position,true);
            }
        });
        if(savedInstanceState == null) {
            update();
        }
        getLoaderManager().initLoader(0, null, this);
        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.now_playing && !getActivity().getLocalClassName().equals("ArtistMain")){
            if(mService.isLoaded()) {
                Intent intent = new Intent(getActivity(), PlayerMain.class);
                intent.putExtra("uri", StreamerContract.TrackEntry.buildTrackByArtist(artistId).toString());
                intent.putExtra("position", -1);
                intent.putExtra("first", false);
                getActivity().startActivity(intent);
            }else{
                Toast.makeText(getActivity(),"There is no music playing now. Select a song.",Toast.LENGTH_SHORT).show();
            }
        }else if (id == R.id.action_settings) {
//            Log.v("davi","onOptionsItemSelected do TrackFragment com id == R.id.action_settings");
            countryCode = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("location","US");
            startActivity(new Intent(getActivity(),SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
//        Log.v("davi","onResume do TrackFragment");
        checkSett();
    }

    private void checkSett(){
        if(!countryCode.equals("")) {
            if(mBound) {
                if(mService.isLoaded()) {
                    mService.notif();
                }
                String location = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("location", "US");
                if (countryCode.equals(location) == false) {
                    update();
                    countryCode = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("location", "US");
                    if(mService.isLoaded()){
                        mService.stop();
                        mService.reset();
                        mService.exit();
                        mService.stopNotif();
                    }
                }
                countryCode = "";
            }
        }
    }
    private void update(){
        getActivity().setTitle(artistName);
        TrackAsyncTask trackAsyncTask = new TrackAsyncTask(getActivity());
        trackAsyncTask.execute(artistId, artistName);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!mBound) {
            getActivity().bindService(new Intent(getActivity(), PlayerService.class), mConnection, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mBound){
            disconnect();
        }
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        if(savedInstanceState != null){
            artistId = savedInstanceState.getString("artistId","");
            artistName = savedInstanceState.getString("artistName","");
            countryCode = savedInstanceState.getString("countryCode","");
        }
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("artistId",artistId);
        outState.putString("artistName",artistName);
        outState.putString("countryCode",countryCode);
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Intent intent = getActivity().getIntent();
        if (intent == null) return null;
        Uri uri = StreamerContract.TrackEntry.buildTrackByArtist(artistId);
        return new CursorLoader(getActivity(),uri,null,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        trackAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        trackAdapter.swapCursor(null);
    }

    public interface clickTrackInterface{
        public void clickTrack(String uri, int position, boolean first);
    }
}