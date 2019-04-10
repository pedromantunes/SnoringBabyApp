package com.example.kamran.bluewhite;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static android.provider.UserDictionary.Words.FREQUENCY;

public class AudioActivity extends AppCompatActivity {


    public static final int DETECT_NONE = 0;
    public static final int DETECT_SNORE = 1;
    public static int selectedDetection = DETECT_NONE;

    private DetectorThread detectorThread;
    private RecorderThread recorderThread;
    private DrawThread drawThread;

    public static int snoreValue = 0;

    private View mainView;
    private Button mSleepRecordBtn, mAlarmBtn, mRecordBtn, mTestBtn;
    private TextView txtAbs;

    private Toast mToast;

    private Handler rhandler = new Handler();
    private Handler showhandler = null;
    private Handler alarmhandler = null;

    private Intent intent;
    private PendingIntent pendingIntent;
    private AlarmManager am;

    private SurfaceView sfv;
    private Paint mPaint;

    private TextView counter;
    private Button play;
    private  Button stop;

    FileInputStream fstream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.audio_record);
        //sfv = (SurfaceView) this.findViewById(R.id.SurfaceView);

        //View v = (View) findViewById(R.id.SurfaceView);

        play = (Button)findViewById(R.id.play_audio);
        stop = (Button)findViewById(R.id.stop_audio);
        counter = (TextView) findViewById(R.id.counter) ;

        mPaint = new Paint();
        mPaint.setColor(Color.GREEN);

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

        selectedDetection = DETECT_SNORE;
        drawThread = new DrawThread(counter);
        drawThread.start();
        recorderThread = new RecorderThread(showhandler, getApplicationContext(), null);
        recorderThread.start();
        detectorThread = new DetectorThread(recorderThread, alarmhandler);
        detectorThread.start();

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    play(v);
                }
                catch (IOException e){
                    Log.i("IOException", "Error in play");
                }
            }
        });


        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    stop(v);
                }
                catch (IOException e){
                    Log.i("IOException", "Error in play");
                }
            }
        });

        // clsOscilloscope.baseLine = sfv.getHeight() / 2;
        // clsOscilloscope.Start(audioRecord, recBufSize, sfv, mPaint);

        mToast = Toast.makeText(getApplicationContext(),
                "Recording & Detecting start", Toast.LENGTH_LONG);
        mToast.show();
        //show ImageView width and height

        //fsfv.getViewTreeObserver().addOnGlobalLayoutListener(new MyGlobalListenerClass());

    }

    public void stop(View view) throws IllegalArgumentException, SecurityException, IllegalStateException, IOException {
        recorderThread.stopRecording();
        //detectorThread.stopDetection();
        //drawThread.stop();
    }

    public void play(View view) throws IllegalArgumentException, SecurityException, IllegalStateException, IOException {

        String fileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/audio_file.txt";
        File file = new File(fileName);

        byte[] audioData = null;

        try {
            InputStream inputStream = new FileInputStream(fileName);

            int minBufferSize = AudioTrack.getMinBufferSize(44100, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
            audioData = new byte[minBufferSize];

            //AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, FREQUENCY, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, minBufferSize, AudioTrack.MODE_STREAM);
            AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,44100,AudioFormat.CHANNEL_OUT_MONO,AudioFormat.ENCODING_PCM_16BIT,minBufferSize,AudioTrack.MODE_STREAM);
            int i=0;

            while((i = inputStream.read(audioData)) != -1) {
                audioTrack.write(audioData,0,i);

                System.out.println("Audio bytes : " + i);
            }

        } catch(FileNotFoundException fe) {
            fe.printStackTrace();
        } catch(IOException io) {
            io.printStackTrace();
        }

    }


    //Declare the layout listener
    class MyGlobalListenerClass implements ViewTreeObserver.OnGlobalLayoutListener {
        @Override
        public void onGlobalLayout() {

        }
    }
}
