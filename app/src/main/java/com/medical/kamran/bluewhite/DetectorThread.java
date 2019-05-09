package com.medical.kamran.bluewhite;

import com.medical.kamran.bluewhite.AlarmStaticVariables;
import com.medical.kamran.bluewhite.RecorderThread;
import com.medical.kamran.bluewhite.SnoringApi;
import com.musicg.wave.WaveHeader;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DetectorThread extends Thread {

    private RecorderThread recorder;
    private volatile Thread _thread;
    private WaveHeader waveHeader;
    private SnoringApi snoringApi;
    // ----------------------------------
    Handler alarmhandler;

    private int snoringCount = 0;
    private int apneaCount = 0;

    // ----------------------------------

    public DetectorThread(RecorderThread recorder, Handler alarmhandler) {
        // TODO Auto-generated constructor stub
        this.recorder = recorder;
        AudioRecord audioRecord = recorder.getAudioRecord();

        this.alarmhandler = alarmhandler;

        int bitsPerSample = 0;
        if (audioRecord.getAudioFormat() == AudioFormat.ENCODING_PCM_16BIT) {
            bitsPerSample = 16;
        } else if (audioRecord.getAudioFormat() == AudioFormat.ENCODING_PCM_8BIT) {
            bitsPerSample = 8;
        }

        int channel = 0;
        // whistle detection only supports mono channel
        if (audioRecord.getChannelConfiguration() == AudioFormat.CHANNEL_CONFIGURATION_MONO) {
            channel = 1;
        }

        // TODO: added detection init
        waveHeader = new WaveHeader();
        waveHeader.setChannels(channel);
        waveHeader.setBitsPerSample(bitsPerSample);
        waveHeader.setSampleRate(audioRecord.getSampleRate());
        snoringApi = new SnoringApi(waveHeader);
    }

    public void stopDetection() {
        // TODO Auto-generated method stub

        Message msg = new Message();
        msg.arg1 = snoringCount;
        msg.arg2 = apneaCount;
        alarmhandler.sendMessage(msg);

        _thread = null;
    }

    public int getTotalSnoreDetected() {
        // TODO Auto-generated method stub
        return 0;
    }

    public void start() {
        _thread = new Thread(this);
        _thread.start();
    }

    public void run() {
        // @TODO: added run method content
        try {
            byte[] buffer = new byte[AlarmStaticVariables.sampleSize * 20];
            byte[] tempBuffer;
            int buffer_length = 0;
            int time = 1;

            snoringApi.resetValues();

            // initBuffer();

            Thread thisThread = Thread.currentThread();
            while (_thread == thisThread) {

                buffer = recorder.getFrameBytes();

                // audio analyst
                if (buffer != null) {
                    System.out.println("How many bytes? " + buffer.length);

                    snoringApi.calculateAmplitude(buffer);
                    snoringCount += snoringApi.isSnoring();
                    apneaCount += snoringApi.isInApnea();

                    // end snore detection

                } else {
                    // no sound detected
                    // MainActivity.snoreValue = 0;
                }
                // end audio analyst
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}