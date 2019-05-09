package com.medical.kamran.bluewhite;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.filefilter.WildcardFileFilter;

public class ReportListActivity extends AppCompatActivity {
    ListView listView ;

    List<Pair<Integer,String>> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String rootDir = this.getFilesDir().getAbsolutePath();
        File dir = new File(rootDir);
        FileFilter fileFilter = new WildcardFileFilter("*user_data*");
        File[] files = dir.listFiles(fileFilter);

        if(files.length == 0)
        {
            Intent intent = new Intent(getBaseContext(), NoReportActivity.class);
            startActivity(intent);
            finish();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_list_activity);

        // Get ListView object from xml
        listView = (ListView) findViewById(R.id.list);

        userList = new ArrayList<Pair<Integer,String>>();

        //File dir = new File(".");

        for (int i = 0; i < files.length; i++) {
            FileInputStream fstream = null;
            try {
                fstream = openFileInput(files[i].getName());
                StringBuffer sbuffer = new StringBuffer();
                int j;
                while ((j = fstream.read())!= -1){
                    sbuffer.append((char)j);
                }
                fstream.close();
                String details[] = sbuffer.toString().split("\n");
                String[] parts = details[0].split(",");
                userList.add(new Pair<Integer, String>(Integer.parseInt(parts[4]), parts[0])); //elements of list should be saved to value in Pair<Integer, Integer>

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

        if(userList.size() > 0) {
            ArrayList values = new ArrayList();

            for (Pair<Integer, String> user : userList) {
                values.add(user.second);
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, android.R.id.text1, values);


            // Assign adapter to ListView
            listView.setAdapter(adapter);

            // ListView Item Click Listener
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {

                    // ListView Clicked item index
                    int itemPosition = position;

                    // ListView Clicked item value
                    String itemValue = (String) listView.getItemAtPosition(position);

                    int index = 0;

                    for (Pair<Integer, String> user : userList) {
                        if (itemValue == user.second) {
                            index = user.first;
                        }
                    }

                    Intent intent = new Intent(getBaseContext(), ReportActivity.class);
                    intent.putExtra("index_id", String.valueOf(index));
                    startActivity(intent);

                }
            });
        }
    }
}
