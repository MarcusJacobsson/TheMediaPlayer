package dv106.lnu.themediaplayer.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.RemoteViews;

import java.io.File;
import java.util.ArrayList;

import dv106.lnu.themediaplayer.MainActivity;
import dv106.lnu.themediaplayer.R;
import dv106.lnu.themediaplayer.database.DataSource;
import dv106.lnu.themediaplayer.pojo.Song;
import dv106.lnu.themediaplayer.preferences.PreferencesActivity;
import dv106.lnu.themediaplayer.util.DBCleaner;
import dv106.lnu.themediaplayer.widgetprovider.SongAppWidgetProvider;

public class SongService extends Service implements OnCompletionListener, AudioManager.OnAudioFocusChangeListener {

    private final IBinder binder = new SongServiceBinder();
    private MediaPlayer mPlayer;
    private String songPath;
    private DataSource datasource;
    private String playlist;
    private int resumedAfterAudioFocus = 0;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        /* commands */
        Bundle commands = intent.getExtras();
        if (commands != null) {
            SharedPreferences settings = getSharedPreferences(
                    "play_mode_prefs", 0);
            if (settings.getBoolean("from_playlist", false)) {
                this.playlist = settings.getString("playlist_name", "");
            } else {
                this.playlist = null;
            }

            switch (commands.getString("action")) {
                case "playOrPause":
                    this.pauseOrResumeMusic();
                    break;

                case "pause":
                    this.pauseMusic();
                    break;

                case "next":
                    this.playNextSong();
                    break;

                case "previous":
                    this.playPreviousSong();
                    break;

                case "remove":
                    this.pauseMusic();
                    stopForeground(true);
                    break;
            }
        }
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class SongServiceBinder extends Binder {
        public SongService getService() {
            return SongService.this;
        }
    }

    @Override
    public void onCreate() {
        datasource = new DataSource(this);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        if (mPlayer != null) {
            mPlayer.reset();
            mPlayer.release();
        }
    }

    private void setUpNotification() {
        SharedPreferences sharedPref = PreferenceManager
                .getDefaultSharedPreferences(this);
        boolean notificationEnabled = sharedPref.getBoolean(
                PreferencesActivity.KEY_PREF_NOTIFICATION, true);
        if (mPlayer != null && notificationEnabled) {
            /*
             * Set up the notification for the started service
			 */
            Song currentSong = null;
            String title = null;
            String artist = null;
            Notification.Builder notifyBuilder = new Notification.Builder(this);

            try {
                datasource.open();
                currentSong = datasource.getSong(songPath);
                datasource.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (currentSong != null) {
                title = currentSong.getTITLE();
                artist = currentSong.getARTIST();
            }
            if (title != null) {
                notifyBuilder.setTicker(title);
                notifyBuilder.setContentTitle(title);
                notifyBuilder.setContentText(artist);
            }

            notifyBuilder.setOngoing(true);
            notifyBuilder.setSmallIcon(R.drawable.ic_launcher);

            Intent notifyIntent = new Intent(this, MainActivity.class);
            notifyIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pi = PendingIntent.getActivity(this, 0, notifyIntent, 1);
            notifyBuilder.setContentIntent(pi);

            Intent actionIntent = new Intent(this, SongService.class);
            actionIntent.putExtra("action", "remove");
            PendingIntent actionPendingIntent = PendingIntent.getService(this,
                    0, actionIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            notifyBuilder.addAction(android.R.drawable.ic_delete, "Remove",
                    actionPendingIntent);

            Notification notification = notifyBuilder.build();
            startForeground(1, notification);
        }

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean repeatOn = pref.getBoolean("isRepeat", false);
        if (repeatOn) {
            this.playMusic(songPath, playlist);
        } else {
            playNextSong();
        }
    }

    private void playPreviousSong() {
        ArrayList<Song> allSongs = new ArrayList<Song>();
        ArrayList<String> allSongPaths = new ArrayList<String>();
        try {
            datasource.open();
            if (playlist != null) {
                allSongs = datasource.getSongsFromPlaylist(playlist);
            } else {
                allSongs = datasource.getAllSongs();
            }
            for (Song song : allSongs) {
                allSongPaths.add(song.getDATA());
            }
            int indexOfSong = allSongPaths.indexOf(this.getCurrentSongPath());
            if (indexOfSong != 0)
                this.playMusic(allSongPaths.get(indexOfSong - 1), null);
            datasource.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void playNextSong() {
        ArrayList<Song> allSongs = new ArrayList<Song>();
        ArrayList<String> allSongPaths = new ArrayList<String>();
        try {
            datasource.open();

            if (playlist != null) {
                allSongs = datasource.getSongsFromPlaylist(playlist);
            } else {
                allSongs = datasource.getAllSongs();
            }
            for (Song song : allSongs) {
                allSongPaths.add(song.getDATA());
            }
            int indexOfSong = allSongPaths.indexOf(this.getCurrentSongPath());
            if (indexOfSong != allSongPaths.size() - 1) {
                this.playMusic(allSongPaths.get(indexOfSong + 1), null);
            }
            datasource.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        /*
		 * Send a local broadcast to the MainActivity to update the
		 * GlobalControl
		 */
        Intent intent = new Intent(
                "dv106.lnu.themediaplayer.UPDATE_GLOBAL_CONTROL");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void setUpWidget() {
        Song tmpSong = null;
        try {
            datasource.open();
            tmpSong = datasource.getSong(this.songPath);
            datasource.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (tmpSong != null) {

            RemoteViews widgetView = new RemoteViews(getPackageName(),
                    R.layout.appwidget_homescreen);
            AppWidgetManager appWidgetManager = AppWidgetManager
                    .getInstance(this);
            widgetView.setTextViewText(R.id.tvAppWidgetArtist, tmpSong.getARTIST());
            widgetView.setTextViewText(R.id.tvAppWidgetTitle, tmpSong.getTITLE());
            appWidgetManager.updateAppWidget(new ComponentName(this,
                    SongAppWidgetProvider.class), widgetView);
        }
    }

    private void setWidgetPlayingButton(boolean isPlaying) {

        RemoteViews widgetView = new RemoteViews(getPackageName(),
                R.layout.appwidget_homescreen);
        AppWidgetManager appWidgetManager = AppWidgetManager
                .getInstance(this);
        if (isPlaying) {
            widgetView.setImageViewResource(R.id.ibAppWidgetPlayPause,
                    R.drawable.ic_action_pause_over_video);
        } else {
            widgetView.setImageViewResource(R.id.ibAppWidgetPlayPause,
                    R.drawable.ic_action_play_over_video);
        }
        appWidgetManager.updateAppWidget(new ComponentName(this,
                SongAppWidgetProvider.class), widgetView);
    }

    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                // resume playback
                if (mPlayer == null) {

                    this.playMusic(songPath, playlist);
                }
                if (mPlayer != null)
                    mPlayer.setVolume(1.0f, 1.0f);
                break;

            case AudioManager.AUDIOFOCUS_LOSS:
                // Lost focus for an unbounded amount of time: stop playback and
                // release media player
                if (mPlayer != null) {
                    resumedAfterAudioFocus = this.getCurrentPosition();
                    if (mPlayer.isPlaying())
                        mPlayer.stop();
                    mPlayer.reset();
                    mPlayer.release();
                    mPlayer = null;
                    setWidgetPlayingButton(false);

                }
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                // Lost focus for a short time, but we have to stop
                // playback. We don't release the media player because playback
                // is likely to resume
                if (mPlayer.isPlaying())
                    mPlayer.pause();
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                // Lost focus for a short time, but it's ok to keep playing
                // at an attenuated level
                if (mPlayer.isPlaying())
                    mPlayer.setVolume(0.1f, 0.1f);
                break;
        }
    }

	/*
	 * Client methods
	 */

    /**
     * Play the song at the provided songPath. If music is already playing, stop
     * the current music and start to play again
     */
    public void playMusic(String songPath, String playlist) {

        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int result = am.requestAudioFocus(this,
                // Use the music stream.
                AudioManager.STREAM_MUSIC,
                // Request permanent focus.
                AudioManager.AUDIOFOCUS_GAIN);

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            File tmpFile = new File(songPath);
            if (tmpFile.exists()) {
                if (mPlayer == null) {
                    try {
                        mPlayer = MediaPlayer.create(getBaseContext(),
                                Uri.parse(songPath));
                        mPlayer.start();

                    } catch (IllegalStateException e) {
                        e.printStackTrace();

                    }
                } else {
                    mPlayer.stop();
                    mPlayer.reset();
                    mPlayer.release();
                    mPlayer = MediaPlayer.create(getBaseContext(),
                            Uri.parse(songPath));
                    mPlayer.start();
                }
                this.songPath = songPath;
                this.playlist = playlist;
                if (mPlayer != null) {
                    mPlayer.setOnCompletionListener(this);
                    setUpWidget();
                    setWidgetPlayingButton(true);
                    setUpNotification();
                }
            } else {
                DBCleaner cleaner = new DBCleaner();
                cleaner.cleanIfNeeded(getApplication());
            }
        }
    }

    /**
     * Pause the player if it's playing, resume the player if it's been paused.
     * Returns 0 if it's paused, 1 if it's playing and 2 if something went wrong
     */
    public int pauseOrResumeMusic() {
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int result = am.requestAudioFocus(this,
                // Use the music stream.
                AudioManager.STREAM_MUSIC,
                // Request permanent focus.
                AudioManager.AUDIOFOCUS_GAIN);

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            setUpNotification();
            if (mPlayer != null && mPlayer.isPlaying()) {
                mPlayer.pause();
                setWidgetPlayingButton(false);
                return 0;
            } else if (mPlayer != null) {
                mPlayer.start();
                setWidgetPlayingButton(true);
                return 1;
            }else if(songPath != null && playlist != null) {
                this.playMusic(songPath, playlist);
                this.seekToPosition(resumedAfterAudioFocus);
            }
            return 2;
        }
        return 2;
    }

    /**
     * Returns the absolute path of the song that's currently playing
     */
    public String getCurrentSongPath() {
        return this.songPath;
    }

    /**
     * Returns the current position of the current track playing
     */
    public int getCurrentPosition() {
        if (mPlayer != null) {
            return mPlayer.getCurrentPosition();
        } else {
            return -1;
        }
    }

    /**
     * Returns the total duration of the current track playing
     */
    public int getTotalDuration() {
        return mPlayer.getDuration();
    }

    /**
     * Seek to the desired position
     */
    public void seekToPosition(int position) {
        mPlayer.seekTo(position);
		/* If the player finishes before the seek is done */
        if (!mPlayer.isPlaying()) {
            mPlayer.start();
        }
    }

    /**
     * Return the current AudioSessionId
     */
    public int getAudioSessionId() {
        return mPlayer.getAudioSessionId();
    }

    /**
     * Returns whether or not the service is playing music
     */
    public boolean isPlaying() {
        if (mPlayer != null) {
            return mPlayer.isPlaying();
        } else {
            return false;
        }
    }

    /**
     * Pause the music
     */
    public void pauseMusic() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.pause();
        }
    }

    /**
     * Return the remaining time of the song
     */
    public int getRemainingTime() {
        return mPlayer.getDuration() - mPlayer.getCurrentPosition();
    }

    /**
     * Return the current playlist
     */
    public String getCurrentPlaylist() {
        return this.playlist;
    }

    public void setCurrentPlaylist(String playlist) {
        this.playlist = playlist;
    }

}
