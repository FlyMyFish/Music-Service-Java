package com.shichen.musicservicejava.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Build;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.media.session.MediaButtonReceiver;

import com.shichen.musicservicejava.R;

import static android.media.session.PlaybackState.ACTION_SKIP_TO_PREVIOUS;
import static android.support.v4.media.session.PlaybackStateCompat.ACTION_PAUSE;
import static android.support.v4.media.session.PlaybackStateCompat.ACTION_PLAY;
import static android.support.v4.media.session.PlaybackStateCompat.ACTION_SKIP_TO_NEXT;
import static android.support.v4.media.session.PlaybackStateCompat.ACTION_STOP;

/**
 * @author shichen 754314442@qq.com
 * Created by shichen on 2019/7/1.
 */
public class NotificationBuilder {
    private Context context;

    public static final String NOW_PLAYING_CHANNEL = "com.shichen.music.media.NOW_PLAYING";
    public static final int NOW_PLAYING_NOTIFICATION = 0xb339;
    private android.app.NotificationManager platformNotificationManager = (android.app.NotificationManager)
            context.getSystemService(Context.NOTIFICATION_SERVICE);

    private NotificationCompat.Action skipToPreviousAction = new NotificationCompat.Action(
            R.drawable.exo_controls_previous,
            context.getString(R.string.notification_skip_to_previous),
            MediaButtonReceiver.buildMediaButtonPendingIntent(context, ACTION_SKIP_TO_PREVIOUS));
    private NotificationCompat.Action playAction = new NotificationCompat.Action(
            R.drawable.exo_controls_play,
            context.getString(R.string.notification_play),
            MediaButtonReceiver.buildMediaButtonPendingIntent(context, ACTION_PLAY));
    private NotificationCompat.Action pauseAction = new NotificationCompat.Action(
            R.drawable.exo_controls_pause,
            context.getString(R.string.notification_pause),
            MediaButtonReceiver.buildMediaButtonPendingIntent(context, ACTION_PAUSE));
    private NotificationCompat.Action skipToNextAction = new NotificationCompat.Action(
            R.drawable.exo_controls_next,
            context.getString(R.string.notification_skip_to_next),
            MediaButtonReceiver.buildMediaButtonPendingIntent(context, ACTION_SKIP_TO_NEXT));

    private PendingIntent stopPendingIntent = MediaButtonReceiver.buildMediaButtonPendingIntent(context, ACTION_STOP);

    public NotificationBuilder(Context context) {
        this.context = context;
    }

    public Notification buildNotification(MediaSessionCompat.Token sessionToke) throws Exception {
        if (shouldCreateNowPlayingChannel()) {
            createNowPlayingChannel();
        }
        MediaControllerCompat controller = new MediaControllerCompat(context, sessionToke);
        MediaDescriptionCompat description = controller.getMetadata().getDescription();
        PlaybackStateCompat playbackState = controller.getPlaybackState();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOW_PLAYING_CHANNEL);
        int playPauseIndex = 0;
        //actions and PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS != 0L
        int state = playbackState.getState();

        if ((playbackState.getActions() & PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS) != 0L) {
            builder.addAction(skipToPreviousAction);
            ++playPauseIndex;
        } else if ((state == PlaybackStateCompat.STATE_BUFFERING) ||
                (state == PlaybackStateCompat.STATE_PLAYING)) {
            builder.addAction(pauseAction);
        } else if (((playbackState.getActions() & PlaybackStateCompat.ACTION_PLAY) != 0L) ||
                (((playbackState.getActions() & PlaybackStateCompat.ACTION_PLAY_PAUSE) != 0L) &&
                        (state == PlaybackStateCompat.STATE_PAUSED))) {
            builder.addAction(playAction);
        }
        if ((playbackState.getActions() & PlaybackStateCompat.ACTION_SKIP_TO_NEXT) != 0L) {
            builder.addAction(skipToNextAction);
        }
        androidx.media.app.NotificationCompat.MediaStyle mediaStyle = new androidx.media.app.NotificationCompat.MediaStyle()
                .setCancelButtonIntent(stopPendingIntent)
                .setMediaSession(sessionToke)
                .setShowActionsInCompactView(playPauseIndex)
                .setShowCancelButton(true);
        return builder.setContentIntent(controller.getSessionActivity())
                .setContentText(description.getSubtitle())
                .setContentTitle(description.getTitle())
                .setDeleteIntent(stopPendingIntent)
                .setLargeIcon(description.getIconBitmap())
                .setOnlyAlertOnce(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setStyle(mediaStyle)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .build();
    }

    private boolean shouldCreateNowPlayingChannel() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !nowPlayingChannelExists();
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private boolean nowPlayingChannelExists() {
        return platformNotificationManager.getNotificationChannel(NOW_PLAYING_CHANNEL) != null;
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void createNowPlayingChannel() {
        NotificationChannel notificationChannel = new NotificationChannel(NOW_PLAYING_CHANNEL,
                context.getString(R.string.notification_channel),
                NotificationManager.IMPORTANCE_LOW);
        notificationChannel.setDescription(context.getString(R.string.notification_channel_description));
        platformNotificationManager.createNotificationChannel(notificationChannel);
    }
}
