package com.taf.videomerge.view;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
import com.taf.videomerge.R;
import com.taf.videomerge.model.Video;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ConcatActivity extends AppCompatActivity {

    ArrayList<Video> videoArrayList;
    @BindView(R.id.vv_main)
    VideoView vvMain;
    @BindView(R.id.pb_loading)
    ProgressBar pbLoading;

    private long countTime;

    private String outputPath;
    private List<String> listOutput = new ArrayList<>();
    public int currentPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_concar);
        ButterKnife.bind(this);

        videoArrayList = getIntent().getParcelableArrayListExtra("video");
        try {
            FFmpeg.getInstance(this).loadBinary(new LoadBinaryResponseHandler() {
                @Override
                public void onSuccess() {
                    super.onSuccess();
                    Log.d("OKOKOK", "load sucess");
                    countTime = System.currentTimeMillis();
                    pbLoading.setVisibility(View.VISIBLE);
                    currentPosition = 0;
                    convertVideo();
                }
            });

        } catch (FFmpegNotSupportedException e) {
            e.printStackTrace();
        }

    }

    private void concatVideo() {
        pbLoading.setVisibility(View.VISIBLE);
        countTime = System.currentTimeMillis();
        String[] cmd = convertVideo(videoArrayList.get(0).getPath());
        try {
            FFmpeg.getInstance(this).execute(cmd, new ExecuteBinaryResponseHandler() {
                @Override
                public void onSuccess(String message) {
                    Log.d("OKOKOK", "concat sucess");
                    pbLoading.setVisibility(View.GONE);
                    Log.d("OKOKtime", String.valueOf(System.currentTimeMillis() - countTime));
                    vvMain.setVideoPath(outputPath);
                    vvMain.start();
                    super.onSuccess(message);
                }

                @Override
                public void onFailure(String message) {
                    super.onFailure(message);
                    Log.d("OKOKOK", "concat err: " + message.substring(3000));
                }

                @Override
                public void onStart() {
                    super.onStart();
                    Log.d("OKOKOK", "onStart");
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                    Log.d("OKOKOK",  "finish");
                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            Log.d("OKOKOK", e.getMessage());
            e.printStackTrace();
        }
    }

    private String[] getCommandFFmpeReverse() {
        String output = makeSubAppFolder(makeAppFolder("ConcatVideo"), "GifFile") + "/GIF_" + System.currentTimeMillis() + ".mp4";
        outputPath = output;
        ArrayList<String> listCmd = new ArrayList<>();
        listCmd.add("-i");
        listCmd.add(videoArrayList.get(0).getPath());
        listCmd.add("-i");
        listCmd.add(videoArrayList.get(1).getPath());
        listCmd.add("-i");
        listCmd.add(videoArrayList.get(2).getPath());
        listCmd.add("-strict");
        listCmd.add("experimental");
        listCmd.add("-vcodec");
        listCmd.add("mpeg4");
        listCmd.add("-filter_complex");
        listCmd.add("[0:0][0:1][1:0][1:1][2:0][2:1]concat=n=3:v=1:a=1 [v] [a]");
        listCmd.add("-map");
        listCmd.add("[v]");
        listCmd.add("-map");
        listCmd.add("[a]");
        listCmd.add("-preset");
        listCmd.add("ultrafast");
        listCmd.add(output);
        return listCmd.toArray(new String[listCmd.size()]);
    }

    private void convertVideo() {
        String[] cmd = convertVideo(videoArrayList.get(currentPosition).getPath());
        try {
            FFmpeg.getInstance(this).execute(cmd, new ExecuteBinaryResponseHandler() {
                @Override
                public void onSuccess(String message) {
                    currentPosition++;
                    if (currentPosition == videoArrayList.size()) {
                        concatVideoSameRate();
                    } else {
                        Log.d("OKOKOK", "concat sucess" + currentPosition);
                        convertVideo();
                    }
                    super.onSuccess(message);
                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            Log.d("OKOKOK", e.getMessage());
            e.printStackTrace();
        }
    }

    private void concatVideoSameRate() {
        writeToFile();
        try {
            String[] cmd = getCommandConcatSame();
            FFmpeg.getInstance(this).execute(cmd, new ExecuteBinaryResponseHandler() {
                @Override
                public void onSuccess(String message) {
                    Log.d("OKOKtime", String.valueOf(System.currentTimeMillis() - countTime));
                    pbLoading.setVisibility(View.GONE);
                    vvMain.setVideoPath(outputPath);
                    Toast.makeText(com.taf.videomerge.view.ConcatActivity.this, outputPath, Toast.LENGTH_SHORT).show();
                    vvMain.start();
                    deleteTempFile();
                    super.onSuccess(message);
                }

                @Override
                public void onFailure(String message) {
                    super.onFailure(message);
                    Log.d("OKOKOK", "err sum " + message);
                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            Log.d("OKOKOK", e.getMessage());
            e.printStackTrace();
        }
    }

    private String[] getCommandConcatSame() {
        String output = makeSubAppFolder(makeAppFolder("ConcatVideo"), "Video") + "/GIF_" + System.currentTimeMillis() + ".mp4";
        outputPath = output;
        ArrayList<String> listCmd = new ArrayList<>();
        listCmd.add("-f");
        listCmd.add("concat");
        listCmd.add("-safe");
        listCmd.add("0");
        listCmd.add("-i");
        listCmd.add("/storage/emulated/0/DCIM/ConcatVideo/TempFile/video.txt");
        listCmd.add("-c");
        listCmd.add("copy");
        listCmd.add(output);
        return listCmd.toArray(new String[listCmd.size()]);
    }

    private String[] convertVideo(String path) {
        String output = makeSubAppFolder(makeAppFolder("ConcatVideo"), "TempFile") + "/temp_" + System.currentTimeMillis() + ".mp4";
        ArrayList<String> listCmd = new ArrayList<>();
        listCmd.add("-i");
        listCmd.add(path);
        listCmd.add("-vcodec");
        listCmd.add("libx264");
        listCmd.add("-vf");
        listCmd.add("scale=720:720:force_original_aspect_ratio=decrease,pad=720:720:(ow-iw)/2:(oh-ih)/2");
        listCmd.add("-r"); // frame per second
        listCmd.add("24");
        listCmd.add("-b:v"); // frame rate
        listCmd.add("1M");
        listCmd.add("-preset");
        listCmd.add("ultrafast");
        listCmd.add(output);
        listOutput.add(output);
        return listCmd.toArray(new String[listCmd.size()]);
    }

    private void writeToFile() {
        try {
            String filePath = "/storage/emulated/0/DCIM/ConcatVideo/TempFile/video.txt";
            File file = new File(filePath);
            if (!file.exists() || !file.isFile()) {
                file.createNewFile();
            }
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(file, false));
            for (int i = 0; i < listOutput.size(); i++) {
                String contentElement = "file '" + listOutput.get(i) + "'";
                outputStreamWriter.write(contentElement);
                if (i != listOutput.size() - 1) {
                    outputStreamWriter.write("\r\n");
                }
            }
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private void checkAudio(String path) {
        ArrayList<String> listCmd = new ArrayList<>();
        listCmd.add("-i");
        listCmd.add(path);
        String[] cmd = listCmd.toArray(new String[listCmd.size()]);
        try {
            FFmpeg.getInstance(this).execute(cmd, new ExecuteBinaryResponseHandler() {
                @Override
                public void onSuccess(String message) {
                    super.onSuccess(message);
                }

                @Override
                public void onFailure(String message) {
                    super.onFailure(message);
                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            e.printStackTrace();
        }
    }

    public String makeSubAppFolder(String path, String subFolderName) {
        String subFolder = null;
        File file = new File(path + "/" + subFolderName);
        if (!file.exists()) {
            file.mkdirs();
            subFolder = file.getPath();
        } else {
            File folder = new File(path + "/" + subFolderName);
            subFolder = folder.getPath();
        }
        return subFolder;
    }

    public String makeAppFolder(String folderName) { //getExternalStorageDirectory
        String path = null;
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/" + folderName);
        if (!file.exists()) {
            file.mkdirs();
            path = file.getPath();
        } else {
            File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/" + folderName);

            path = folder.getPath();
        }
        return path;
    }

    private void deleteTempFile() {
        for (String path : listOutput) {
            File file = new File(path);
            if (file.exists()) {
                file.delete();
            }
        }
    }
}
