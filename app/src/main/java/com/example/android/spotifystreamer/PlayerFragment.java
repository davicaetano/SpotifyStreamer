package com.example.android.spotifystreamer;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.spotifystreamer.data.StreamerContract;
import com.squareup.picasso.Picasso;

import java.math.BigDecimal;

public class PlayerFragment extends DialogFragment {
    private Handler handler;

    private SeekBar seekBar;
    private Button buttonPlay;
    private TextView limit1;
    private TextView limit2;
    private TextView txtARTIST_NAME;
    private TextView txtALBUM_NAME;
    private TextView txtTRACK_NAME;
    private ImageView imgIMAGE;



    private Toolbar toolbar;
    private boolean fake;
    private MenuItem menuItem1;
    private MenuItem menuItem2;
    private ShareActionProvider mShareActionProvider1;
    private ShareActionProvider mShareActionProvider2;

    private static PlayerFragment me;

    private String uri;
    private int position;
    private boolean first;
    private boolean mTwoPane = false;

    private Cursor cursor;


    //The lines below is to access the service.
    private boolean mBound;
    protected PlayerService mService = null;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PlayerService.LocalBinder binder = (PlayerService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
            if (first) {
                first = false;
                mService.setmTwoPane(mTwoPane);
                mService.loadCursor(uri, position);
            }else {
                mService.setmTwoPane(mTwoPane);
                uri = mService.getUri();
                position = mService.getPosition();
                cursor = getActivity().getContentResolver().query(Uri.parse(uri), null, null, null, null);
                cursor.moveToPosition(position);
                face();
                refreshSeek();
            }
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

    public static PlayerFragment newInstance(String uri, int position, boolean first) {
        PlayerFragment pf = new PlayerFragment();
        if(position == -10){
            pf.fake = true; //In this case I have only to open and close the fragment
            //There is a bug that doesn show properly the share icon on the first time
            //I used a flag to open, close and re-open the fragment in this case.
        }else {
            pf.me = pf;
            pf.first = first;
            pf.position = position;
            pf.uri = uri;
                    }
        return pf;
    }

    public static PlayerFragment PF(){
        return me;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        Log.v("davi", "onCreateOptionsMenu:" + mTwoPane);
        if(mBound) {
            mService.setmTwoPane(mTwoPane);
        }
        if(mTwoPane){
            mShareActionProvider2 = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem2);
            mShareActionProvider2.setShareIntent(getShareIntent());
        }else{
            inflater.inflate(R.menu.menu_player_main, menu);
            menuItem1 = menu.findItem(R.id.action_share);
            mShareActionProvider1 = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem1);
            mShareActionProvider1.setShareIntent(getShareIntent());
        }



    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        Log.v("davi", "onCreateDialog");
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        Log.v("davi", "onCreateView");
        this.setHasOptionsMenu(true);
        getActivity().setTitle("Spotify Streamer");
        View rootView = inflater.inflate(R.layout.fragment_player, container, false);

        Intent intentShare = new Intent(Intent.ACTION_SEND);
        intentShare.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        intentShare.setType("text/plain");
        intentShare.putExtra(Intent.EXTRA_TEXT, "");
        toolbar = (Toolbar) rootView.findViewById(R.id.my_toolbar);
        if(mTwoPane) {
            toolbar.inflateMenu(R.menu.menu_player_main);
            toolbar.setTitle("Spotify Streamer");
            menuItem2 = toolbar.getMenu().findItem(R.id.action_share);
            mShareActionProvider2 = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem2);
            mShareActionProvider2.setShareIntent(intentShare);
        }
        else{
            toolbar.setVisibility(View.GONE);
        }

        seekBar = (SeekBar) rootView.findViewById(R.id.seekBar);
        buttonPlay = (Button) rootView.findViewById(R.id.PLAY);
        Button buttonPrev = (Button) rootView.findViewById(R.id.PREV);
        Button buttonNext = (Button) rootView.findViewById(R.id.NEXT);
        limit1 = (TextView) rootView.findViewById(R.id.limit1);
        limit2 = (TextView) rootView.findViewById(R.id.limit2);

        txtARTIST_NAME = (TextView) rootView.findViewById(R.id.ARTIST_NAME);
        txtALBUM_NAME = (TextView) rootView.findViewById(R.id.ALBUM_NAME);
        txtTRACK_NAME = (TextView) rootView.findViewById(R.id.TRACK_NAME);
        imgIMAGE = (ImageView) rootView.findViewById(R.id.IMAGE);

        buttonPlay.setBackgroundResource(android.R.drawable.ic_media_play);
        buttonNext.setBackgroundResource(android.R.drawable.ic_media_next);
        buttonPrev.setBackgroundResource(android.R.drawable.ic_media_previous);

        buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mService.play();
                refreshButtonPlay();
            }
        });
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mService.next();
                position = mService.getPosition();
                face();
                load();
            }
        });
        buttonPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mService.prev();
                position = mService.getPosition();
                face();
                load();
            }
        });
        handler = new Handler();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mBound && seekBar != null && mService.isPlaying()) {
                    seekBar.setMax(mService.getDuration());
                    seekBar.setProgress(mService.getCurrentPosition());
                    setLimit();
                }
                handler.postDelayed(this, 1000);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mService.seekTo(seekBar.getProgress());
                setLimit();
            }
        });
//        Log.v("davi", "onCreateView final");
        return rootView;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        Log.v("davi ", "onDestroy do Player Fragment");
        me = null;
        if(mBound){
            disconnect();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
//        Log.v("davi ", "onCreate do Player Fragment");
//        Log.v("davi ", getActivity().getLocalClassName());
        if(getActivity().getLocalClassName().equals("ArtistMain")){
            mTwoPane = true;
        }
        super.onCreate(savedInstanceState);
        if(!fake) {
            me = this;
            if (!mBound) {
                Intent intent = new Intent(getActivity(), PlayerService.class);
                intent.putExtra("twoPane",mTwoPane);
                getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
//        Log.v("davi ", "onResume do Player Fragment");
        if(getActivity().getLocalClassName().equals("ArtistMain")){
            mTwoPane = true;
        }
        if(!fake) {
            cursor = getActivity().getContentResolver().query(Uri.parse(uri), null, null, null, null);
            cursor.moveToPosition(position);
            if (first) {
                load();
            }
        }
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
//        Log.v("davi ", "onViewStateRestored do Player Fragment");
        super.onViewStateRestored(savedInstanceState);
        if(savedInstanceState != null){
            uri = savedInstanceState.getString("uri");
            position = savedInstanceState.getInt("position");
            first = savedInstanceState.getBoolean("first");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
//        Log.v("davi ", "onSaveInstanceState do Player Fragment");
        outState.putString("uri", uri);
        outState.putInt("position", position);
        outState.putBoolean("first", first);
        super.onSaveInstanceState(outState);
    }

    private void setLimit(){
        if(mBound){
            BigDecimal duration1 = new BigDecimal(mService.getCurrentPosition() / 1000);
            BigDecimal minutes1 = duration1.divide(new BigDecimal("60"), 1);
            BigDecimal seconds1 = duration1.remainder(new BigDecimal("60"));
            BigDecimal duration2 = new BigDecimal(mService.getDuration() / 1000);
            BigDecimal minutes2 = duration2.divide(new BigDecimal("60"), 1);
            BigDecimal seconds2 = duration2.remainder(new BigDecimal("60"));
            if(minutes1.intValue() < 1000) {
                limit1.setText(minutes1.toString() + ":" + String.format("%02d",seconds1.intValue()));
            }else{
                limit1.setText("0:00");
            }
            if(minutes2.intValue() < 1000) {
                limit2.setText(minutes2.toString() + ":" + String.format("%02d", seconds2.intValue()));
            }else{
                limit2.setText("0:00");
            }
        }
    }
    public void setOnPreparedListener() {
        if(mBound) {
            seekBar.setMax(mService.getDuration());
        }
        seekBar.setProgress(0);
        seekBar.setEnabled(true);
        buttonPlay.setBackgroundResource(android.R.drawable.ic_media_pause);
        setLimit();
    }

    public void setOnCompletionListener() {
        seekBar.setProgress(0);
        seekBar.setEnabled(true);
        buttonPlay.setBackgroundResource(android.R.drawable.ic_media_play);
        setLimit();
    }

    public void error() {
        seekBar.setEnabled(false);
        seekBar.setMax(1000000);
        seekBar.setProgress(0);
        limit2.setText("0:00");
        limit1.setText("0:00");
        buttonPlay.setBackgroundResource(android.R.drawable.ic_media_play);
        Toast.makeText(getActivity(), "Problema", Toast.LENGTH_SHORT);
    }

    private void load() {
        txtARTIST_NAME.setText(cursor.getString(cursor.getColumnIndex(StreamerContract.TrackEntry.COLUMN_ARTIST_NAME)));
        txtALBUM_NAME.setText(cursor.getString(cursor.getColumnIndex(StreamerContract.TrackEntry.COLUMN_ALBUM_NAME)));
        txtTRACK_NAME.setText(cursor.getString(cursor.getColumnIndex(StreamerContract.TrackEntry.COLUMN_TRACK_NAME)));
        if(!cursor.getString(cursor.getColumnIndex(StreamerContract.TrackEntry.COLUMN_IMAGE)).equals("")) {
            Picasso.with(getActivity()).load(cursor.getString(cursor.getColumnIndex(StreamerContract.TrackEntry.COLUMN_IMAGE))).into(imgIMAGE);
        }else{
            imgIMAGE.setImageDrawable(null);
        }
        seekBar.setMax(1000000);
        seekBar.setProgress(0);
        seekBar.setEnabled(false);
        buttonPlay.setBackgroundResource(android.R.drawable.ic_media_play);
        limit1.setText("0:00");
        limit2.setText("0:00");
        getShareIntent();
        if(mTwoPane){
            mShareActionProvider2 = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem2);
            mShareActionProvider2.setShareIntent(getShareIntent());
        }else{
            if(menuItem1!=null) {
                mShareActionProvider1 = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem1);
                mShareActionProvider1.setShareIntent(getShareIntent());
            }
        }
    }

    private void refreshSeek() {
        seekBar.setMax((mService.getDuration()));
        seekBar.setProgress(mService.getCurrentPosition());
            setLimit();
            if (mService.getDuration() - mService.getCurrentPosition() < 1000) {
                setOnCompletionListener();
            }
    }

    public void face() {
        position = mService.getPosition();
        cursor.moveToPosition(position);
        refreshButtonPlay();
        txtARTIST_NAME.setText(cursor.getString(cursor.getColumnIndex(StreamerContract.TrackEntry.COLUMN_ARTIST_NAME)));
        txtALBUM_NAME.setText(cursor.getString(cursor.getColumnIndex(StreamerContract.TrackEntry.COLUMN_ALBUM_NAME)));
        txtTRACK_NAME.setText(cursor.getString(cursor.getColumnIndex(StreamerContract.TrackEntry.COLUMN_TRACK_NAME)));
        if(!cursor.getString(cursor.getColumnIndex(StreamerContract.TrackEntry.COLUMN_IMAGE)).equals("")) {
            Picasso.with(getActivity()).load(cursor.getString(cursor.getColumnIndex(StreamerContract.TrackEntry.COLUMN_IMAGE))).into(imgIMAGE);
        }else{
            imgIMAGE.setImageDrawable(null);
        }
    }

    private void refreshButtonPlay(){
        if (mService.isPlaying()) {
            buttonPlay.setBackgroundResource(android.R.drawable.ic_media_pause);
        } else {
            buttonPlay.setBackgroundResource(android.R.drawable.ic_media_play);
        }
    }
    private Intent getShareIntent(){
        String text = "I'm listening to ";
        text = text + cursor.getString(cursor.getColumnIndex(StreamerContract.TrackEntry.COLUMN_TRACK_NAME));
        text = text + " by ";
        text = text + cursor.getString(cursor.getColumnIndex(StreamerContract.TrackEntry.COLUMN_ARTIST_NAME));
        text = text + " with my Spotify Streamer App.\n";
        text = text + cursor.getString(cursor.getColumnIndex(StreamerContract.TrackEntry.COLUMN_TRACK_URL));
        Intent intentShare = new Intent(Intent.ACTION_SEND);
        intentShare.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        intentShare.setType("text/plain");
        intentShare.putExtra(Intent.EXTRA_TEXT, text);
        return intentShare;
    }
}