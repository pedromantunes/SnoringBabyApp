package com.example.kamran.bluewhite;

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

                    playRecord();
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
                mToast = Toast.makeText(getApplicationContext(),
                        "Is Snoring", Toast.LENGTH_LONG);
                mToast.show();
                //txtAbs.setText(msg.obj.toString());
            }
        };

    }

    ShortBuffer mSamples; // the samples to play
    int mNumSamples; // number of samples to play

    void playRecord(){

        File file = new File(Environment.getExternalStorageDirectory(), "test.pcm");

        int shortSizeInBytes = Short.SIZE/Byte.SIZE;

        int bufferSizeInBytes = (int)(file.length()/shortSizeInBytes);
        short[] audioData = new short[bufferSizeInBytes];

        try {
            InputStream inputStream = new FileInputStream(file);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            DataInputStream dataInputStream = new DataInputStream(bufferedInputStream);

            int i = 0;
            while(dataInputStream.available() > 0){
                audioData[i] = dataInputStream.readShort();
                i++;
            }

            dataInputStream.close();

            //int selectedPos = spFrequency.getSelectedItemPosition();
            //int sampleFreq = freqset[selectedPos];

        /*    final String promptPlayRecord =
                    "PlayRecord()\n"
                            + file.getAbsolutePath() + "\n"
                            + (String)spFrequency.getSelectedItem();*/

        /*    Toast.makeText(AndroidAudioRecordActivity.this,
                    promptPlayRecord,
                    Toast.LENGTH_LONG).show();*/

            AudioTrack audioTrack = new AudioTrack(
                    AudioManager.STREAM_MUSIC,
                    44100,
                    AudioFormat.CHANNEL_CONFIGURATION_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    bufferSizeInBytes,
                    AudioTrack.MODE_STREAM);

            audioTrack.play();
            audioTrack.write(audioData, 0, bufferSizeInBytes);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}