package com.taf.videomerge.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.taf.videomerge.R;
import com.taf.videomerge.common.FileUtils;
import com.taf.videomerge.common.PermissionUtils;
import com.taf.videomerge.model.Video;
import com.taf.videomerge.viewmodel.VideoAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity implements VideoAdapter.VideoListener {

    @BindView(R.id.rv_video)
    RecyclerView rvVideo;
    VideoAdapter videoAdapter;

    ArrayList<Video> listVideo;
    @BindView(R.id.btn_concat)
    Button btnConcat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        init();
        allowPermission();
    }

    private void init() {
        videoAdapter = new VideoAdapter(this);
    }

    private void allowPermission() {
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (PermissionUtils.hasPermission(this, permissions)) {
            loadVideo(this);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(permissions, 66);
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    public void loadVideo(Context context) {
        new AsyncTask<Void, Void, List<Video>>() {
            @Override
            protected List<Video> doInBackground(Void... voids) {
                List<Video> videos = new ArrayList<>();
                FileUtils.getVideoList(context, videos);
                return videos;
            }

            @Override
            protected void onPostExecute(List<Video> videos) {
                super.onPostExecute(videos);
                onVideoLoaded(videos);
            }
        }.execute();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 66) {
            if (PermissionUtils.checkGranted(grantResults)) {
                loadVideo(this);
            } else {
                Toast.makeText(this, "Allow string", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    public void onVideoLoaded(List<Video> listVideo) {
        this.listVideo = (ArrayList<Video>) listVideo;
        videoAdapter.setVideoListener(this);
        videoAdapter.setListVideo(listVideo);
        rvVideo.setLayoutManager(new GridLayoutManager(this, 2));
        rvVideo.setAdapter(videoAdapter);
    }


    @Override
    public void onItemClick(Video video) {

    }

    public void concatClick(View view) {
        if (videoAdapter == null) {
            Toast.makeText(this, "Wait for load all videos", Toast.LENGTH_SHORT).show();
            return;
        }
        if (videoAdapter.getVideoListSelect().size() < 2) {
            Toast.makeText(this, "Please select at least 2 video to concat", Toast.LENGTH_SHORT).show();
            return;
        }
        ArrayList<Video> listVideoSelect = videoAdapter.getVideoListSelect();
        Intent intent = new Intent(MainActivity.this, ConcatActivity.class);
        intent.putParcelableArrayListExtra("video", listVideoSelect);
        startActivity(intent);
    }

}

