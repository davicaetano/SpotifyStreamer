package com.example.android.spotifystreamer;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.WindowManager;

public class ArtistMain extends ActionBarActivity implements TrackFragment.clickTrackInterface{
    private final String TFTAG = "TFTAG";
    private boolean mTwoPane;
    private boolean firstDialog = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist);
        if(findViewById(R.id.fragment_song_container) != null){
            mTwoPane = true;
        }else{
            mTwoPane = false;
        }

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_default, menu);
        getMenuInflater().inflate(R.menu.menu_now_playing, menu);
        return true;
    }

    @Override
    public void onBackPressed() {//To stop in case the user wants to close the app
        ArtistFragment artistFragment = (ArtistFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_artist);
        if(artistFragment != null) {
            artistFragment.backPressed();
        }
        super.onBackPressed();
    }

    public void clickArtist(String artistName, String artistId){//Case the user click in an artist
        if(mTwoPane){
            TrackFragment trackFragment = new TrackFragment();
            Bundle bundle = new Bundle();
            bundle.putString("artistName",artistName);
            bundle.putString("artistId",artistId);
            trackFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_song_container,trackFragment,TFTAG).commit();
        }else{
            Intent intent = new Intent(this, TrackMain.class);
            intent.putExtra("artistName",artistName );
            intent.putExtra("artistId", artistId);
            startActivity(intent);
        }
    }

    public void clickTrack(String uri, int position, boolean first){//case the user click in a song
        if(firstDialog) {//there is a bug where the share options is not shown by the first time. this if solved this problem.
            PlayerFragment playerFragment = PlayerFragment.newInstance(uri, -10, first);
            FragmentManager FM = getFragmentManager();
            playerFragment.show(FM, "dialog");
            playerFragment.dismiss();
            playerFragment = PlayerFragment.newInstance(uri, position, first);
            playerFragment.show(FM, "dialog");
            firstDialog = false;
        }else{
            PlayerFragment playerFragment = PlayerFragment.newInstance(uri, position, first);
            FragmentManager FM = getFragmentManager();
            playerFragment.show(FM, "dialog");
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {//when the user open the notification.
        super.onNewIntent(intent);
        if(intent.hasExtra("twoPane")){
            if(intent.getBooleanExtra("twoPane",false)){
                FragmentManager FM = getFragmentManager();
                PlayerFragment playerFragment = (PlayerFragment)FM.findFragmentByTag("dialog");
                if(playerFragment == null) {
                    boolean first = intent.getBooleanExtra("first", false);
                    String uri = intent.getStringExtra("uri");
                    int position = intent.getIntExtra("position", 0);
                    playerFragment = PlayerFragment.newInstance(uri, position, first);
                    playerFragment.show(FM, "dialog");
                }
            }
        }
    }

    public boolean getmTwoPane(){
        return mTwoPane;
    }
}
