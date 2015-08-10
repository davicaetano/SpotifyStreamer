package com.example.android.spotifystreamer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.example.android.spotifystreamer.data.StreamerContract;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import static android.app.Notification.VISIBILITY_PUBLIC;


public class PlayerService extends Service {
    private MediaPlayer mp = new MediaPlayer();
    private int position;
    private String uri;
    private NotificationManager notificationManager;
    private Cursor cursor;
    private boolean loaded = false;
    private boolean mTwoPane;

    private final IBinder mBinder = new LocalBinder();
    public class LocalBinder extends Binder {
        public PlayerService getService(){
            return PlayerService.this;
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = (NotificationManager) getSystemService(this.NOTIFICATION_SERVICE);
        mp.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);

        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.seekTo(0);
                if (PlayerFragment.PF() != null) {
                    PlayerFragment.PF().setOnCompletionListener();
                }
                notif();
            }
        });
        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                try {
                    mp.start();
                    loaded = true;
                    notif();
                    if (PlayerFragment.PF() != null) {
                        PlayerFragment.PF().setOnPreparedListener();
                    }
                } catch (IllegalStateException e) {
                    if (PlayerFragment.PF() != null) {
                        PlayerFragment.PF().error();
                    }
                }
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        Log.v("davi", "onStartCommand do Service");
        String action;
        if(intent != null) {
            if(intent.hasExtra("uri")){
                uri = intent.getStringExtra("uri");
                this.cursor = getContentResolver().query(Uri.parse(uri), null, null, null, null);
            }
            if(intent.hasExtra("position")){
                position = intent.getIntExtra("position", 0);
                this.cursor.moveToPosition(position);
            }
            if (intent.hasExtra("action")) {
                action = intent.getStringExtra("action");
                if (action.equals("play")) {
                    play();
                } else if (action.equals("prev")) {
                    prev();
                } else if (action.equals("next")) {
                    next();
                }
                if (PlayerFragment.PF() != null) {
                    PlayerFragment.PF().face();
                }
                if (intent.hasExtra("twoPane")){
                    mTwoPane = intent.getBooleanExtra("twoPane",true);
                }else{
                    mTwoPane = false;
                }

                notif();
            }

            if(intent.hasExtra("twoPane")){
                mTwoPane = intent.getBooleanExtra("twoPane",false);
            }else{
                mTwoPane = false;
            }
//            Log.v("davi: mTwoPane = ",mTwoPane+"");
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        stopNotif();
        mp.reset();
    }

    public void loadCursor(String uri, int position){
        this.position = position;
        this.uri = uri;
        this.cursor = getContentResolver().query(Uri.parse(uri),null,null,null,null);
        this.cursor.moveToPosition(position);
        loadPlayer();
    }

    private void loadPlayer() {
        mp.reset();
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mp.setDataSource(cursor.getString(cursor.getColumnIndex(StreamerContract.TrackEntry.COLUMN_TRACK_URL)));
            mp.prepareAsync();
        } catch (IOException e) {
            if (PlayerFragment.PF() != null) {
                PlayerFragment.PF().error();
            }
        }

    }

    public void play(){
        if(mp.isPlaying()){
            mp.pause();
        }else {
            mp.start();
        }
        notif();
    }
    public void next(){
        mp.stop();
        position++;
        position = position % cursor.getCount();
        cursor.moveToPosition(position);
        notif();
        loadPlayer();
    }

    public void prev(){
        mp.stop();
        position--;
        position = (position == -1 ? cursor.getCount() - 1 : position);
        cursor.moveToPosition(position);
        notif();
        loadPlayer();
    }

    public void seekTo(int progress) {
        mp.seekTo(progress);
    }

    public boolean isLoaded(){
        return loaded;
    }

    public int getCurrentPosition(){
        return mp.getCurrentPosition();
    }
    public int getDuration(){
        return mp.getDuration();
    }
    public boolean isPlaying(){
        return mp.isPlaying();
    }
    public void stop(){
        mp.stop();
    }
    public void pause(){
        mp.pause();
    }
    public void reset(){
        mp.reset();
    }
    public int getPosition(){
        return position;
    }
    public String getUri(){
        return uri;
    }
    public void exit(){
        loaded = false;
    }
    public void setmTwoPane(boolean mTwoPane){
        this.mTwoPane = mTwoPane;
    }

    public void notif() {
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("notification",true)) {
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                    .setVisibility(VISIBILITY_PUBLIC)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(cursor.getString(cursor.getColumnIndex(StreamerContract.TrackEntry.COLUMN_TRACK_NAME)))
                    .setContentText(cursor.getString(cursor.getColumnIndex(StreamerContract.TrackEntry.COLUMN_ARTIST_NAME)));
            Notification notification = mBuilder.build();
            RemoteViews remoteViews = new RemoteViews(this.getPackageName(),R.layout.notification);
            remoteViews.setTextViewText(R.id.list_item_song_song_name, cursor.getString(cursor.getColumnIndex(StreamerContract.TrackEntry.COLUMN_TRACK_NAME)));
            remoteViews.setTextViewText(R.id.list_item_song_artist_name, cursor.getString(cursor.getColumnIndex(StreamerContract.TrackEntry.COLUMN_ARTIST_NAME)));
//            Log.v("davi: mTwoPane = ", mTwoPane + "");

            if(mTwoPane){
                Intent intent = new Intent(this, ArtistMain.class);
                intent.putExtra("uri", uri);
                intent.putExtra("position", position);
                intent.putExtra("first", false);
                intent.putExtra("twoPane", mTwoPane);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                remoteViews.setOnClickPendingIntent(R.id.layout_notication, pendingIntent);
            }else {
                Intent intent = new Intent(this, PlayerMain.class);
                intent.putExtra("uri", uri);
                intent.putExtra("position", position);
                intent.putExtra("first", false);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                remoteViews.setOnClickPendingIntent(R.id.layout_notication, pendingIntent);
            }

            remoteViews.setInt(R.id.button1, "setBackgroundResource", android.R.drawable.ic_media_previous);
            Intent intent1 = new Intent(this,PlayerService.class);
            intent1.putExtra("action", "prev");
            intent1.putExtra("uri", uri);
            intent1.putExtra("position", position);
            intent1.putExtra("twoPane", mTwoPane);
            PendingIntent pendingIntent1 = PendingIntent.getService(this,1,intent1,PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.button1, pendingIntent1);

            if(mp.isPlaying()) {
                remoteViews.setInt(R.id.button2, "setBackgroundResource", android.R.drawable.ic_media_pause);
            }else{
                remoteViews.setInt(R.id.button2, "setBackgroundResource", android.R.drawable.ic_media_play);
            }
            Intent intent2 = new Intent(this,PlayerService.class);
            intent2.putExtra("action", "play");
            intent2.putExtra("uri", uri);
            intent2.putExtra("position", position);
            intent2.putExtra("twoPane", mTwoPane);
            PendingIntent pendingIntent2 = PendingIntent.getService(this,2,intent2,PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.button2, pendingIntent2);

            remoteViews.setInt(R.id.button3, "setBackgroundResource", android.R.drawable.ic_media_next);
            Intent intent3 = new Intent(this,PlayerService.class);
            intent3.putExtra("action", "next");
            intent3.putExtra("uri", uri);
            intent3.putExtra("position", position);
            intent3.putExtra("twoPane", mTwoPane);
            PendingIntent pendingIntent3 = PendingIntent.getService(this,3,intent3,PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.button3, pendingIntent3);
            notification.contentView = remoteViews;
//            notificationManager.notify(0, notification);
            startForeground(0,notification);
            final String getOnlinePic = cursor.getString(cursor.getColumnIndex(StreamerContract.TrackEntry.COLUMN_IMAGE));
            if(!cursor.getString(cursor.getColumnIndex(StreamerContract.TrackEntry.COLUMN_IMAGE)).equals("")) {
                Picasso.with(this).load(getOnlinePic).into(remoteViews, R.id.list_item_song_image, 0, notification);
            }
        }else{
            stopNotif();
        }
    }

    public void stopNotif(){
        notificationManager.cancel(0);
    }


}