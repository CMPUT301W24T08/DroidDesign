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
		setContentView(R.layout.activity_event_details); // Ensure you have a layout file named activity_event_details.xml in your res/layout directory

		// Initialize your UI components and setup listeners here
		// Example: TextView textView = findViewById(R.id.your_text_view_id);
		ImageButton backButton = findViewById(R.id.button_back);
		backButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	// You can add more methods here as needed for your activity
}

