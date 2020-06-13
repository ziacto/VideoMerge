package com.taf.videomerge.viewmodel;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.taf.videomerge.R;
import com.taf.videomerge.model.Video;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoHolder> {

    private List<Video> listVideo;
    private Context context;
    private VideoListener videoListener;

    private static final int NUMBER_COLUMN = 2;
    private static final int NUMBER_ROW = 4;

    private ArrayList<Video> videoListSelect;

    public ArrayList<Video> getVideoListSelect() {
        return videoListSelect;
    }

    public VideoAdapter(Context context) {
        this.context = context;
        videoListSelect = new ArrayList<>();
    }

    public List<Video> getListVideo() {
        return listVideo;
    }

    public void setListVideo(List<Video> listVideo) {
        this.listVideo = listVideo;
    }

    public void setVideoListener(VideoListener videoListener) {
        this.videoListener = videoListener;
    }

    @NonNull
    @Override
    public VideoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);
        return new VideoHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final VideoHolder holder, int position) {
        holder.onBind(listVideo.get(position));
        holder.imgItemVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int index = checkContain(listVideo.get(position));
                if (index == -1) {
                    videoListSelect.add(listVideo.get(position));
                } else {
                    videoListSelect.remove(index);
                }
                notifyItemChanged(position);
            }
        });
    }

    private int checkContain(Video videoCheck) {
        for (int i = 0; i < videoListSelect.size(); i++) {
            if (videoListSelect.get(i).getPath().equals(videoCheck.getPath())) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int getItemCount() {
        return listVideo.size();
    }

    class VideoHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.img_item_video)
        ImageView imgItemVideo;
        @BindView(R.id.layout_item_video)
        RelativeLayout layoutItemVideo;
        @BindView(R.id.img_check)
        ImageView imgCheck;

        VideoHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void onBind(Video video) {
            DisplayMetrics metrics = new DisplayMetrics();
            ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int width = metrics.widthPixels;
            int height = metrics.heightPixels;
            ViewGroup.LayoutParams layoutParams = layoutItemVideo.getLayoutParams();
            layoutParams.width = width / NUMBER_COLUMN;
            layoutParams.height = height / NUMBER_ROW;
            layoutItemVideo.setLayoutParams(layoutParams);
            Bitmap thumb = ThumbnailUtils.createVideoThumbnail(video.getPath(), MediaStore.Video.Thumbnails.MICRO_KIND);
            Glide.with(context).load(thumb)
                    .thumbnail(0.1f)
                    .into(imgItemVideo);
            int index = checkContain(video);
            if (index == -1) {
                imgCheck.setVisibility(View.GONE);
            } else {
                imgCheck.setVisibility(View.VISIBLE);
            }
        }
    }

    public interface VideoListener {
        void onItemClick(Video video);
    }
}

