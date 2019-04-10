package com.example.kamran.bluewhite;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import com.example.kamran.bluewhite.AlarmStaticVariables;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class RecorderThread extends Thread {
    private AudioRecord audioRecord;
    private boolean isRecording;
    private int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    private int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
    private int sampleRate = 44100;
    private int frameByteSize = 1024; // for 1024 fft size (16bit sample size)
    private MediaRecorder myAudioRecorder;
    FileOutputStream fstream;
    int recBufSize;
    OutputStream os = null;

    DataOutputStream dataOutputStream;

    byte[] buffer;
    byte[] totalBuf;
    int cnt;

    // showVariableThread showVariable;
    Handler showhandler;

    public RecorderThread(Handler showhandler, Context context, MediaRecorder myAudioRecorder) {
        this.showhandler = showhandler;
        recBufSize = AudioRecord.getMinBufferSize(sampleRate,
                channelConfiguration, audioEncoding); // need to be larger than
        // size of a frame
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                sampleRate, channelConfiguration, audioEncoding, recBufSize);

        buffer = new byte[frameByteSize];
        totalBuf = new byte[AlarmStaticVariables.sampleSize * 2];
        cnt = 0;

        /*String output = Environment.getExternalStorageDirectory().getAbsolutePath() + "/myrecording.3gp";
        myAudioRecorder = new MediaRecorder();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        myAudioRecorder.setOutputFile(output);

        try {
            fstream = context.openFileOutput("audio_file", Context.MODE_PRIVATE);
            fstream.write(output.getBytes());
            fstream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    public AudioRecord getAudioRecord() {
        return audioRecord;
    }

    public boolean isRecording() {
        return this.isAlive() && isRecording;
    }

    public void startRecording() {
        try {
            //myAudioRecorder.prepare();
            //myAudioRecorder.start();
            //audioRecord.startRecording();

            isRecording = true;

            File file = new File(Environment.getExternalStorageDirectory(), "test.pcm");

            file.createNewFile();

            OutputStream outputStream = new FileOutputStream(file);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
            dataOutputStream = new DataOutputStream(bufferedOutputStream);

            audioRecord.startRecording();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopRecording() {
        try {
            audioRecord.getAudioSource();
            audioRecord.stop();
            dataOutputStream.close();

            //myAudioRecorder.stop();
            //myAudioRecorder.release();
            //myAudioRecorder = null;

            isRecording = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public byte[] getFrameBytes() throws IOException {

        //Create a timestemp to know how many seconds were recorded

        int bufferReadResult = audioRecord.read(buffer, 0, frameByteSize);

        /*
        for(int i = 0; i < bufferReadResult; i++){
            dataOutputStream.writeShort(buffer[i]);
        }
        */

        // analyze sound
        int totalAbsValue = 0;
        short sample = 0;
        short[] tmp = new short[frameByteSize];
        // float averageAbsValue = 0.0f;
        AlarmStaticVariables.absValue = 0.0f;

        for (int i = 0; i < frameByteSize; i += 2) {
            sample = (short) ((buffer[i]) | buffer[i + 1] << 8);
            tmp[i] = sample;
            totalAbsValue += Math.abs(sample);
        }
        AlarmStaticVariables.absValue = totalAbsValue / frameByteSize / 2;

        Message msg = new Message();
        msg.obj = AlarmStaticVariables.absValue;
        showhandler.sendMessage(msg);

        for (int i = 0; i < buffer.length; i++) {
            totalBuf[cnt++] = buffer[i];
        }

        // ----------save into buf----------------------
        short[] tmpBuf = new short[bufferReadResult
                / AlarmStaticVariables.rateX];
        for (int i = 0, ii = 0; i < tmpBuf.length; i++, ii = i
                * AlarmStaticVariables.rateX) {
            tmpBuf[i] = tmp[ii];
        }
        synchronized (AlarmStaticVariables.inBuf) {//
            AlarmStaticVariables.inBuf.add(tmpBuf);// add data
        }
        // ----------save into buf----------------------

        // System.out.println(cnt + " vs " + AlarmStaticVariables.sampleSize);
        if (cnt > AlarmStaticVariables.sampleSize) {
            cnt = 0;
            return totalBuf;
        } else
            return null;
        // return buffer;
    }

    public void run() {
        startRecording();
    }

}
