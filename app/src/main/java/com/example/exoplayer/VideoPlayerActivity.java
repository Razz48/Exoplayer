package com.example.exoplayer;

import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.C;
import androidx.media3.common.MediaItem;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;
import androidx.media3.common.TrackSelectionParameters;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.common.util.Util;
import androidx.media3.datasource.DefaultDataSourceFactory;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.source.ConcatenatingMediaSource;
import androidx.media3.exoplayer.source.ConcatenatingMediaSource2;
import androidx.media3.exoplayer.source.MediaSource;
import androidx.media3.exoplayer.source.ProgressiveMediaSource;
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector;
import androidx.media3.ui.AspectRatioFrameLayout;
import androidx.media3.ui.DefaultTimeBar;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.exoplayer.Adapter.PlaybackIconsAdapter;
import com.example.exoplayer.Adapter.VideoFilesAdapter;

import java.io.File;
import java.util.ArrayList;

 public class VideoPlayerActivity extends AppCompatActivity implements View.OnClickListener {

   DefaultTimeBar timeBar;
    PlayerView playerView;
    RelativeLayout root_layout;
    ExoPlayer exoPlayer;
    ConcatenatingMediaSource concatenatingMediaSource;
    int position;
    String videoName;
    ArrayList<MediaFiles> mVideoArrayList;
    VideoFilesAdapter videoFilesAdapter;
    ImageView videoBack,lock,unlock,backward,previous,play,pause,forward,next,scaling,videoList;
    TextView videoTitle;
    private ControlsMode controlsMode;
    public enum ControlsMode{
        LOCK,FULLSCREEN;
    }

    ArrayList<IconModel> iconModelsList=new ArrayList<>();
    PlaybackIconsAdapter playbackIconsAdapter;
    RecyclerView recyclerview_icon;


    @SuppressLint("MissingInflatedId")
    @OptIn(markerClass = UnstableApi.class) @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreen();


//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_video_player);

        getSupportActionBar().hide();

        playerView=(PlayerView) findViewById(R.id.playerView);
        videoBack=(ImageView) findViewById(R.id.videoBack) ;
        backward=(ImageView) findViewById(R.id.backward) ;
        previous=(ImageView) findViewById(R.id.previous) ;
        play=(ImageView) findViewById(R.id.play) ;
        pause=(ImageView) findViewById(R.id.pause) ;
        forward=(ImageView) findViewById(R.id.forward) ;
        next=(ImageView) findViewById(R.id.next) ;
        lock=(ImageView) findViewById(R.id.lock);
        unlock=(ImageView) findViewById(R.id.unlock);
        scaling=(ImageView) findViewById(R.id.scaling);
        root_layout=(RelativeLayout) findViewById(R.id.root_layout);
        videoList=(ImageView) findViewById(R.id.videoList);

        //
        recyclerview_icon=(RecyclerView) findViewById(R.id.recyclerview_icon);





        position=getIntent().getIntExtra("position",1);
        videoName=getIntent().getStringExtra("videoTitle");
        mVideoArrayList=getIntent().getExtras().getParcelableArrayList("videoArrayList");

        videoTitle=(TextView) findViewById(R.id.videoTitle);

        videoTitle.setText(videoName);
        videoBack.setOnClickListener(this);
        backward.setOnClickListener(this);
        previous.setOnClickListener(this);
        play.setOnClickListener(this);
        pause.setOnClickListener(this);
        forward.setOnClickListener(this);
        next.setOnClickListener(this);
        lock.setOnClickListener(this);
        unlock.setOnClickListener(this);
        scaling.setOnClickListener(firstListener);
        videoList.setOnClickListener(this);

        iconModelsList.add(new IconModel(R.drawable.ic_right,""));
        iconModelsList.add(new IconModel(R.drawable.ic_night_mode,"Night Mode"));
        iconModelsList.add(new IconModel(R.drawable.round_volume_off_24,"Mute"));

        playbackIconsAdapter=new PlaybackIconsAdapter(iconModelsList,getApplicationContext());
        recyclerview_icon.setAdapter(playbackIconsAdapter);
        recyclerview_icon.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,true));
        playbackIconsAdapter.notifyDataSetChanged();
        playVideo();
    }

    @OptIn(markerClass = UnstableApi.class) private void playVideo() {
        String path=mVideoArrayList.get(position).getPath();
        Uri uri=Uri.parse(path);
        exoPlayer = new ExoPlayer.Builder(getApplicationContext()).build();



        DefaultDataSourceFactory defaultDataSourceFactory=new DefaultDataSourceFactory(this, Util.getUserAgent(this,"myapp"));
        concatenatingMediaSource=new ConcatenatingMediaSource();
        for(int i=0;i<mVideoArrayList.size();i++){
            new File(String.valueOf(mVideoArrayList.get(i)));
            MediaSource mediaSource=new ProgressiveMediaSource.Factory(defaultDataSourceFactory).createMediaSource(MediaItem.fromUri(Uri.parse(String.valueOf(uri))));
            concatenatingMediaSource.addMediaSource(mediaSource);
        }
        playerView.setPlayer(exoPlayer);
        playerView.setKeepScreenOn(true);
        exoPlayer.prepare(concatenatingMediaSource);
        exoPlayer.seekTo(position, C.TIME_UNSET);
        playError();
    }

    private void playError() {
        exoPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlayerError(PlaybackException error) {
                Player.Listener.super.onPlayerError(error);
                Toast.makeText(VideoPlayerActivity.this, "Video playing Error", Toast.LENGTH_SHORT).show();
            }
        });
        exoPlayer.setPlayWhenReady(true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(exoPlayer.isPlaying()){
            exoPlayer.stop();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        exoPlayer.setPlayWhenReady(false);
        exoPlayer.getPlaybackState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        exoPlayer.setPlayWhenReady(true);
        exoPlayer.getPlaybackState();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        exoPlayer.setPlayWhenReady(true);
        exoPlayer.getPlaybackState();
    }
    public void setFullScreen(){
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }

     @Override
     public void onClick(View v) {
         int id=v.getId();
         if(id==R.id.videoList){
             PlaylistDialogue playlistDialogue=new PlaylistDialogue(mVideoArrayList,videoFilesAdapter);
             playlistDialogue.show(getSupportFragmentManager(),playlistDialogue.getTag());
         }
         if(id==R.id. videoBack){
             finish();
         }
         if(id==R.id.lock){
            controlsMode=ControlsMode.FULLSCREEN;
            root_layout.setVisibility(View.INVISIBLE);
            unlock.setVisibility(View.VISIBLE);
            lock.setVisibility(View.GONE);
             Toast.makeText(this, "locked", Toast.LENGTH_SHORT).show();
         }
         if(id==R.id.unlock){
             controlsMode=ControlsMode.LOCK;
             root_layout.setVisibility(View.VISIBLE);
             lock.setVisibility(View.VISIBLE);
             unlock.setVisibility(View.GONE);
             Toast.makeText(this, "unlocked", Toast.LENGTH_SHORT).show();

         }
         if(id==R.id.backward){
             exoPlayer.seekTo(exoPlayer.getCurrentPosition() - 10000);
         }
         if(id==R.id.previous){
             try {
                 exoPlayer.stop();
                 position--;
                 videoName=mVideoArrayList.get(position).getTitle();
                 videoTitle.setText(videoName);
                 playVideo();
             }catch (Exception e){
                 Toast.makeText(this, "no prev video", Toast.LENGTH_SHORT).show();
                 finish();
             }

         }
         if(id==R.id.pause){
             exoPlayer.pause();
             pause.setVisibility(View.GONE);
             play.setVisibility(View.VISIBLE);

         }
         if(id==R.id.play){
             exoPlayer.play();
             play.setVisibility(View.GONE);
             pause.setVisibility(View.VISIBLE);
         }
         if(id==R.id.forward){
             exoPlayer.seekTo(exoPlayer.getCurrentPosition() + 10000);
         }
         if(id==R.id.next){
            try {
                exoPlayer.stop();
                position++;
                videoName=mVideoArrayList.get(position).getTitle();
                videoTitle.setText(videoName);
                playVideo();
            }catch (Exception e){
                Toast.makeText(this, "No next video", Toast.LENGTH_SHORT).show();
                finish();

            }
         }
     }

     View.OnClickListener firstListener=new View.OnClickListener() {
         @OptIn(markerClass = UnstableApi.class) @Override
         public void onClick(View v) {
             getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
             setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
             RelativeLayout.LayoutParams params=(RelativeLayout.LayoutParams)playerView.getLayoutParams();
             params.width=params.MATCH_PARENT;
             params.height=params.MATCH_PARENT;
             playerView.setLayoutParams(params);
//            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
//            exoPlayer.setVideoScalingMode(C.VIDEO_SCALING_MODE_DEFAULT);
            scaling.setImageResource(R.drawable.baseline_zoom_out_map_24);
             Toast.makeText(VideoPlayerActivity.this, "full screen", Toast.LENGTH_SHORT).show();
             scaling.setOnClickListener(secondListener);
         }
     };

//    View.OnClickListener secondListener=new View.OnClickListener() {
//        @OptIn(markerClass = UnstableApi.class) @Override
//        public void onClick(View v) {
//            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
//            exoPlayer.setVideoScalingMode(C.VIDEO_SCALING_MODE_DEFAULT);
//            scaling.setImageResource(R.drawable.round_fit_screen_24);
//            Toast.makeText(VideoPlayerActivity.this, "zoom", Toast.LENGTH_SHORT).show();
//            scaling.setOnClickListener(thirdListener);
//        }
//    };

    View.OnClickListener secondListener=new View.OnClickListener() {
        @OptIn(markerClass = UnstableApi.class) @Override
        public void onClick(View v) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            RelativeLayout.LayoutParams params=(RelativeLayout.LayoutParams)playerView.getLayoutParams();
            params.width=params.MATCH_PARENT;
            params.height=params.MATCH_PARENT;
            playerView.setLayoutParams(params);
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
//            exoPlayer.setVideoScalingMode(C.VIDEO_SCALING_MODE_DEFAULT);
            scaling.setImageResource(R.drawable.fullscreen);
            Toast.makeText(VideoPlayerActivity.this, "fit", Toast.LENGTH_SHORT).show();
            scaling.setOnClickListener(firstListener);
        }
    };
 }