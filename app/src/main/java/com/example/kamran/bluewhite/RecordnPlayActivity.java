package com.example.kamran.bluewhite;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ShortBuffer;

public class RecordnPlayActivity extends AppCompatActivity {
    private Button play, stop, record;
    private MediaRecorder myAudioRecorder;
    private String outputFile;

    private DetectorThread detectorThread;
    private RecorderThread recorderThread;

    private Handler showhandler = null;
    private Handler alarmhandler = null;

    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.audio_recordv2);
        play = (Button) findViewById(R.id.play);
        stop = (Button) findViewById(R.id.stop);
        record = (Button) findViewById(R.id.record);
        stop.setEnabled(false);
        play.setEnabled(false);
        // ...


        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording.3gp";

        myAudioRecorder = new MediaRecorder();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        myAudioRecorder.setOutputFile(outputFile);

        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // myAudioRecorder.prepare();
               // myAudioRecorder.start();
                recorderThread = new RecorderThread(showhandler, getApplicationContext(), myAudioRecorder);
                recorderThread.start();
                detectorThread = new DetectorThread(recorderThread, alarmhandler);
                detectorThread.start();
                // make something ...
                record.setEnabled(false);
                stop.setEnabled(true);
                Toast.makeText(getApplicationContext(), "Recording started", Toast.LENGTH_LONG).show();
            }
        });


        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recorderThread.stopRecording();
                detectorThread.stopDetection();
                //recorderThread.stop();
                //detectorThread.stop();
               // myAudioRecorder.stop();
               // myAudioRecorder.release();
                //myAudioRecorder = null;
                record.setEnabled(true);
                stop.setEnabled(false);
                play.setEnabled(true);
                Toast.makeText(getApplicationContext(), "Audio Recorder stopped", Toast.LENGTH_LONG).show();
            }
        });


        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaPlayer mediaPlayer = new MediaPlayer();
                try {
                    //mediaPlayer.setDataSource(outputFile);
                    //mediaPlayer.prepare();
                    //mediaPlayer.start();

                    recorderThread.playRecord();
                    Toast.makeText(getApplicationContext(), "Playing Audio", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    // make something
                }
            }
        });

        showhandler = new Handler() {
            public void handleMessage(Message msg) {
                //txtAbs.setText(msg.obj.toString());
            }
        };

        alarmhandler = new Handler() {
            public void handleMessage(Message msg) {
                FileOutputStream fstream = null;
                try {
                    fstream = openFileOutput("snoring_apnea", Context.MODE_PRIVATE);
                    String user_data =(new StringBuilder()).append(msg.arg1).append(",").append(msg.arg2).toString();
                    fstream.write(user_data.getBytes());
                    fstream.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

    }

    ShortBuffer mSamples; // the samples to play
    int mNumSamples; // number of samples to play
}