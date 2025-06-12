package com.ahmadrezagh671.nasamediaexplorer.Services;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.media3.common.AudioAttributes;
import androidx.media3.common.C;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.session.MediaSession;
import androidx.media3.session.MediaSessionService;

import com.ahmadrezagh671.nasamediaexplorer.ItemViewerActivity;

public class PlaybackService extends MediaSessionService {

  private MediaSession mediaSession = null;
  private ExoPlayer player;

    @OptIn(markerClass = UnstableApi.class)
  @Override
  public void onCreate() {
    super.onCreate();

    SharedPreferences prefs = getSharedPreferences("ahmadrezagh671_nasamediaexplorer", MODE_PRIVATE);
    String dataJson = prefs.getString("lastAudioData", "");

    player = new ExoPlayer.Builder(this)
            .setAudioAttributes(AudioAttributes.DEFAULT, true)
            .setHandleAudioBecomingNoisy(true)
            .setWakeMode(C.WAKE_MODE_NETWORK)
            .build();

    mediaSession = new MediaSession.Builder(this, player).build();

    Intent intent = new Intent(PlaybackService.this, ItemViewerActivity.class);
    intent.putExtra("data", dataJson);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    PendingIntent pendingIntent = PendingIntent.getActivity(
            PlaybackService.this,
            103,
            intent,
            PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
    );
    mediaSession.setSessionActivity(pendingIntent);
  }


  @Nullable
  @Override
  public androidx.media3.session.MediaSession onGetSession(androidx.media3.session.MediaSession.ControllerInfo controllerInfo) {
    return mediaSession;
  }

  @Override
  public void onDestroy() {
    if (mediaSession != null) {
      if (mediaSession.getPlayer() != null) {
        mediaSession.getPlayer().release();
      }
      mediaSession.release();
      mediaSession = null;
    }
    player = null;
    super.onDestroy();
  }
}