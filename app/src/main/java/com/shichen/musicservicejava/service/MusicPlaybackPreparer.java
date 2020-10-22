package com.shichen.musicservicejava.service;

import android.net.Uri;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.annotation.Nullable;

import com.google.android.exoplayer2.ControlDispatcher;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.shichen.musicservicejava.model.MusicSource;

/**
 * @author shichen 754314442@qq.com
 * Created by shichen on 2020/4/5.
 */
public class MusicPlaybackPreparer implements MediaSessionConnector.PlaybackPreparer {
    private ExoPlayer exoPlayer;
    private MusicSource musicSource;
    private DataSource.Factory factory;

    public MusicPlaybackPreparer(ExoPlayer exoPlayer, MusicSource musicSource, DataSource.Factory factory) {
        this.exoPlayer = exoPlayer;
        this.musicSource = musicSource;
        this.factory = factory;
    }

    @Override
    public long getSupportedPrepareActions() {
        return (PlaybackStateCompat.ACTION_PREPARE_FROM_MEDIA_ID |
        PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID |
        PlaybackStateCompat.ACTION_PREPARE_FROM_SEARCH |
        PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH);
    }

    @Override
    public void onPrepare(boolean playWhenReady) {

    }

    @Override
    public void onPrepareFromMediaId(String mediaId, boolean playWhenReady, Bundle extras) {

    }

    @Override
    public void onPrepareFromSearch(String query, boolean playWhenReady, Bundle extras) {

    }

    @Override
    public void onPrepareFromUri(Uri uri, boolean playWhenReady, Bundle extras) {

    }

    @Override
    public boolean onCommand(Player player, ControlDispatcher controlDispatcher, String command, @Nullable Bundle extras, @Nullable ResultReceiver cb) {
        return false;
    }
}
