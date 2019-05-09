package com.medical.kamran.bluewhite;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ShortBuffer;

public class RecordnPlayActivity extends AppCompatActivity {
    private Button stop, record;
    public TextView counter;
    private MediaRecorder myAudioRecorder;
    private String outputFile;

    private DetectorThread detectorThread;
    private RecorderThread recorderThread;
    private CountingThread counterThread;

    private Handler showhandler = null;
    private Handler alarmhandler = null;
    String currentUserId;

    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.audio_recordv2);
        stop = (Button) findViewById(R.id.stop);
        record = (Button) findViewById(R.id.record);
        counter = (TextView) findViewById(R.id.time_counter);

        Bundle bundle = getIntent().getExtras();
        currentUserId = bundle.getString("user_id");

        stop.setEnabled(false);

        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recorderThread = new RecorderThread(showhandler, getApplicationContext(), currentUserId);
                recorderThread.start();
                detectorThread = new DetectorThread(recorderThread, alarmhandler);
                detectorThread.start();
                counterThread = new CountingThread(recorderThread, detectorThread, record, stop, getApplicationContext(), counter);
                counterThread.start();
                // make something ...
                record.setEnabled(false);
                stop.setEnabled(true);
                Toast.makeText(getApplicationContext(), "Gravação de audio iniciada", Toast.LENGTH_LONG).show();
            }
        });


        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recorderThread.stopRecording();
                detectorThread.stopDetection();
                counterThread.stopCounter();
                record.setEnabled(true);
                stop.setEnabled(false);
                counter.setText("Gravação terminada");
                Toast.makeText(getApplicationContext(), "Gravação de audio terminada", Toast.LENGTH_LONG).show();
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
                fstream = openFileOutput("snoring_apnea_" + currentUserId, Context.MODE_PRIVATE);
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