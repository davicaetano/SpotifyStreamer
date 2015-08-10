package com.example.android.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;


public class TrackMain extends ActionBarActivity implements TrackFragment.clickTrackInterface {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        setContentView(R.layout.activity_song);
        if(savedInstanceState == null) {
            TrackFragment trackFragment = new TrackFragment();
            Intent intent = getIntent();
            Bundle arguments = new Bundle();
            arguments.putString("artistName",intent.getStringExtra("artistName"));
            arguments.putString("artistId",intent.getStringExtra("artistId"));
            trackFragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_song_container, trackFragment).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_default, menu);
        getMenuInflater().inflate(R.menu.menu_now_playing, menu);
        return true;
    }

    public void clickTrack(String uri, int position, boolean first){
        Intent intent = new Intent(this, PlayerMain.class);
        intent.putExtra("uri", uri);
        intent.putExtra("position", position);
        intent.putExtra("first", first);
        this.startActivity(intent);
    }
}
