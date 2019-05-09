package com.medical.kamran.bluewhite;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class main extends AppCompatActivity {
    TextView sin;
    LinearLayout report;
    LinearLayout process_reading;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        report = (LinearLayout)findViewById(R.id.report);
        process_reading = (LinearLayout)findViewById(R.id.process_reading);

        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(main.this,ReportListActivity.class);
                startActivity(it);

            }
        });
        process_reading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(main.this,signin.class);
                startActivity(it);
            }
        });
    }


    protected void onDestroy() {
        super.onDestroy();
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
