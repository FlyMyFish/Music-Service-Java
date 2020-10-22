package com.shichen.musicservicejava.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationManagerCompat;
import androidx.media.MediaBrowserServiceCompat;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.shichen.musicservicejava.model.MusicSource;
import com.shichen.musicservicejava.model.ScanSource;

import java.util.List;

/**
 * @author shichen 754314442@qq.com
 * Created by shichen on 2020/4/4.
 */
public class MediaPlayerService extends MediaBrowserServiceCompat {
    private BecomingNoisyReceiver becomingNoisyReceiver;
    private NotificationManagerCompat notificationManager;
    private NotificationBuilder notificationBuilder;
    private MediaSessionCompat mediaSession;
    private MusicSource musicSource;
    private MediaControllerCompat mediaController;
    private MediaSessionConnector mediaSessionConnector;
    private ExoPlayer exoPlayer;
    private AudioAttributes uAmpAudioAttributes;

    @Override
    public void onCreate() {
        super.onCreate();
        Intent sessionIntent = getPackageManager().getLaunchIntentForPackage(getPackageName());
        mediaSession = new MediaSessionCompat(this, "com.shichen.musicservicejava");
        mediaSession.setSessionActivity(PendingIntent.getActivity(this, 0, sessionIntent, 0));
        mediaSession.setActive(true);

        setSessionToken(mediaSession.getSessionToken());

        mediaController = new MediaControllerCompat(this, mediaSession);
        mediaController.registerCallback(new MediaControllerCallback());

        becomingNoisyReceiver =
                new BecomingNoisyReceiver(this, mediaSession.getSessionToken());
        musicSource=new ScanSource(this);
        // ExoPlayer will manage the MediaSession for us.
        mediaSessionConnector = new MediaSessionConnector(mediaSession);

        // Produces DataSource instances through which media data is loaded.
        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, MUSIC_USER_AGENT), null);
        SimpleExoPlayer simpleExoPlayer=new SimpleExoPlayer.Builder(this).build();
        uAmpAudioAttributes=new AudioAttributes.Builder()
                .setContentType(C.CONTENT_TYPE_MUSIC)
                .setUsage(C.USAGE_MEDIA)
                .build();
        simpleExoPlayer.setAudioAttributes(uAmpAudioAttributes,true);
        exoPlayer=simpleExoPlayer;
        MusicPlaybackPreparer musicPlaybackPreparer=new MusicPlaybackPreparer(exoPlayer,musicSource,dataSourceFactory);
        mediaSessionConnector.setPlayer(exoPlayer);
        mediaSessionConnector.setPlaybackPreparer(musicPlaybackPreparer);
        mediaSessionConnector.setQueueNavigator(new MusicQueueNavigator(mediaSession));
    }

    private class MediaControllerCallback extends MediaControllerCompat.Callback {
        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            if (mediaController.getPlaybackState() != null) {
                updateNotification(mediaController.getPlaybackState());
            }
        }

        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            if (state != null) {
                updateNotification(state);
            }
        }

        private void updateNotification(PlaybackStateCompat state) {
            int updatedState = state.getState();
            Notification notification = null;
            if (mediaController.getMetadata() != null
                    && updatedState != PlaybackStateCompat.STATE_NONE) {
                try {
                    notification = notificationBuilder.buildNotification(mediaSession.getSessionToken());
                } catch (Exception e) {
                    notification = null;
                }
            }
            if (updatedState == PlaybackStateCompat.STATE_BUFFERING || updatedState == PlaybackStateCompat.STATE_PLAYING) {

            } else {

            }

        }
    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        //控制客户端媒体浏览器的连接请求，通过返回值决定是否允许该客户端连接服务

        return null;
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        //媒体浏览器向Service发送数据订阅时调用，一般在这执行异步获取数据的操作，最后将数据发送至媒体浏览器的回调接口中
    }

    private class BecomingNoisyReceiver extends BroadcastReceiver {
        private Context context;
        private MediaSessionCompat.Token sessionToken;

        public BecomingNoisyReceiver(Context context, MediaSessionCompat.Token sessionToken) {
            this.context = context;
            this.sessionToken = sessionToken;
            try {
                controller = new MediaControllerCompat(context, sessionToken);
            } catch (RemoteException e) {
                controller = null;
            }
        }

        private IntentFilter noisyIntentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        private MediaControllerCompat controller;

        private boolean registered = false;

        private void register() {
            if (!registered) {
                context.registerReceiver(this, noisyIntentFilter);
                registered = true;
            }
        }

        private void unregister() {
            if (registered) {
                context.unregisterReceiver(this);
                registered = false;
            }
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                if (intent.getAction() != null) {
                    if (intent.getAction().equals(AudioManager.ACTION_AUDIO_BECOMING_NOISY) && controller != null) {
                        controller.getTransportControls().pause();
                    }
                }
            }
        }
    }

    private static final String MUSIC_USER_AGENT = "music.next";
}
