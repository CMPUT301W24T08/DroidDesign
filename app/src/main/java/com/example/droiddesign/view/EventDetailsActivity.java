package com.example.droiddesign.view;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.droiddesign.R;

public class EventDetailsActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_details);

		// Initialize your UI components and setup listeners here
		// Example: TextView textView = findViewById(R.id.your_text_view_id);
		ImageButton backButton = findViewById(R.id.back_button);
		backButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	// You can add more methods here as needed for your activity
}

