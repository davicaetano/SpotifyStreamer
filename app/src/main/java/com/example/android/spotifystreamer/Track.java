package com.example.android.spotifystreamer;

import android.net.Uri;

/**
 * Created by davi on 6/23/15.
 */
public class Track {
    private String id = "";
    private String album = "";
    private String song = "";
    private Uri image = null;

    public void setId(String id){
        this.id = id;
    }
    public void setAlbum(String album){
        this.album = album;
    }
    public void setSong(String song){
        this.song = song;
    }
    public void setImage(Uri image){
        this.image = image;
    }
    public String getId(){
        return id;
    }
    public String getAlbum(){
        return album;
    }
    public String getSong(){
        return song;
    }
    public Uri getImage(){
        return image;
    }
}
