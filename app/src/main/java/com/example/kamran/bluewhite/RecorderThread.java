package com.example.kamran.bluewhite;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;

import com.example.kamran.bluewhite.AlarmStaticVariables;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.Buffer;
import java.nio.ShortBuffer;

public class RecorderThread extends Thread {
    private AudioRecord audioRecord;
    private boolean isRecording;
    private int channelConfiguration = AudioFormat.CHANNEL_IN_MONO;
    private int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
    private int sampleRate = 44100;
    private int frameByteSize = 1024; // for 1024 fft size (16bit sample size)
    FileOutputStream fstream;
    int recBufSize;
    OutputStream os = null;

    DataOutputStream dataOutputStream;

    InputStream inputStream;
    ByteArrayOutputStream byteArrayOutputStream;

    short[] buffer;
    short[] totalBuf;
    int cnt;

    // showVariableThread showVariable;
    Handler showhandler;

    public RecorderThread(){

    }

    public RecorderThread(Handler showhandler, Context context) {
        this.showhandler = showhandler;
        recBufSize = AudioRecord.getMinBufferSize(sampleRate,
                channelConfiguration, audioEncoding); // need to be larger than

        buffer = new short[recBufSize/2];
        totalBuf = new short[AlarmStaticVariables.sampleSize * 2];
        cnt = 0;

        // size of a frame
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                sampleRate, channelConfiguration, audioEncoding, recBufSize);

    }

    public AudioRecord getAudioRecord() {
        return audioRecord;
    }

    public boolean isRecording() {
        return this.isAlive() && isRecording;
    }


    public void startRecording() {
        try {
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

    private byte[] short2byte(short[] sData) {
        int shortArrsize = sData.length;
        byte[] bytes = new byte[shortArrsize * 2];

        for (int i = 0; i < shortArrsize; i++) {
            bytes[i * 2] = (byte) (sData[i] & 0x00FF);
            bytes[(i * 2) + 1] = (byte) (sData[i] >> 8);
            sData[i] = 0;
        }
        return bytes;

    }

    public byte[] getFrameBytes() throws IOException {

        //Create a timestemp to know how many seconds were recorded

        int bufferReadResult = audioRecord.read(buffer, 0, frameByteSize);

        for(int i = 0; i < bufferReadResult && i < frameByteSize; i++) {
            dataOutputStream.writeShort(buffer[i]);
        }

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
            return short2byte(totalBuf);
        } else
            return null;
        // return buffer;
    }

    private static final int audioStreamBufferSize = 1024 * 20;
    static byte[] audioStreamBuffer = new byte[audioStreamBufferSize];
    private static int audioStreamBufferIndex = 0;


    private static void Log(String log) {
        System.out.println(log);
    }

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

            AudioTrack audioTrack = new AudioTrack(
                    AudioManager.STREAM_MUSIC,
                    44100,
                    AudioFormat.CHANNEL_OUT_MONO,
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

    public void run() {
        startRecording();
    }

}
