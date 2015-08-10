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

public class ArtistAdapter extends CursorAdapter {

    public ArtistAdapter(Context context, Cursor c, int flags)
    {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item_artist,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView name = (TextView) view.findViewById(R.id.list_item_artist_name);
        ImageView imagem = (ImageView) view.findViewById(R.id.list_item_artist_image);
        if(name != null){
            name.setText(cursor.getString(cursor.getColumnIndex(StreamerContract.ArtistEntry.COLUMN_ARTIST_NAME)));
        }
        if(imagem != null){
            if (!cursor.getString(cursor.getColumnIndex(StreamerContract.ArtistEntry.COLUMN_IMAGE)).equals("")) {
                Picasso.with(context).load(cursor.getString(cursor.getColumnIndex(StreamerContract.ArtistEntry.COLUMN_IMAGE))).into(imagem);
            }else{
                imagem.setImageDrawable(null);
            }
        }
    }
}