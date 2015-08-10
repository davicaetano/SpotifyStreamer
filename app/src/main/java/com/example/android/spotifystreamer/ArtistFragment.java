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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.spotifystreamer.data.StreamerContract;

public class ArtistFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private ArtistAdapter artistAdapter;
    private String countryCode;
    private ComponentName componentName = null;
    private Intent intentService;

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
        if(mBound) {
            getActivity().unbindService(mConnection);
            mBound = false;
        }
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        countryCode = "";
        View rootView = inflater.inflate(R.layout.fragment_artist, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.listview_artist);
        artistAdapter = new ArtistAdapter(getActivity(), null, 0);
        listView.setAdapter(artistAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor)artistAdapter.getItem(position);
                int i = cursor.getCount();
                String artistName = cursor.getString(cursor.getColumnIndex(StreamerContract.ArtistEntry.COLUMN_ARTIST_NAME));
                String artistId = cursor.getString(cursor.getColumnIndex(StreamerContract.ArtistEntry.COLUMN_ARTIST_ID));
                ((ArtistMain)getActivity()).clickArtist(artistName, artistId);
            }
        });

        TextView textView = (TextView) rootView.findViewById(R.id.editText_artist);
        textView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE) {
                    update();
                }
                return false;
            }
        });
        getLoaderManager().initLoader(0, null, this);
        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.now_playing){
            if(mService.isLoaded()) {
                String uri = mService.getUri();
                int position =  mService.getPosition();
//                Log.v("davi","onOptionsItemSelected tem uri = " + uri);
                if(((ArtistMain)getActivity()).getmTwoPane()){
                    PlayerFragment playerFragment = PlayerFragment.newInstance(uri, position, false);
                    playerFragment.show(getActivity().getFragmentManager(),"dialog");
                }else {
                    Intent intent = new Intent(getActivity(), PlayerMain.class);
                    intent.putExtra("uri", uri);
                    intent.putExtra("position", -1);
                    intent.putExtra("first", false);
                    startActivity(intent);
                }
            }else{
                Toast.makeText(getActivity(),"There is no music playing now. Select an artist and a song.",Toast.LENGTH_SHORT).show();
            }
        }else if (id == R.id.action_settings) {
            countryCode = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("location", "US");
            if(!((ArtistMain)getActivity()).getmTwoPane()) {
//                Log.v("davi", "onOptionsItemSelected do ArtistFragment com id == R.id.action_settings");
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void update() {
        ArtistAsyncTask artistAsyncTask = new ArtistAsyncTask(getActivity());
        EditText mEdit = (EditText) getActivity().findViewById(R.id.editText_artist);
        String search = mEdit.getText().toString();
        artistAsyncTask.execute(search);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disconnect();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(componentName == null) {
            intentService = new Intent(getActivity(),PlayerService.class);
            intentService.putExtra("twoPane",((ArtistMain)getActivity()).getmTwoPane());
            componentName = getActivity().startService(intentService);
        }
        if(!mBound){
            getActivity().bindService(new Intent(getActivity(), PlayerService.class), mConnection, Context.BIND_AUTO_CREATE);
        }
    }
    @Override
    public void onResume() {
        super.onResume();
//        Log.v("davi", "onResume do ArtistFragment");
        checkSett();
    }

    private void checkSett(){
        if(!countryCode.equals("")) {
            if(mBound) {
                if(mService.isLoaded()) {
                    mService.notif();
                    String location = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("location", "US");
                    if (countryCode.equals(location) == false) {
                        countryCode = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("location", "US");
                        mService.stop();
                        mService.reset();
                        mService.exit();
                        mService.stopNotif();
                    }
                    countryCode = "";
                }
            }
        }
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        if(savedInstanceState != null){
            countryCode = savedInstanceState.getString("countryCode","");

        }
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("countryCode",countryCode);
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        Uri uri = StreamerContract.ArtistEntry.CONTENT_URI;
        return new CursorLoader(getActivity(),uri,null,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data.moveToFirst() == true) {
            artistAdapter.swapCursor(data);
        }
        else {
            artistAdapter.swapCursor(null);
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        artistAdapter.swapCursor(null);
    }
    public void backPressed(){
        mService.exit();
        mService.reset();
        mService.stopNotif();
    }
}