package com.medical.kamran.bluewhite;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ReportActivity extends AppCompatActivity {

    FileInputStream fstream;
    PlayAudioThread playAudioThread;
    private TextView play, play_video;
    private Handler playAudioHandler = null;
    String[] parts;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String index_id = getIntent().getStringExtra("index_id");
        setContentView(R.layout.activity_report);

        try {
            fstream = openFileInput("user_data_" + index_id);
            StringBuffer sbuffer = new StringBuffer();
            int i;
            while ((i = fstream.read())!= -1){
                sbuffer.append((char)i);
            }
            fstream.close();
            String details[] = sbuffer.toString().split("\n");

            final TextView name = (TextView) findViewById(R.id.name_text);
            final TextView age = (TextView) findViewById(R.id.age_text);
            final TextView height = (TextView) findViewById(R.id.height_text);
            final TextView weight = (TextView) findViewById(R.id.weight_text);
            final TextView snoring = (TextView) findViewById(R.id.snoring_text);
            final TextView apnea = (TextView) findViewById(R.id.apnea_text);
            parts = details[0].split(",");

            name.setText(parts[0]);
            age.setText(parts[1]);
            weight.setText(parts[2]);
            height.setText(parts[3]);

            final int user_id_number = Integer.parseInt(parts[4]);

            FileInputStream snoring_stream = openFileInput("snoring_apnea_" + user_id_number);
            StringBuffer snoringBuffer = new StringBuffer();
            i = 0;
            while ((i = snoring_stream.read())!= -1){
                snoringBuffer.append((char)i);
            }
            fstream.close();
            String snoringAndApnea[] = snoringBuffer.toString().split("\n");
            String[] snoringAndApneaSplitted = snoringAndApnea[0].split(",");

            snoring.setText(snoringAndApneaSplitted[0]);
            apnea.setText(snoringAndApneaSplitted[1]);

            play = (TextView) findViewById(R.id.play);
            play_video = (TextView) findViewById(R.id.play_video);
            play.setEnabled(true);
            play_video.setEnabled(true);

            progress = new ProgressDialog(ReportActivity.this);

            play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MediaPlayer mediaPlayer = new MediaPlayer();
                    try {
                        progress.setMessage("A preparar clip de audio");
                        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                        progress.show();

                        playAudioThread = new PlayAudioThread(playAudioHandler, user_id_number, progress);

                        playAudioThread.start();

                        Toast.makeText(getApplicationContext(), "A reproduzir audio", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Ocorreu um erro a reproduzir o clip de audio", Toast.LENGTH_LONG).show();
                    }
                }
            });

            /*
            stop_audio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        playAudioThread.stopAudio();
                        playAudioThread.interrupt();

                        Toast.makeText(getApplicationContext(), "Reprodução de audio parada", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Ocorreu um erro a parar reprodução de audio", Toast.LENGTH_LONG).show();
                    }
                }
            });
            */




            play_video.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(ReportActivity.this, VideoFullActivity.class);
                    it.putExtra("user_id", String.valueOf(user_id_number));
                    startActivity(it);
                }
            });

        } catch (FileNotFoundException e) {
            Intent it = new Intent(ReportActivity.this, NoReportActivity.class);
            it.putExtra("state", "initial");
            startActivity(it);
            finish();
        } catch (IOException e) {
            Intent it = new Intent(ReportActivity.this, NoReportActivity.class);
            it.putExtra("state", "initial");
            startActivity(it);
            finish();
        }


        playAudioHandler = new Handler() {
            public void handleMessage(Message msg) {
                progress.dismiss();
            }
        };
    }
}
