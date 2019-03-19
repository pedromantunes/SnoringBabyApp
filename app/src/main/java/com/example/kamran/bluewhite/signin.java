package com.example.kamran.bluewhite;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import customfonts.MyTextView;

public class signin extends AppCompatActivity {

    ImageView sback;
    MyTextView start_register;
    EditText name;
    EditText age;
    EditText weight;
    EditText height;
    FileOutputStream fstream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_signin);
        sback = (ImageView) findViewById(R.id.sinb);
        start_register = (MyTextView) findViewById(R.id.create_register);
        sback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(signin.this, main.class);
                startActivity(it);
            }
        });

        start_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name   = (EditText)findViewById(R.id.name);
                age   = (EditText)findViewById(R.id.age);
                weight   = (EditText)findViewById(R.id.weight);
                height   = (EditText)findViewById(R.id.height);

                try {
                    fstream = openFileOutput("user_details", Context.MODE_PRIVATE);
                    fstream.write(name.getText().toString().getBytes());
                    fstream.write(age.getText().toString().getBytes());
                    fstream.write(weight.getText().toString().getBytes());
                    fstream.write(height.getText().toString().getBytes());
                    fstream.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                age.getText().toString();
                weight.getText().toString();
                height.getText().toString();

                Intent it = new Intent(signin.this, signup.class);
                it.putExtra("state", "initial");
                startActivity(it);
            }
        });
    }
};
