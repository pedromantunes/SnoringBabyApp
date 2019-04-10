package com.example.kamran.bluewhite;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import customfonts.MyTextView;

public class signin extends AppCompatActivity {

    TextView start_register;
    EditText name;
    EditText age;
    EditText weight;
    EditText height;
    FileOutputStream fstream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_signin);
      //  sback = (ImageView) findViewById(R.id.sinb);
        start_register = (TextView) findViewById(R.id.create_register);
//        sback.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent it = new Intent(signin.this, main.class);
//                startActivity(it);
//            }
//        });

        start_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name   = (EditText)findViewById(R.id.name);
                age   = (EditText)findViewById(R.id.age);
                weight   = (EditText)findViewById(R.id.weight);
                height   = (EditText)findViewById(R.id.height);

                try {
                    fstream = openFileOutput("user_details", Context.MODE_PRIVATE);
                    String user_data =(new StringBuilder()).append(name.getText()).append(",").append(age.getText()).append(",").append(weight.getText()).append(",").append(height.getText()).toString();
                    fstream.write(user_data.getBytes());
                    fstream.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Intent it = new Intent(signin.this, signup.class);
                it.putExtra("state", "initial");
                startActivity(it);
            }
        });
    }
};
