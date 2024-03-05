package com.example.droiddesign.view;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.droiddesign.R;

public class AddEventSecondActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event_second);

        AutoCompleteTextView dropdownMenu = findViewById(R.id.QR_menu);
        String[] listItems = new String[]{"Generate New QR", "Use Existing QR"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.list_item,
                listItems);

        dropdownMenu.setAdapter(adapter);
    }
}
