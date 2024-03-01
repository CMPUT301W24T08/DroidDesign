package com.example.droiddesign.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.droiddesign.R;

public class LaunchScreenActivity extends AppCompatActivity {

    private Button skipButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_screen);

        skipButton = findViewById(R.id.skip_button);
        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LaunchScreenActivity.this, EventMenuActivity.class);
                startActivity(intent);
            }
        });
    }
}