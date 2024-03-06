package com.example.droiddesign.view;

import android.content.Intent;
<<<<<<< HEAD
import android.graphics.Bitmap;
=======
>>>>>>> f00a83d0f855d53d691828d490feeb337df7260e
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
<<<<<<< HEAD
import android.widget.ImageView;
=======
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
>>>>>>> f00a83d0f855d53d691828d490feeb337df7260e

import androidx.appcompat.app.AppCompatActivity;

import com.example.droiddesign.R;
<<<<<<< HEAD
import com.example.droiddesign.model.GenerateQRCode;
import com.google.android.material.button.MaterialButton;

import com.bumptech.glide.Glide;
=======
import com.example.droiddesign.model.Event;
import com.google.android.material.button.MaterialButton;

import java.util.concurrent.atomic.AtomicReference;
>>>>>>> f00a83d0f855d53d691828d490feeb337df7260e

public class AddEventSecondActivity extends AppCompatActivity {

    Intent intent = getIntent();
    String eventName = intent.getStringExtra("EVENT_NAME");
    String eventLocation = intent.getStringExtra("EVENT_LOCATION");
    String startTime = intent.getStringExtra("START_TIME");
    String endTime = intent.getStringExtra("END_TIME");
    String startDate = intent.getStringExtra("START_DATE");
    String endDate = intent.getStringExtra("END_DATE");
    boolean isMultiDayEvent = intent.getBooleanExtra("IS_MULTIDAY_EVENT", false);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event_second);

<<<<<<< HEAD
        MaterialButton cancelButton = findViewById(R.id.button_cancel);
=======
        // Retrieve data from Intent
        Intent intent = getIntent();
        String eventName = intent.getStringExtra("eventName");
        String eventLocation = intent.getStringExtra("eventLocation");
        String startTime = intent.getStringExtra("startTime");
        String endTime = intent.getStringExtra("endTime");
        String startDate = intent.getStringExtra("startDate");
        String endDate = intent.getStringExtra("endDate");
        MaterialButton buttonUploadPoster = findViewById(R.id.button_upload_poster);

>>>>>>> f00a83d0f855d53d691828d490feeb337df7260e
        AutoCompleteTextView dropdownMenu = findViewById(R.id.QR_menu);
        String[] listItems = new String[]{"Generate New QR", "Use Existing QR"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.list_item,
                listItems);

        dropdownMenu.setAdapter(adapter);

<<<<<<< HEAD
        ImageView posterImage = findViewById(R.id.image_event_poster);
        ImageView qrImage = findViewById(R.id.qr_code);

        String posterImageURL = "https://placehold.jp/160x160.png";
        String qrImageURL = "https://placehold.jp/160x160.png";

        Bitmap qrCodeBitmap = GenerateQRCode.generateQRCodeBitmap("This event name", 200, 200);
        qrImage.setImageBitmap(qrCodeBitmap);

        Glide.with(this)
                        .load(posterImageURL)
                                .placeholder(R.drawable.image_placeholder)
                                        .error(R.drawable.image_error)
                                                .into(posterImage);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddEventSecondActivity.this, EventMenuActivity.class));
=======

        buttonUploadPoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddEventSecondActivity.this, ImageUploadActivity.class);
                startActivity(intent);
>>>>>>> f00a83d0f855d53d691828d490feeb337df7260e
            }
        });


<<<<<<< HEAD
=======
        dropdownMenu.setOnItemClickListener((parent, view, position, id) -> {
            String selectedItem = (String) parent.getItemAtPosition(position);
            // Check if "Generate New QR" is selected
            if ("Generate New QR".equals(selectedItem)) {
                // Navigate to QrCodeGeneratorActivity
                Intent qrGeneratorIntent = new Intent(AddEventSecondActivity.this, QrCodeGeneratorActivity.class);
                startActivity(qrGeneratorIntent);
            }
        });

        Button finishAddButton = findViewById(R.id.finish_add_button);

        finishAddButton.setOnClickListener(view -> {
            Event event = new Event();
            event.setEventName(eventName);
            event.setGeolocation(eventLocation);
            event.setStartTime(startTime);
            event.setEndTime(endTime);
            event.setEventDate(startDate);

            TextView eventDescriptionTextView = findViewById(R.id.text_event_description);
            String eventDescription = eventDescriptionTextView.getText().toString();

            TextView maxAttendeesTextView = findViewById(R.id.text_input_max_attendees);
            String maxAttendeesString = maxAttendeesTextView.getText().toString();
            int maxAttendees = Integer.parseInt(maxAttendeesString);

            event.setDescription(eventDescription);
            event.setSignupLimit(maxAttendees);

            // Save the event to Firestore
            event.saveToFirestore();

            Toast.makeText(AddEventSecondActivity.this, "Event added successfully!", Toast.LENGTH_SHORT).show();
            finish();

            Intent detailsIntent = new Intent(AddEventSecondActivity.this, EventDetailsActivity.class);
            detailsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(detailsIntent);
        });
>>>>>>> f00a83d0f855d53d691828d490feeb337df7260e
    }
}
