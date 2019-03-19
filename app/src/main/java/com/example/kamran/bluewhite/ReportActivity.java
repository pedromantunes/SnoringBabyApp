package com.example.kamran.bluewhite;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ReportActivity extends AppCompatActivity {

    FileInputStream fstream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_report);

        try {
            fstream = openFileInput("user_details");
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

            name.setText(details[0]);
            name.setText(details[1]);
            name.setText(details[2]);
            name.setText(details[3]);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
