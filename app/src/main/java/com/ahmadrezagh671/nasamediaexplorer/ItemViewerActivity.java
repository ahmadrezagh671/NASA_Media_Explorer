package com.ahmadrezagh671.nasamediaexplorer;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import static com.ahmadrezagh671.nasamediaexplorer.Utilities.Display.enterFullScreenMode;
import static com.ahmadrezagh671.nasamediaexplorer.Utilities.Display.exitFullScreenMode;
import static com.ahmadrezagh671.nasamediaexplorer.Utilities.Display.setViewFullscreen;
import static com.ahmadrezagh671.nasamediaexplorer.Utilities.Display.setViewHeightAndWidth;
import static com.ahmadrezagh671.nasamediaexplorer.Utilities.Utilities.isServiceRunning;
import static com.ahmadrezagh671.nasamediaexplorer.Utilities.Utilities.setImageViewUtil;
import static com.ahmadrezagh671.nasamediaexplorer.Utilities.Utilities.setTextUtil;

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MediaMetadata;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.datasource.DataSource;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory;
import androidx.media3.session.MediaController;
import androidx.media3.session.SessionToken;
import androidx.media3.ui.PlayerView;

import com.ahmadrezagh671.nasamediaexplorer.Models.Item;
import com.ahmadrezagh671.nasamediaexplorer.Models.ItemLink;
import com.ahmadrezagh671.nasamediaexplorer.Services.PlaybackService;
import com.ahmadrezagh671.nasamediaexplorer.Utilities.ExoPlayerCache;
import com.ahmadrezagh671.nasamediaexplorer.Utilities.VolleySingleton;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.concurrent.ExecutionException;

public class ItemViewerActivity extends AppCompatActivity {

    private static final String TAG = "ItemViewerActivity";

    String dataJson;

    RequestQueue requestQueue;

    Item item;

    ImageView imageView;

    View valuesLayout;

    PlayerView playerViewVideo,playerViewAudio;
    ExoPlayer playerVideo;
    MediaController mediaControllerAudio;

    TextView titleTV, locationTV, dateTV, descriptionTV, keywordsTV,centerTV;

    @OptIn(markerClass = UnstableApi.class)
    @Override
    public void onBackPressed() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            playerViewVideo.setFullscreenButtonState(false);
        }else {
            super.onBackPressed();
            finish();
        }
    }

    @Override
    protected void onStop() {
        if (playerVideo != null){
            playerVideo.pause();
        }
        super.onStop();
    }
    @Override
    protected void onDestroy() {
        if (playerVideo != null){
            playerVideo.release();
        }
        super.onDestroy();
    }


    @Override
    protected void onPause() {
        if (playerVideo != null){
            playerVideo.pause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (playerVideo != null){
            //player.play();
        }
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dataJson = getIntent().getStringExtra("data");
        item = new Gson().fromJson(dataJson,Item.class);

        WindowCompat.setDecorFitsSystemWindows(getWindow(),false);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_item_viewer);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, item.getMedia_type().equals("audio") ? systemBars.bottom : 0);
            return insets;
        });

        titleTV = findViewById(R.id.titleTV);
        locationTV = findViewById(R.id.locationTV);
        dateTV = findViewById(R.id.dateTV);
        descriptionTV = findViewById(R.id.descriptionTV);
        keywordsTV = findViewById(R.id.keywordsTV);
        centerTV = findViewById(R.id.centerTV);

        valuesLayout = findViewById(R.id.valuesLayout);

        setTextUtil(titleTV,item.getTitle(),"title",R.color.text_color_black);
        setTextUtil(locationTV,item.getLocation(),"location",R.color.text_color_gray);
        setTextUtil(centerTV,item.getCenter(),"center",R.color.text_color_gray);
        setTextUtil(dateTV,item.getDate_created(),"date",R.color.text_color_gray);
        setTextUtil(descriptionTV,item.getDescription(),null,R.color.text_color_black);
        setTextUtil(keywordsTV,item.getKeywordsString(),null,R.color.blue);

        if (item.getMedia_type().equals("video")){
            playerViewVideo = findViewById(R.id.playerViewVideo);
            playerViewVideo.setVisibility(VISIBLE);

            setVideoView();
        } else if (item.getMedia_type().equals("image")) {
            imageView = findViewById(R.id.imageView);
            imageView.setVisibility(VISIBLE);

            setImageView();
        }else if (item.getMedia_type().equals("audio")){
            playerViewAudio = findViewById(R.id.playerViewAudio);
            playerViewAudio.setVisibility(VISIBLE);

            setAudioView();
        }
    }

    @OptIn(markerClass = UnstableApi.class)
    private void setAudioView() {
        SharedPreferences prefs = getSharedPreferences("ahmadrezagh671_nasamediaexplorer", MODE_PRIVATE);

        // check if another item is playing close it.
        if (isServiceRunning(this,PlaybackService.class)){
            if (!prefs.getString("lastAudioDataID","").equals(item.getNasa_id())) {
                stopService(new Intent(this, PlaybackService.class));
            }
        }

        prefs.edit().putString("lastAudioDataID", item.getNasa_id()).apply();
        prefs.edit().putString("lastAudioData", dataJson).apply();

        SessionToken sessionToken = new SessionToken(this, new ComponentName(this, PlaybackService.class));
        ListenableFuture<MediaController> controllerFuture = new MediaController.Builder(this, sessionToken).buildAsync();
        controllerFuture.addListener(() -> {
            // Call controllerFuture.get() to retrieve the MediaController.
            // MediaController implements the Player interface, so it can be
            // attached to the PlayerView UI component.
            try {
                playerViewAudio.setPlayer(controllerFuture.get());
                mediaControllerAudio = controllerFuture.get();

            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }

            if (mediaControllerAudio.isPlaying()){

            }else {
                requestQueue = VolleySingleton.getmInstance(this).getRequestQueue();
                JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(item.getHref(), new Response.Listener<JSONArray>() {
                    @OptIn(markerClass = UnstableApi.class)
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            String audioUrl = "";
                            for (int i = 0; i < response.length(); i++) {
                                if (response.getString(i).contains("~orig")) {
                                    audioUrl = response.getString(i);
                                    if (audioUrl.contains("http://")) audioUrl = audioUrl.replace("http://","https://");
                                    break;
                                }
                            }
                            if (!audioUrl.isEmpty()) {
                                MediaItem mediaItem =
                                        new MediaItem.Builder()
                                                .setMediaId("media-1")
                                                .setUri(audioUrl)
                                                .setMediaMetadata(
                                                        new MediaMetadata.Builder()
                                                                .setArtist(item.getCenter())
                                                                .setTitle(item.getTitle())
                                                                //.setArtworkUri(Uri.parse(ART_WORK_URI))
                                                                .build())
                                                .build();

                                mediaControllerAudio.setMediaItem(mediaItem);
                                mediaControllerAudio.prepare();
                                mediaControllerAudio.play();
                            }

                        } catch (JSONException e) {
                            //Log.e(TAG, e.getMessage());
                            Toast.makeText(ItemViewerActivity.this, "Audio not found", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Log.e(TAG, error.getMessage());
                        Toast.makeText(ItemViewerActivity.this, "Audio not found", Toast.LENGTH_SHORT).show();
                    }
                });

                requestQueue.add(jsonArrayRequest);
            }
            playerViewAudio.showController();
        }, MoreExecutors.directExecutor());
    }

    private void setVideoView() {

        if (isServiceRunning(this,PlaybackService.class)){
            stopService(new Intent(this, PlaybackService.class));
        }

        requestQueue = VolleySingleton.getmInstance(this).getRequestQueue();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(item.getHref(), new Response.Listener<JSONArray>() {
            @OptIn(markerClass = UnstableApi.class)
            @Override
            public void onResponse(JSONArray response) {
                try {
                    // select video quality with priority {"~medium.mp4","~mobile.mp4","~orig.mp4"}
                    String videoUrl = "";
                    String[] qualities = new String[]{"~medium.mp4","~mobile.mp4","~orig.mp4"};
                    for (String quality : qualities) {
                        if (videoUrl.isEmpty()) {
                            for (int j = 0; j < response.length(); j++) {
                                if (response.getString(j).contains(quality)) {
                                    videoUrl = response.getString(j);
                                    if (videoUrl.contains("http://"))
                                        videoUrl = videoUrl.replace("http://", "https://");
                                    break;
                                }
                            }
                        }
                    }

                    if (!videoUrl.isEmpty()) {
                        DataSource.Factory dataSourceFactory = ExoPlayerCache.getDataSourceFactory(ItemViewerActivity.this);
                        playerVideo = new ExoPlayer.Builder(ItemViewerActivity.this)
                                .setMediaSourceFactory(new DefaultMediaSourceFactory(dataSourceFactory)) // for cache
                                .build();
                        playerViewVideo.setPlayer(playerVideo);
                        playerViewVideo.setControllerShowTimeoutMs(2000);
                        MediaItem mediaItem = MediaItem.fromUri(videoUrl);

                        playerVideo.setMediaItem(mediaItem);

                        playerVideo.setRepeatMode(Player.REPEAT_MODE_OFF);
                        playerViewVideo.setFullscreenButtonClickListener(new PlayerView.FullscreenButtonClickListener() {
                            @Override
                            public void onFullscreenButtonClick(boolean isFullscreen) {
                                if (isFullscreen){
                                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                                }else {
                                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                                }
                            }
                        });

                        playerVideo.prepare();
                        playerVideo.play();
                    }

                } catch (JSONException e) {
                    //Log.e(TAG, e.getMessage().toString());
                    Toast.makeText(ItemViewerActivity.this, "Video not found", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e(TAG, error.getMessage().toString());
                Toast.makeText(ItemViewerActivity.this, "Video not found", Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(jsonArrayRequest);

        setViewHeightAndWidth(playerViewVideo,item.getPreviewLink().getHeight(),item.getPreviewLink().getWidth(),this,0.8f,false);
    }

    public void setImageView(){
        ItemLink itemLink = item.getCanonicalLink();
        setViewHeightAndWidth(imageView,itemLink.getHeight(),itemLink.getWidth(),this,0.8f,false);

        setImageViewUtil(this,imageView,itemLink.getHref(),null);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        boolean isLandscape = newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE;

        if (isLandscape) {
            enterFullScreenMode(this);
            setViewFullscreen(playerViewVideo,this);
            valuesLayout.setVisibility(GONE);
        } else {
            exitFullScreenMode(this);
            setViewHeightAndWidth(playerViewVideo,item.getPreviewLink().getHeight(),item.getPreviewLink().getWidth(),this,0.8f,false);
            valuesLayout.setVisibility(VISIBLE);
        }
    }
}

