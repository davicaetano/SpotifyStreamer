package com.example.android.spotifystreamer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class TrackAdapter extends ArrayAdapter<Track> {
    private ArrayList<Track> songArrayList;

    public TrackAdapter(Context context, int textViewResourceId, ArrayList<Track> songArrayList) {

        super(context,textViewResourceId, songArrayList);
        this.songArrayList = songArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if(v == null){
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.list_item_song, null);
        }
        Track i = songArrayList.get(position);
        if(i != null){
            TextView song_name = (TextView) v.findViewById(R.id.list_item_song_song_name);
            TextView artist_name = (TextView) v.findViewById(R.id.list_item_song_artist_name);
            ImageView imagem = (ImageView) v.findViewById(R.id.list_item_song_image);
            if(song_name != null){
                song_name.setText(i.getSong());
            }
            if(artist_name != null){
                artist_name.setText(i.getAlbum());
            }
            if(imagem != null){
                Picasso.with(v.getContext()).load(i.getImage()).into(imagem);
            }else{

            }
        }
        return v;
    }
}