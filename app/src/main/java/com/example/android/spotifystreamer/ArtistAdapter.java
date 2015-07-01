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

public class ArtistAdapter extends ArrayAdapter<Artist> {
    private ArrayList<Artist> artistArrayList;

    public ArtistAdapter(Context context, int textViewResourceId, ArrayList<Artist> artistArrayList) {
        super(context,textViewResourceId,artistArrayList);
        this.artistArrayList = artistArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if(v == null){
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.list_item_artist, null);
        }
        Artist i = artistArrayList.get(position);
        if(i != null){
            TextView name = (TextView) v.findViewById(R.id.list_item_artist_name);
            ImageView imagem = (ImageView) v.findViewById(R.id.list_item_artist_image);
            if(name != null){
                name.setText(i.getArtist_name());
            }
            if(imagem != null){
                Picasso.with(v.getContext()).load(i.getImage()).into(imagem);
            }
        }
        return v;
    }
}