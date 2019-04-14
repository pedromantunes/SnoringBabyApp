package com.example.kamran.bluewhite;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class VideoFullActivity extends AppCompatActivity {

    private VideoView videoView;
    private MediaController mediaController;
    FileInputStream fstream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_video_layout);

        videoView = (VideoView) findViewById(R.id.videoViewFull);
        String fullScreen =  getIntent().getStringExtra("fullScreenInd");
        if("y".equals(fullScreen)){
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getSupportActionBar().hide();
        }

        try {
            StringBuffer sbuffer = new StringBuffer();
            fstream = openFileInput("video_uri");
            int i;
            sbuffer = new StringBuffer();
            while ((i = fstream.read())!= -1){
                sbuffer.append((char)i);
            }
            fstream.close();

            String video_uri[] = sbuffer.toString().split("\n");
            Uri videoUri = Uri.parse(video_uri[0]);

            videoView.setVideoURI(videoUri);

            mediaController = new FullScreenMediaController(this);
            mediaController.setAnchorView(videoView);

            videoView.setMediaController(mediaController);
            videoView.start();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
