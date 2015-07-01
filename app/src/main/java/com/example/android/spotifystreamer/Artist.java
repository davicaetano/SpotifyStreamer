package com.example.android.spotifystreamer;

import android.net.Uri;

/**
 * Created by davi on 6/23/15.
 */
public class Artist {
    private String artist_name = "";
    private Uri image = null;
    private String id ="";

    public void setArtist_name(String artist_name){
        this.artist_name = artist_name;
    }

    public String getArtist_name(){
        return artist_name;
    }

    public void setImage(Uri image){
        this.image = image;
    }

    public Uri getImage(){
        return image;
    }

    public String getId(){
        return id;
    }

    public void setId(String id){
        this.id = id;
    }
}
