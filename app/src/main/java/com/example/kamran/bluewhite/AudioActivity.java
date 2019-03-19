package com.example.kamran.bluewhite;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.audio_record);
        sfv = (SurfaceView) this.findViewById(R.id.SurfaceView);

        View v = (View) findViewById(R.id.SurfaceView);

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
        // alarmThread = new AlarmThread(pendingIntent, am);
        recorderThread = new RecorderThread(showhandler);
        recorderThread.start();
        detectorThread = new DetectorThread(recorderThread, alarmhandler);
        detectorThread.start();
        drawThread = new DrawThread(sfv.getHeight() / 2, sfv, mPaint);
        drawThread.start();
        // clsOscilloscope.baseLine = sfv.getHeight() / 2;
        // clsOscilloscope.Start(audioRecord, recBufSize, sfv, mPaint);

        mToast = Toast.makeText(getApplicationContext(),
                "Recording & Detecting start", Toast.LENGTH_LONG);
        mToast.show();
        //show ImageView width and height

        //fsfv.getViewTreeObserver().addOnGlobalLayoutListener(new MyGlobalListenerClass());

    }


    //Declare the layout listener
    class MyGlobalListenerClass implements ViewTreeObserver.OnGlobalLayoutListener {
        @Override
        public void onGlobalLayout() {

        }
    }
}
