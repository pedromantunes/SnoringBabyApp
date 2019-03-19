package com.example.kamran.bluewhite;

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

            // initBuffer();

            Thread thisThread = Thread.currentThread();
            while (_thread == thisThread) {

                buffer = recorder.getFrameBytes();

               // snoringApi.getFrequency(buffer);

//                while(false)
//                {
//                    // detect sound
//
//
//                    if(tempBuffer != null) {
//
//                        if (time == 0) {
//                            buffer = tempBuffer;
//                            buffer_length = tempBuffer.length;
//                        } else {
//                            buffer_length = tempBuffer.length + buffer_length;
//                            for (int i = 0; i < tempBuffer.length; i++) {
//                                buffer[buffer_length + i + 1] = tempBuffer[i];
//                            }
//                        }
//
//                        Thread.sleep(1000);
//                        time--;
//                    }
//
//                }

                // audio analyst
                if (buffer != null) {
                    System.out.println("How many bytes? " + buffer.length);
                    AlarmStaticVariables.snoringCount = snoringApi
                            .isSnoring(buffer);
                    System.out.println("count="
                            + AlarmStaticVariables.snoringCount);
                    if (AlarmStaticVariables.snoringCount >= AlarmStaticVariables.sampleCount) {
                        AlarmStaticVariables.snoringCount = 0;
                        if (!AlarmStaticVariables.inProcess) {
                            //AlarmStaticVariables.inProcess = true;
                            int level = 1;
                            Message msg = new Message();
                            msg.arg1 = level;
                            alarmhandler.sendMessage(msg);

                        }
                    }

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
