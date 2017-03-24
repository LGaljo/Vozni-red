package com.lukag.atvoznired;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;

public class DisplayMessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();

        HashMap<String, String> vozniRed = (HashMap<String,String>)intent.getSerializableExtra(MainActivity.EXTRA_MESSAGE);

        Log.v("HashMapTest", vozniRed.get("ID"));
        // Capture the layout's TextView and set the string as its text
        //TextView textView = (TextView) findViewById(R.id.textView);
        //textView.setText(vozniRed.toString());
    }
}
