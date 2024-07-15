package com.example.exoplayer;

import static com.example.exoplayer.VideoFilesActivity.MY_PREF;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.exoplayer.Adapter.VideoFilesAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.File;
import java.util.ArrayList;

public class PlaylistDialogue extends BottomSheetDialogFragment {

    ArrayList<MediaFiles> videoList=new ArrayList<>();
    VideoFilesAdapter videoFilesAdapter;
    BottomSheetDialog bottomSheetDialog;
    RecyclerView playlist_rv;
    TextView playlist_name;

    public PlaylistDialogue(ArrayList<MediaFiles> videoList,VideoFilesAdapter videoFilesAdapter){
        this.videoList=videoList;
        this.videoFilesAdapter=videoFilesAdapter;

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        Log.d("TAG", "onCreateDialog called");
        Log.d("TAG", "list size: "+videoList.size());
        Log.d("TAG", "list size: "+videoList.get(1));
        bottomSheetDialog=(BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        View view= LayoutInflater.from(getContext()).inflate(R.layout.playlist_bs_layout,null);
        bottomSheetDialog.setContentView(view);

        playlist_rv=(RecyclerView) view.findViewById(R.id.playlist_rv);
        playlist_name=(TextView) view.findViewById(R.id.playlist_name);

        SharedPreferences sharedPreferences=this.getActivity().getSharedPreferences(MY_PREF,Context.MODE_PRIVATE);
            String folder=sharedPreferences.getString("playListFolderName","myApp");
        Log.d("Retrivefoldername", "Retriving "+folder);
            playlist_name.setText(folder);

         videoList=fetchMedia(folder);

        videoFilesAdapter=new VideoFilesAdapter(videoList,getContext(),1 );
        playlist_rv.setAdapter(videoFilesAdapter);
        playlist_rv.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false));
        videoFilesAdapter.notifyDataSetChanged();
        return bottomSheetDialog;


    }
    private ArrayList<MediaFiles> fetchMedia(String folderName) {
        Log.d("TAG", "fetchMedia called with folder "+folderName);
        ArrayList<MediaFiles> videoFileList=new ArrayList<>();
        Uri uri= MediaStore.Video.Media.EXTERNAL_CONTENT_URI;


        String selection = MediaStore.Video.Media.DATA + " LIKE ?";
        String[] selectionArgs = new String[] { "%" + folderName + "%" };

        Cursor cursor = null;
        try {
            cursor = getContext().getContentResolver().query(uri, null, selection, selectionArgs, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    // Retrieve data from the cursor
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
}
