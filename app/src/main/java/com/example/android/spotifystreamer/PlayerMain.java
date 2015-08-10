package com.example.android.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public class PlayerMain extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        setContentView(R.layout.activity_player);
        if(savedInstanceState == null){
            Intent intent = getIntent();
            boolean first = intent.getBooleanExtra("first", false);
            String uri = intent.getStringExtra("uri");
            int position = intent.getIntExtra("position", 0);
            PlayerFragment playerFragment = PlayerFragment.newInstance(uri, position, first);
            android.app.FragmentTransaction FT = getFragmentManager().beginTransaction();
            FT.add(R.id.fragment_player_container, playerFragment).commit();
        }
    }
}