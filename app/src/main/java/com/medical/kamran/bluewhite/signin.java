package com.medical.kamran.bluewhite;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class signin extends AppCompatActivity {

    TextView start_register;
    EditText name;
    EditText age;
    EditText weight;
    EditText height;
    FileOutputStream fstream;
    AppContextId appContextId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_signin);
        start_register = (TextView) findViewById(R.id.create_register);
        final Context context = getApplicationContext();

        start_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current_user_id = 1;
                if(fileExists(context,"current_id"))
                {
                    FileInputStream user_id_stream = null;
                    StringBuffer user_id_buffer = null;
                    try {
                        user_id_stream = openFileInput("current_id");
                        user_id_buffer = new StringBuffer();
                        int i = 0;
                        while ((i = user_id_stream.read())!= -1){
                            user_id_buffer.append((char)i);
                        }
                        user_id_stream.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    String user_id_list[] = user_id_buffer.toString().split("\n");
                    String user_id = user_id_list[0];

                    int user_id_number = Integer.parseInt(user_id);
                    current_user_id = user_id_number + 1;

                }

                appContextId = new AppContextId(current_user_id);

                name   = (EditText)findViewById(R.id.name);
                age   = (EditText)findViewById(R.id.age);
                weight   = (EditText)findViewById(R.id.weight);
                height   = (EditText)findViewById(R.id.height);


                if(name.getText().length() == 0 || age.getText().length() == 0 || weight.getText().length() == 0 || height.getText().length() == 0)
                {
                    Toast.makeText(getApplicationContext(), "Dados de registo em falta", Toast.LENGTH_LONG).show();
                }else {
                    try {
                        fstream = openFileOutput("user_data_" + current_user_id, Context.MODE_PRIVATE);
                        String user_data = (new StringBuilder()).append(name.getText()).append(",").append(age.getText()).append(",").append(weight.getText()).append(",").append(height.getText()).append(",").append(current_user_id).toString();
                        fstream.write(user_data.getBytes());
                        fstream.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try {
                        FileOutputStream fstream_user_id = openFileOutput("current_id", Context.MODE_PRIVATE);
                        String user_id = (new StringBuilder()).append(current_user_id).toString();
                        fstream_user_id.write(user_id.getBytes());
                        fstream_user_id.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Intent it = new Intent(signin.this, signup.class);
                    it.putExtra("user_id", String.valueOf(current_user_id));
                    startActivity(it);
                    finish();
                }
            }
        });
    }

    public boolean fileExists(Context context, String filename) {
        File file = context.getFileStreamPath(filename);
        if(file == null || !file.exists()) {
            return false;
        }
        return true;
    }
};
