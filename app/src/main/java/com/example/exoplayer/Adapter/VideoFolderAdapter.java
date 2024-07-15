package com.example.exoplayer.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.exoplayer.MediaFiles;
import com.example.exoplayer.R;
import com.example.exoplayer.VideoFilesActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VideoFolderAdapter extends RecyclerView.Adapter<VideoFolderAdapter.ViewHolder> {

    ArrayList<MediaFiles> mediaFiles;
    ArrayList<String> folderPath;
    Context context;

    public VideoFolderAdapter(ArrayList<MediaFiles> mediaFiles, ArrayList<String> folderPath, Context context) {
        this.mediaFiles = mediaFiles;
        this.folderPath = folderPath;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.folder_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int indexPath = folderPath.get(position).lastIndexOf("/");
        String nameOfFolder = folderPath.get(position).substring(indexPath + 1);
        holder.folderName.setText(nameOfFolder);
        holder.folder_Path.setText(folderPath.get(position));

        String folder = folderPath.get(position);
        int numberOfFiles = getNumberOfFilesInFolder(folder);

        holder.noOfFiles.setText(numberOfFiles + " videos");
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, VideoFilesActivity.class);
                intent.putExtra("folderName", nameOfFolder);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return folderPath.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView folderName, folder_Path, noOfFiles;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            folderName = itemView.findViewById(R.id.folderName);
            folder_Path = itemView.findViewById(R.id.folderPath);
            noOfFiles = itemView.findViewById(R.id.noOfFiles);
        }
    }

    private int getNumberOfFilesInFolder(String folderPath) {
        File folder = new File(folderPath);
        if (folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                return files.length;
            }
        }
        return 0;
    }

    public void updateFolder(ArrayList<String> files){
        folderPath=new ArrayList<>();
        folderPath.addAll(files);
        notifyDataSetChanged();
    }
}
