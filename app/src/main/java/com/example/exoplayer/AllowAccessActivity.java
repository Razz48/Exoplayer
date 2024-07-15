package com.example.exoplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class AllowAccessActivity extends AppCompatActivity {
    //    PlayerView playerView;
    ImageView exoImage;
    Button allowButton;
    public static final int STORAGE_PERMISSION = 1;
    public static final int REQUEST_PERMISSION_SETTING = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.allow_access_activity);

        exoImage = (ImageView) findViewById(R.id.exoImage);
        allowButton = (Button) findViewById(R.id.allow);

        allowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (Environment.isExternalStorageManager()) {
                        Intent intent = new Intent(AllowAccessActivity.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        try {
                            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                            intent.addCategory("android.intent.category.DEFAULT");
                            intent.setData(Uri.parse(String.format("package:%s", getApplicationContext().getPackageName())));
                            startActivityIfNeeded(intent, REQUEST_PERMISSION_SETTING);
                        } catch (Exception e) {
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                            startActivityIfNeeded(intent, REQUEST_PERMISSION_SETTING);
                        }
                    }
                }
            }
        });
//        playerView=(PlayerView) findViewById(R.id.playerView);
//
//        ExoPlayer exoPlayer = new ExoPlayer.Builder(getApplicationContext()).build();
//
//        playerView.setPlayer(exoPlayer);
//
//        MediaItem mediaItem = MediaItem.fromUri("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4");
//        MediaItem mediaItem1=MediaItem.fromUri("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4");
//        exoPlayer.addMediaItem(mediaItem1);
//        exoPlayer.addMediaItem(mediaItem);
//
//        exoPlayer.prepare();
//        exoPlayer.play();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                Intent intent = new Intent(AllowAccessActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }
    }
}