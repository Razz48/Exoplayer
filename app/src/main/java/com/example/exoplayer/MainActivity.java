package com.example.exoplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;

import com.example.exoplayer.Adapter.VideoFolderAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ArrayList<MediaFiles> mediaFiles=new ArrayList<>();
    ArrayList<String> allFolderList=new ArrayList<>();
    RecyclerView folderRv;
    SearchView search;
    SwipeRefreshLayout swipeRefresh;
    static VideoFolderAdapter videoFolderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        folderRv=(RecyclerView) findViewById(R.id.folderRv);
        swipeRefresh=(SwipeRefreshLayout) findViewById(R.id.swipeRefresh) ;
        showFolder();

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                showFolder();
                swipeRefresh.setRefreshing(false);
            }
        });

    }

    private void showFolder() {
        mediaFiles=fetchMedia();
        videoFolderAdapter=new VideoFolderAdapter(mediaFiles,allFolderList,getApplicationContext());
        folderRv.setAdapter(videoFolderAdapter);
        folderRv.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL,false));
        videoFolderAdapter.notifyDataSetChanged();


    }

    private ArrayList<MediaFiles> fetchMedia() {
        ArrayList<MediaFiles> mediaFilesList=new ArrayList<>();
        Uri uri= MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Cursor cursor= getContentResolver().query(uri,null,null,null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    // Retrieve column indices
                    int idColumnIndex = cursor.getColumnIndex(MediaStore.Video.Media._ID);
                    int titleColumnIndex = cursor.getColumnIndex(MediaStore.Video.Media.TITLE);
                    int displayNameColumnIndex = cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME);
                    int sizeColumnIndex = cursor.getColumnIndex(MediaStore.Video.Media.SIZE);
                    int durationColumnIndex = cursor.getColumnIndex(MediaStore.Video.Media.DURATION);
                    int pathColumnIndex = cursor.getColumnIndex(MediaStore.Video.Media.DATA);
                    int dateAddedColumnIndex = cursor.getColumnIndex(MediaStore.Video.Media.DATE_ADDED);

                    // Check if all column indices are valid
                    if (idColumnIndex != -1 && titleColumnIndex != -1 && displayNameColumnIndex != -1 &&
                            sizeColumnIndex != -1 && durationColumnIndex != -1 && pathColumnIndex != -1 && dateAddedColumnIndex != -1) {

                        // Retrieve values from the cursor using the column indices
                        String id = cursor.getString(idColumnIndex);
                        String title = cursor.getString(titleColumnIndex);
                        String displayName = cursor.getString(displayNameColumnIndex);
                        String size = cursor.getString(sizeColumnIndex);
                        String duration = cursor.getString(durationColumnIndex);
                        String path = cursor.getString(pathColumnIndex);
                        String dateAdded = cursor.getString(dateAddedColumnIndex);

                        // Normalize the path to avoid duplicates
                        path = path.replaceAll("/+", "/");
                        path = new File(path).getAbsolutePath(); // Get absolute path to avoid relative path issues


                        MediaFiles mediaFiles = new MediaFiles(id, title, displayName, size, duration, path, dateAdded);

                        int lastIndex = path.lastIndexOf("/");
                        String folderPath = path.substring(0, lastIndex);

                        if (!allFolderList.contains(folderPath)) {
                            allFolderList.add(folderPath);
                        }


                        mediaFilesList.add(mediaFiles);
                    }
                }
                cursor.close();
            }
        }
        return mediaFilesList;
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.video_menu,menu);
        MenuItem menuItem=menu.findItem(R.id.searchView);
        search=(SearchView) menuItem.getActionView();

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String inputs=newText.toLowerCase();
                ArrayList<String> folderList=new ArrayList<>();
                for(String name: allFolderList){
                    if(name.toLowerCase().contains(inputs)){
                        folderList.add(name);
                    }
                }
                MainActivity.videoFolderAdapter.updateFolder(folderList);
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }
}