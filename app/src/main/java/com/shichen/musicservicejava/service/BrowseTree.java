package com.shichen.musicservicejava.service;

import android.content.Context;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;

import com.shichen.musicservicejava.R;
import com.shichen.musicservicejava.model.MusicSource;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * @author shichen 754314442@qq.com
 * Created by shichen on 2020/4/9.
 */
public class BrowseTree {
    private final String MUSIC_BROWSABLE_ROOT = "/";
    private final String MUSIC_EMPTY_ROOT = "@empty@";
    private final String MUSIC_RECOMMENDED_ROOT = "__RECOMMENDED__";
    private final String MUSIC_ALBUMS_ROOT = "__ALBUMS__";

    public static final String METADATA_KEY_MUSIC_FLAGS = "com.shichen.musicservicejava.media.METADATA_KEY_UAMP_FLAGS";

    private final String MEDIA_SEARCH_SUPPORTED = "android.media.browse.SEARCH_SUPPORTED";

    private final String RESOURCE_ROOT_URI = "android.resource://com.shichen.musicservicejava.next/drawable/";
    private Context context;
    private MusicSource musicSource;

    private HashMap<String, List<MediaMetadataCompat>> mediaIdToChildren=new HashMap<>();

    private boolean searchableByUnknownCaller = true;

    public BrowseTree(Context context, MusicSource musicSource) {
        this.context = context;
        this.musicSource = musicSource;

        List<MediaMetadataCompat> rootList=mediaIdToChildren.get(MUSIC_BROWSABLE_ROOT);
        if (rootList==null){
            rootList=new ArrayList<>();
        }
        MediaMetadataCompat recommendedMetadata=new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID,MUSIC_RECOMMENDED_ROOT)
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, context.getString(R.string.recommended_title))
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI,RESOURCE_ROOT_URI +
                        context.getResources().getResourceEntryName(R.drawable.ic_recommended))
                .putLong(METADATA_KEY_MUSIC_FLAGS, MediaBrowserCompat.MediaItem.FLAG_BROWSABLE).build();

        MediaMetadataCompat albumsMetadata=new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID,MUSIC_ALBUMS_ROOT)
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, context.getString(R.string.albums_title))
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI,RESOURCE_ROOT_URI +
                        context.getResources().getResourceEntryName(R.drawable.ic_album))
                .putLong(METADATA_KEY_MUSIC_FLAGS, MediaBrowserCompat.MediaItem.FLAG_BROWSABLE).build();
        rootList.add(recommendedMetadata);
        rootList.add(albumsMetadata);
        mediaIdToChildren.put(MUSIC_BROWSABLE_ROOT,rootList);

        Iterator<MediaMetadataCompat> iterator=musicSource.getAll().iterator();
        while (iterator.hasNext()){
            MediaMetadataCompat item=iterator.next();
            String albumMediaId;
            try {
                albumMediaId= URLEncoder.encode(item.getString(MediaMetadataCompat.METADATA_KEY_ALBUM),"UTF-8");
            }catch (UnsupportedEncodingException e){
                albumMediaId= URLEncoder.encode(item.getString(MediaMetadataCompat.METADATA_KEY_ALBUM));
            }

        }
    }


}
