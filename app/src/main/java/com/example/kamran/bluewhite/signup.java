package com.example.kamran.bluewhite;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import agency.tango.materialintroscreen.MaterialIntroActivity;
import agency.tango.materialintroscreen.MessageButtonBehaviour;
import agency.tango.materialintroscreen.SlideFragmentBuilder;

public class signup extends MaterialIntroActivity implements ActivityCompat.OnRequestPermissionsResultCallback
{
    ImageView sback;
    private VideoView videoView;
    private static final int MEDIA_RECORDER_REQUEST = 0;
    private final String[] requiredPermissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();
        String value = bundle.getString("state");

        if (value.equalsIgnoreCase("initial")) {
            addSlide(new SlideFragmentBuilder()
                            .backgroundColor(R.color.dark_grey)
                            .buttonsColor(R.color.third_slide_buttons)
                            .image(R.drawable.snoring)
                            .title("Vai começar a filmar o seu filho. Assegure-se que se encontra " +
                                    "num ambiente sem ruído e com alguma luminosidade. Siga as instruções abaixo indicadas:\n \n")
                            .description("1 - A criança deve estar deitada de costas \n" +
                                    "2 - Deve remover o máximo de roupa possível acima da cintura \n" +
                                    "3 - Certifique-se que a câmara do seu telemóvel abrange desde a cabeça até à cintura durante todo o tempo de gravação \n")
                            .build(),
                    new MessageButtonBehaviour(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (areCameraPermissionGranted()) {
                                Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                                takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                                takeVideoIntent.putExtra("state", "video");
                                if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
                                    startActivityForResult(takeVideoIntent, 1);
                                }
                            } else {
                                requestCameraPermissions();
                            }
                        }
                    }, "Iniciar video"));

            if (value.equalsIgnoreCase("initial")) {

                addSlide(new SlideFragmentBuilder()
                        .backgroundColor(R.color.dark_grey)
                        .buttonsColor(R.color.fourth_slide_buttons)
                        .image(R.drawable.snoring)
                        .title("Vai começar a gravar o som respiratório do seu filho. \" +\n" +
                                "                                \"Assegure-se que está num ambiente com pouco ruído. Coloque o microfone do telemóvel próximo da face da criança.")
                        .build(), new MessageButtonBehaviour(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent it = new Intent(signup.this, RecordnPlayActivity.class);
                        it.putExtra("state", "audio");
                        startActivity(it);
                    }
                }, "Gravar audio"));
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Uri videoUri = intent.getData();

            FileOutputStream fstream = null;
            try {
                fstream = openFileOutput("video_uri", Context.MODE_PRIVATE);
                String user_data =(new StringBuilder()).append(videoUri).toString();
                fstream.write(user_data.getBytes());
                fstream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private boolean areCameraPermissionGranted() {
        for (String permission : requiredPermissions){
            if (!(ActivityCompat.checkSelfPermission(this, permission) ==
                    PackageManager.PERMISSION_GRANTED)){
                return false;
            }
        }
        return true;
    }

    private void requestCameraPermissions(){
        ActivityCompat.requestPermissions(
                this,
                requiredPermissions,
                MEDIA_RECORDER_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (MEDIA_RECORDER_REQUEST != requestCode) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        boolean areAllPermissionsGranted = true;
        for (int result : grantResults){
            if (result != PackageManager.PERMISSION_GRANTED){
                areAllPermissionsGranted = false;
                break;
            }
        }

        if (areAllPermissionsGranted){
            // startCapture();
        } else {
            // User denied one or more of the permissions, without these we cannot record
            // Show a toast to inform the user.
            Toast.makeText(getApplicationContext(),
                    "Need camera permitions",
                    Toast.LENGTH_SHORT)
                    .show();
        }
    }
}
