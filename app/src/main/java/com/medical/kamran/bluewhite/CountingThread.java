package com.medical.kamran.bluewhite;

import android.content.Context;
import android.icu.text.AlphabeticIndex;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class CountingThread extends Thread {

    public Button record, stop;
    public TextView counter;
    public RecorderThread recorderThread;
    public DetectorThread detectorThread;
    public Context context;
    public CountDownTimer countDownTimer;

    public CountingThread(RecorderThread recorderThread, DetectorThread detectorThread, Button record, Button stop, Context context, TextView counter){
        this.recorderThread = recorderThread;
        this.detectorThread = detectorThread;
        this.record = record;
        this.stop = stop;
        this.context = context;
        this.counter = counter;
    }

    public void startThread(){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                countDownTimer = new CountDownTimer(180000, 1000) {

                    public void onTick(long millisUntilFinished) {
                        CountingThread.this.counter.setText(millisUntilFinished / 1000 + "s");
                    }

                    public void onFinish() {
                        CountingThread.this.recorderThread.stopRecording();
                        CountingThread.this.detectorThread.stopDetection();
                        CountingThread.this.record.setEnabled(true);
                        CountingThread.this.stop.setEnabled(false);
                        CountingThread.this.counter.setText("Gravação terminada");
                    }
                }.start();
            }
        });
    }

    public void stopCounter(){
        countDownTimer.cancel();
    }


    public void run()
    {
        startThread();
    }

}
