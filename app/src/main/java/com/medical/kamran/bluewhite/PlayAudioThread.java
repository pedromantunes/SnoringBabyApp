package com.medical.kamran.bluewhite;

import android.app.ProgressDialog;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import org.apache.commons.io.filefilter.WildcardFileFilter;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class PlayAudioThread extends Thread {
    short[] audioData;
    int bufferSizeInBytes;

    Handler progressRecordingHandler;
    int userId;
    ProgressDialog progress;
    AudioTrack audioTrack;
    ArrayList<short[]> audioTrackList;
    boolean threadStopped = false;

    public PlayAudioThread(Handler progressRecordingHandler, int userId, ProgressDialog progress) {
        this.progressRecordingHandler = progressRecordingHandler;
        this.userId = userId;
        this.progress = progress;
    }

    void playRecord(){
        threadStopped = false;
        FileFilter fileFilter = new WildcardFileFilter("*test" + this.userId + "_*");
        File[] files = Environment.getExternalStorageDirectory().listFiles(fileFilter);

       ArrayList<File> sortedFiles = new ArrayList<File>();

        for(int i = 0; i < files.length - 1; i++) {

            int fileNumberPosition = files[i].getName().length() - 5;

            String fileNumber =  files[i].getName().substring(fileNumberPosition, fileNumberPosition + 1);

            for(int j = i + 1; j < files.length; j++)
            {
                String nextFileNumber = files[j].getName().substring(fileNumberPosition, fileNumberPosition + 1);
                if(Integer.parseInt(nextFileNumber) < Integer.parseInt(fileNumber))
                {
                    File tempfile = files[i];
                    files[i] = files[j];
                    files[j] = tempfile;
                    fileNumber = files[i].getName().substring(fileNumberPosition, fileNumberPosition + 1);
                }
            }
        }

        for (int j = 0; j < files.length; j++) {

            if(!threadStopped) {
                FileInputStream fstream = null;

                //Don't forget to check for file null
                File file = new File(Environment.getExternalStorageDirectory(), files[j].getName());

                int shortSizeInBytes = Short.SIZE / Byte.SIZE;

                bufferSizeInBytes = (int) (file.length() / shortSizeInBytes);

                audioData = new short[bufferSizeInBytes];

                try {
                    InputStream inputStream = new FileInputStream(file);
                    BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                    DataInputStream dataInputStream = new DataInputStream(bufferedInputStream);

                    int i = 0;
                    while (dataInputStream.available() > 0) {
                        audioData[i] = dataInputStream.readShort();

                        i++;
                    }

                    dataInputStream.close();


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    System.out.println(e);
                }

                this.progress.dismiss();

                audioTrack = new AudioTrack(
                        AudioManager.STREAM_MUSIC,
                        44100,
                        AudioFormat.CHANNEL_OUT_MONO,
                        AudioFormat.ENCODING_PCM_16BIT,
                        bufferSizeInBytes,
                        AudioTrack.MODE_STREAM);

                audioTrack.play();
                audioTrack.write(audioData, 0, bufferSizeInBytes);
            }
        }
    }

    public void stopAudio(){
        audioTrack.stop();
        threadStopped = true;
    }

    public void run(){
        playRecord();
    }
}
