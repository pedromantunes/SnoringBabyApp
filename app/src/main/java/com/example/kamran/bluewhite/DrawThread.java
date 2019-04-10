package com.example.kamran.bluewhite;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.CountDownTimer;
import android.view.SurfaceView;
import android.widget.TextView;

public class DrawThread extends Thread {
    private int oldX = 0;
    private int oldY = 0;
    private SurfaceView sfv;
    private int X_index = 0;
    private Paint mPaint;
    private int baseline;
    public boolean draw_isRecording = true;
    private TextView counter;

    public DrawThread(TextView counter ) {
        this.counter = counter;
        //this.sfv = sfv;
        //this.mPaint = mPaint;

    }

    public void run() {
        /*while (true) {
            if (draw_isRecording) {
                // System.out.println("draw");
                ArrayList<short[]> buf = new ArrayList<short[]>();
                synchronized (AlarmStaticVariables.inBuf) {
                    if (AlarmStaticVariables.inBuf.size() == 0)
                        continue;
                    buf = (ArrayList<short[]>) AlarmStaticVariables.inBuf
                            .clone();// save
                    AlarmStaticVariables.inBuf.clear();// clear
                }
                for (int i = 0; i < buf.size(); i++) {
                    short[] tmpBuf = buf.get(i);
                    SimpleDraw(X_index, tmpBuf, AlarmStaticVariables.rateY,
                            baseline);// draw buffer
                    // data
                    X_index = X_index + tmpBuf.length;
                    if (X_index > sfv.getWidth()) {
                        X_index = 0;
                    }
                }
            }
        }*/

        /*new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {

                String seconds = Long.toString(millisUntilFinished /1000);
                counter.setText(seconds);
            }

            public void onFinish() {
                counter.setText("done!");
            }
        }.start();*/
    }

    void SimpleDraw(int start, short[] buffer, int rate, int baseLine) {
        if (start == 0)
            oldX = 0;
        Canvas canvas = sfv.getHolder().lockCanvas(
                new Rect(start, 0, start + buffer.length, sfv.getHeight()));
        if (canvas == null)
            return;
        canvas.drawColor(Color.BLACK);// clear background
        int y;
        for (int i = 0; i < buffer.length; i++) {// draw
            int x = i + start;
            y = buffer[i] / rate + baseLine;// shrink
            canvas.drawLine(oldX, oldY, x, y, mPaint);
            oldX = x;
            oldY = y;
        }
        sfv.getHolder().unlockCanvasAndPost(canvas);// unlock canvas
    }
}

