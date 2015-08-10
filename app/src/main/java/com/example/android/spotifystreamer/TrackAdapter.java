package com.example.android.spotifystreamer;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.spotifystreamer.data.StreamerContract;
import com.squareup.picasso.Picasso;

public class TrackAdapter extends CursorAdapter {
    public TrackAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item_song,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        View v = view;
        TextView song_name = (TextView) v.findViewById(R.id.list_item_song_song_name);
        TextView artist_name = (TextView) v.findViewById(R.id.list_item_song_artist_name);
        ImageView imagem = (ImageView) v.findViewById(R.id.list_item_song_image);
        if(song_name != null){
            song_name.setText(cursor.getString(cursor.getColumnIndex(StreamerContract.TrackEntry.COLUMN_TRACK_NAME)));
        }
        if(artist_name != null){
            artist_name.setText(cursor.getString(cursor.getColumnIndex(StreamerContract.TrackEntry.COLUMN_ARTIST_NAME)));
        }
        if(imagem != null){
            if(!cursor.getString(cursor.getColumnIndex(StreamerContract.TrackEntry.COLUMN_IMAGE)).equals("")){
                Picasso.with(v.getContext()).load(cursor.getString(cursor.getColumnIndex(StreamerContract.TrackEntry.COLUMN_IMAGE))).into(imagem);
            }else{
                imagem.setImageDrawable(null);
            }
        }
    }
}