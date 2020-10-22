package com.shichen.musicservicejava.model;

import android.os.Bundle;
import android.support.v4.media.MediaMetadataCompat;

import java.util.List;

/**
 * @author shichen 754314442@qq.com
 * Created by shichen on 2020/4/7.
 */
public interface MusicSource{

    interface LoadCallback{
        void success();
        void fail();
    }

    boolean whenReady();

    void load(LoadCallback callback);

    List<MediaMetadataCompat> search(String query, Bundle extras);

    List<MediaMetadataCompat> getAll();
}
