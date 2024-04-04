package com.example.droiddesign.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.droiddesign.R;
import com.example.droiddesign.model.Event;
import com.example.droiddesign.model.QRcode;
import com.example.droiddesign.model.SharedPreferenceHelper;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.UUID;

/**
 * This activity allows users to add an event to the app.
 * The user can enter details about the event, upload an image poster for the event,
 * and generate a QR code for the event.
 */
public class AddEventSecondActivity extends AppCompatActivity {
    private static final int UPLOAD_IMAGE_REQUEST = 1;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ActivityResultLauncher<Intent> qrGeneratorLauncher;
    private ActivityResultLauncher<Intent> scanQrLauncher;
    // Generate a UUID for the event
    private final String uniqueID = UUID.randomUUID().toString();
    private String eventName, eventLocation, eventStartTime, eventEndTime, eventDate, eventGeo, shareQrUrl, shareQrId, checkInQrUrl, checkInQrId, imagePosterId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event_second);

        qrGeneratorLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            shareQrUrl = data.getStringExtra("shareQrUrl");
                            shareQrId = data.getStringExtra("shareQrId");
                            checkInQrUrl = data.getStringExtra("checkInQrUrl");
                            checkInQrId = data.getStringExtra("checkInId");
                            Log.d("AddEventSecondActivity", "Share QR Code ID: " + shareQrId);
                            Log.d("AddEventSecondActivity", "Check-in QR Code ID: " + checkInQrId);
                        }
                    }
                });

        scanQrLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            shareQrId = data.getStringExtra("SCANNED_QR_DATA");
                            Bitmap qrBitmap = data.getParcelableExtra("SCANNED_QR_BITMAP");
                            Log.d("AddEventSecondActivity", "Scanned QR Code ID: " + shareQrId);

                            // Create a QR code object from the scanned QR code and upload it to Firebase Storage
                            QRcode shareQr = new QRcode(uniqueID, shareQrId, qrBitmap);
                            shareQr.upload(new QRcode.OnQrCodeUploadListener() {
                                @Override
                                public void onQrCodeUploadSuccess() {
                                    shareQrUrl = shareQr.getUri();
                                    Log.d("AddEventSecondActivity", "Share QR code uploaded successfully. URL: " + shareQrUrl);
                                }

                                @Override
                                public void onQrCodeUploadFailure(String errorMessage) {
                                    // Handle upload failure
                                    Log.e("AddEventSecondActivity", "Share QR code upload failed: " + errorMessage);
                                    // Display an error message or take appropriate action
                                }
                            });
                            // Generate the check-in QR code
                            generateCheckInQrCode();
                        }
                    }
                });

        Intent intent = getIntent();
        populateEventFromIntent(intent);

        MaterialButton buttonUploadPoster = findViewById(R.id.button_upload_poster);
        AutoCompleteTextView dropdownMenu = findViewById(R.id.QR_menu);
        Button finishAddButton = findViewById(R.id.finish_add_button);

        setupDropdownMenu(dropdownMenu);

        buttonUploadPoster.setOnClickListener(view -> {
            Intent imageUploadIntent = new Intent(AddEventSecondActivity.this, ImageUploadActivity.class);
            startActivityForResult(imageUploadIntent, UPLOAD_IMAGE_REQUEST);
        });

        finishAddButton.setOnClickListener(view -> {
            saveEvent();
        });

        Button cancelButton = findViewById(R.id.button_cancel);
        cancelButton.setOnClickListener(view -> finish());
    }

    /**
     * Populates the event details from the intent.
     * @param intent The intent containing the event details.
     */
    private void populateEventFromIntent(Intent intent) {
        try {
            eventName = intent.getStringExtra("eventName");
            eventLocation = intent.getStringExtra("eventLocation");
            eventStartTime = intent.getStringExtra("startTime");
            eventEndTime = intent.getStringExtra("endTime");
            eventDate = intent.getStringExtra("startDate");
            eventGeo = intent.getStringExtra("eventLocation");
        } catch (Exception e) {
            Log.e("AddEventSecondActivity", "Error populating event from intent", e);
            Toast.makeText(this, "Error loading event details", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Sets up the dropdown menu for selecting QR code options.
     * @param dropdownMenu The dropdown menu to set up.
     */
    private void setupDropdownMenu(AutoCompleteTextView dropdownMenu) {
        try {
            String[] listItems = new String[]{"Generate New QR", "Use Existing QR"};
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.list_item, listItems);
            dropdownMenu.setAdapter(adapter);

            dropdownMenu.setOnItemClickListener((parent, view, position, id) -> {
                String selectedItem = (String) parent.getItemAtPosition(position);
                if ("Generate New QR".equals(selectedItem)) {
                    Intent qrGeneratorIntent = new Intent(AddEventSecondActivity.this, QrCodeGeneratorActivity.class);
                    qrGeneratorIntent.putExtra("eventID", uniqueID);
                    qrGeneratorLauncher.launch(qrGeneratorIntent);
                } else if ("Use Existing QR".equals(selectedItem)) {
                    Intent intent = new Intent(AddEventSecondActivity.this, QrCodeScanActivity.class);
                    scanQrLauncher.launch(intent);
                }

            });
        } catch (Exception e) {
            Log.e("AddEventSecondActivity", "Error setting up dropdown menu", e);
            Toast.makeText(this, "Error setting up QR options", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Generates a share QR code for the event.
     * @return The QR code object.
     */
    private void generateCheckInQrCode() {
        QRcode checkInQrCode = new QRcode(uniqueID, "check_in");
        checkInQrId = checkInQrCode.getQrId();

        checkInQrCode.upload(new QRcode.OnQrCodeUploadListener() {
            @Override
            public void onQrCodeUploadSuccess() {
                checkInQrUrl = checkInQrCode.getUri();
                Log.d("AddEventSecondActivity", "Check-in QR code uploaded successfully. URL: " + checkInQrUrl);
            }

            @Override
            public void onQrCodeUploadFailure(String errorMessage) {
                // Handle upload failure
                Log.e("CheckInQRCode", "Check-in QR code upload failed: " + errorMessage);
                // Display an error message or take appropriate action
            }
        });
    }

    /**
     * Saves the event details to Firestore.
     */
    private void saveEvent() {
        TextView eventDescriptionTextView = findViewById(R.id.text_input_event_description);
        String eventDescription = eventDescriptionTextView.getText().toString();
        Log.d("AddEvent", "Max Attendees String: '" + eventDescription + "'");
        TextView maxAttendeesTextView = findViewById(R.id.input_number_max_attendees);
        TextView milestoneTextView = findViewById(R.id.input_number_milestone);
        String maxAttendeesString = maxAttendeesTextView.getText().toString().trim();
        String milestoneString = milestoneTextView.getText().toString().trim();

        int maxAttendees = 0;
        int milestone = 0;
        if (!maxAttendeesString.isEmpty() && !milestoneString.isEmpty()) {
            try {
                maxAttendees = Integer.parseInt(maxAttendeesString);
                milestone = Integer.parseInt(milestoneString);
            } catch (NumberFormatException e) {
                Toast.makeText(AddEventSecondActivity.this, "Invalid number for maximum attendees", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Add user event to their managedEventsList
        SharedPreferenceHelper prefsHelper = new SharedPreferenceHelper(this);
        String currentUserId = prefsHelper.getUserId();

        Event event = new Event(uniqueID, eventName, eventDate, eventLocation, eventStartTime, eventEndTime, eventLocation,
                currentUserId, imagePosterId, eventDescription, maxAttendees, 0, milestone, shareQrUrl,
                checkInQrUrl, shareQrId, checkInQrId);

        event.saveToFirestore();

        DocumentReference userRef = db.collection("Users").document(currentUserId);
        userRef.update("managedEventsList", FieldValue.arrayUnion(event.getEventId()))
                .addOnSuccessListener(aVoid -> Toast.makeText(AddEventSecondActivity.this, "Event added successfully!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Log.e("AddEventSecondActivity", "Error adding event to user's managedEventsList", e));

        Intent detailsIntent = new Intent(AddEventSecondActivity.this, EventDetailsActivity.class);
        detailsIntent.putExtra("EVENT_ID", event.getEventId());
        detailsIntent.putExtra("ORIGIN", "AddEventSecondActivity");
        detailsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(detailsIntent);

        finish();
    }
}

