package com.example.kamran.bluewhite;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ReportActivity extends AppCompatActivity {

    FileInputStream fstream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_report);

        try {
            fstream = openFileInput("user_details");
            StringBuffer sbuffer = new StringBuffer();
            int i;
            while ((i = fstream.read())!= -1){
                sbuffer.append((char)i);
            }
            fstream.close();
            String details[] = sbuffer.toString().split("\n");

            final TextView name = (TextView) findViewById(R.id.name_text);
            final TextView age = (TextView) findViewById(R.id.age_text);
            final TextView height = (TextView) findViewById(R.id.height_text);
            final TextView weight = (TextView) findViewById(R.id.weight_text);
            final TextView snoring = (TextView) findViewById(R.id.snoring_text);
            final TextView apnea = (TextView) findViewById(R.id.apnea_text);
            String[] parts = details[0].split(",");

            name.setText(parts[0]);
            age.setText(parts[1]);
            weight.setText(parts[2]);
            height.setText(parts[3]);

            fstream = openFileInput("snoring_apnea");
            sbuffer = new StringBuffer();
            i = 0;
            while ((i = fstream.read())!= -1){
                sbuffer.append((char)i);
            }
            fstream.close();
            String snoringAndApnea[] = sbuffer.toString().split("\n");
            String[] snoringAndApneaSplitted = snoringAndApnea[0].split(",");

            snoring.setText(parts[0]);
            apnea.setText(parts[1]);

            fstream = openFileInput("video_uri");
            sbuffer = new StringBuffer();
            while ((i = fstream.read())!= -1){
                sbuffer.append((char)i);
            }
            fstream.close();
            String video_uri[] = sbuffer.toString().split("\n");

            final VideoView videoView = (VideoView)findViewById(R.id.videoView);

            MediaController mediaController = new MediaController(this);
            mediaController.setAnchorView(videoView);
            // Set video link (mp4 format )
            videoView.setMediaController(mediaController);
            videoView.setVideoURI(Uri.parse(video_uri[0]));
            videoView.start();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
