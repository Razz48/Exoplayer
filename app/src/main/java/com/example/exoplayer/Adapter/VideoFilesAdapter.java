package com.example.exoplayer.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.media3.common.util.UnstableApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.exoplayer.MediaFiles;
import com.example.exoplayer.R;
import com.example.exoplayer.VideoPlayerActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VideoFilesAdapter extends RecyclerView.Adapter<VideoFilesAdapter.ViewHolder> {

    ArrayList<MediaFiles> videoList;
    Context context;
    BottomSheetDialog bottomSheetDialog;
    private  int viewType;

    public VideoFilesAdapter(ArrayList<MediaFiles> videoList, Context context,int viewType) {
        this.videoList = videoList;
        this.context = context;
        this.viewType=viewType;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View view=inflater.inflate(R.layout.video_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    holder.videoName.setText(videoList.get(position).getDisplayName());
    String size=videoList.get(position).getSize();
    holder.videoSize.setText(android.text.format.Formatter.formatFileSize(context,Long.parseLong(size)));
    double millisecond= Double.parseDouble(videoList.get(position).getDuration());
    holder.videoDuration.setText(timeConversion((long) millisecond));
        Glide.with(context).load(new File(videoList.get(position).getPath())).into(holder.thumbnail);

        if(viewType==0){
            holder.menuMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bottomSheetDialog = new BottomSheetDialog(context,R.style.BottomSheetTheme);
                    View bsView = LayoutInflater.from(context).inflate(R.layout.video_bs_layout,
                            v.findViewById(R.id.bottomSheet));

//                bsView.findViewById(R.id.bs_play).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        holder.itemView.performClick();
//                        bottomSheetDialog.dismiss();
//                    }
//                });
                    bottomSheetDialog.setContentView(bsView);
                    bottomSheetDialog.show();

                }
            });
        }else{
            holder.menuMore.setVisibility(View.GONE);
            holder.videoName.setTextColor(Color.WHITE);
            holder.videoSize.setTextColor(Color.WHITE);
        }


    holder.itemView.setOnClickListener(new View.OnClickListener() {
        @OptIn(markerClass = UnstableApi.class) @Override
        public void onClick(View v) {
            Intent intent=new Intent(context, VideoPlayerActivity.class);
            intent.putExtra("position",position);
            intent.putExtra("videoTitle",videoList.get(position).getDisplayName());
            Bundle bundle=new Bundle();
            bundle.putParcelableArrayList("videoArrayList",videoList);
            intent.putExtras(bundle);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            if(viewType==1){
                ((Activity) context).finish();
            }
        }
    });
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail,menuMore;
        TextView videoDuration,videoName,videoSize;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnail=(ImageView) itemView.findViewById(R.id.thumbnail);
            menuMore=(ImageView) itemView.findViewById(R.id.menuMore);
            videoDuration=(TextView) itemView.findViewById(R.id.videoDuration);
            videoName=(TextView) itemView.findViewById(R.id.videoName);
            videoSize=(TextView) itemView.findViewById(R.id.videoSize);
        }
    }

    public String timeConversion(long value){
        String videoTime;
        int duration=(int) value;
        int hrs=(duration/3600000);
        int mns=(duration/60000)%60000;
        int scs=duration%60000/1000;
        if(hrs>0){
            videoTime=String.format("%02d:%02d:%02d",hrs,mns,scs);
        }else{
            videoTime=String.format("%02d:%02d",mns,scs);
        }
        return videoTime;
    }

   public void updateVideoFiles(ArrayList<MediaFiles> files){
        videoList=new ArrayList<>();
        videoList.addAll(files);
        notifyDataSetChanged();
    }
}
