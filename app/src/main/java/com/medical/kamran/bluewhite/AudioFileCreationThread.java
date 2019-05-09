package com.medical.kamran.bluewhite;

import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class AudioFileCreationThread extends Thread {

    private DataOutputStream dataOutputStream;
    private String userId;
    private int counting = 0;

    public AudioFileCreationThread(DataOutputStream dataOutputStream, String userId)
    {
        this.dataOutputStream = dataOutputStream;
        this.userId = userId;
    }

    public void startThread() throws IOException {

        counting++;

        createAudioFile();

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                new CountDownTimer(30000, 1000) {

                    public void onTick(long millisUntilFinished) {
                    }

                    public void onFinish() {
                        try {
                            if(counting <= 6)
                            {
                                startThread();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });
    }

    public void createAudioFile() throws IOException {

        File file = new File(Environment.getExternalStorageDirectory(), "test" + this.userId + "_" + counting + ".pcm");

        file.createNewFile();

        OutputStream outputStream = new FileOutputStream(file);
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
        this.dataOutputStream = new DataOutputStream(bufferedOutputStream);

    }

    public DataOutputStream getDataOutputStream()
    {
        return this.dataOutputStream;
    }
}
