package com.shichen.musicservicejava.service;

import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.session.MediaSessionCompat;

import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator;

/**
 * @author shichen 754314442@qq.com
 * Created by shichen on 2020/4/9.
 */
public class MusicQueueNavigator extends TimelineQueueNavigator {
    private  Timeline.Window window =new Timeline.Window();

    public MusicQueueNavigator(MediaSessionCompat mediaSession) {
        super(mediaSession);
    }

    @Override
    public MediaDescriptionCompat getMediaDescription(Player player, int windowIndex) {
        return (MediaDescriptionCompat) player.getCurrentTimeline()
                .getWindow(windowIndex, window).tag;
    }
}
