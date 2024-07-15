package com.example.exoplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.exoplayer.Adapter.VideoFilesAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VideoFilesActivity extends AppCompatActivity  {
    RecyclerView videoRv;
    ArrayList<MediaFiles> videoList=new ArrayList<>();
   static VideoFilesAdapter videoFilesAdapter;
    String folder_name;
    SearchView searchview;

    public static final String MY_PREF="my pref";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_files);
        folder_name=getIntent().getStringExtra("folderName");

        SharedPreferences.Editor editor=getSharedPreferences(MY_PREF,MODE_PRIVATE).edit();
        editor.putString("playListFolderName",folder_name);
        editor.apply();

        Log.d("SaveFolderName", "foldername "+folder_name);

        getSupportActionBar().setTitle(folder_name);
        videoRv=(RecyclerView) findViewById(R.id.videoRv);




        showVideo(); 
        
        
    }

    private void showVideo() {
        videoList=fetchMedia(folder_name);
        videoFilesAdapter=new VideoFilesAdapter(videoList,getApplicationContext(),0);
        videoRv.setAdapter(videoFilesAdapter);
        videoRv.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL,false));
        videoFilesAdapter.notifyDataSetChanged();
    }

    private ArrayList<MediaFiles> fetchMedia(String folderName) {
        ArrayList<MediaFiles> videoFileList=new ArrayList<>();
        Uri uri= MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        if (folderName == null || folderName.isEmpty()) {

            return videoFileList;
        }

        String selection = MediaStore.Video.Media.DATA + " LIKE ?";
        String[] selectionArgs = new String[] { "%" + folderName + "%" };

        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(uri, null, selection, selectionArgs, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    // Retrieve data from the cursor//
                    int idColumnIndex = cursor.getColumnIndex(MediaStore.Video.Media._ID);
                    int titleColumnIndex = cursor.getColumnIndex(MediaStore.Video.Media.TITLE);
                    int displayNameColumnIndex = cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME);
                    int sizeColumnIndex = cursor.getColumnIndex(MediaStore.Video.Media.SIZE);
                    int durationColumnIndex = cursor.getColumnIndex(MediaStore.Video.Media.DURATION);
                    int pathColumnIndex = cursor.getColumnIndex(MediaStore.Video.Media.DATA);
                    int dateAddedColumnIndex = cursor.getColumnIndex(MediaStore.Video.Media.DATE_ADDED);


                    if (idColumnIndex != -1 && titleColumnIndex != -1 &&
                            displayNameColumnIndex != -1 && sizeColumnIndex != -1 &&
                            durationColumnIndex != -1 && pathColumnIndex != -1 &&
                            dateAddedColumnIndex != -1) {

                        String id = cursor.getString(idColumnIndex);
                        String title = cursor.getString(titleColumnIndex);
                        String displayName = cursor.getString(displayNameColumnIndex);
                        String size = cursor.getString(sizeColumnIndex);
                        String duration = cursor.getString(durationColumnIndex);
                        String path = cursor.getString(pathColumnIndex);
                        String dateAdded = cursor.getString(dateAddedColumnIndex);


                        path = path.replaceAll("/+", "/");
                        path = new File(path).getAbsolutePath();


                        MediaFiles mediaFiles = new MediaFiles(id, title, displayName, size, duration, path, dateAdded);
                        videoFileList.add(mediaFiles);
                    }
                }
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
        finally {

            if (cursor != null) {
                cursor.close();
            }

        }
        return videoFileList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.video_menu,menu);
        MenuItem menuItem=menu.findItem(R.id.searchView);
        searchview=(SearchView) menuItem.getActionView();

        searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String inputs=newText.toLowerCase();
                ArrayList<MediaFiles> mediaFiles=new ArrayList<>();
                for(MediaFiles media:videoList){
                    if(media.getTitle().toLowerCase().contains(inputs)){
                        mediaFiles.add(media);
                    }
                }
                VideoFilesActivity.videoFilesAdapter.updateVideoFiles(mediaFiles);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}