package com.example.android.spotifystreamer.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class StreamerContract {
    public static final String CONTENT_AUTHORITY = "com.example.android.spotifystreamer";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_TRACK = "track";
    public static final String PATH_ARTIST = "artist";

    public static final class ArtistEntry implements BaseColumns{
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_ARTIST).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ARTIST;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.ANY_CURSOR_ITEM_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ARTIST;

        public static final String TABLE_NAME = "artist";

        public static final String COLUMN_ARTIST_NAME = "artist_name";
        public static final String COLUMN_ARTIST_ID = "artist_id";
        public static final String COLUMN_IMAGE = "image";

        public static Uri buildArtistUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
        public static Uri buildArtistById(String artist){
            return CONTENT_URI.buildUpon().appendPath(artist).build();
        }
    }

    public static final class TrackEntry implements BaseColumns{
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRACK).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRACK;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.ANY_CURSOR_ITEM_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRACK;
        public static final String TABLE_NAME = "track";

        public static final String COLUMN_ARTIST_ID = "artist_id";
        public static final String COLUMN_ARTIST_NAME = "artist_name";
        public static final String COLUMN_TRACK_ID = "track_id";
        public static final String COLUMN_TRACK_NAME = "track_name";
        public static final String COLUMN_ALBUM_NAME = "album_name";
        public static final String COLUMN_IMAGE = "image";
        public static final String COLUMN_TRACK_URL = "track_url";
        public static final String COLUMN_DURATION = "duration";
        public static final String COLUMN_COUNTRY = "country";


        public static Uri buildTrackUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
        public static Uri buildTrackByArtist(String artist){
            return CONTENT_URI.buildUpon().appendPath(artist).build();
        }
        public static String getArtistIdFromUri(Uri uri){
            return uri.getPathSegments().get(1);
        }
    }

}
