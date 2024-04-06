package com.example.droiddesign.view.Organizer;

import static com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.droiddesign.R;
import com.example.droiddesign.controller.UploadQR;
import com.example.droiddesign.databinding.ActivityQrCodeScanBinding;
import com.example.droiddesign.model.AttendanceDB;
import com.example.droiddesign.model.QRcode;
import com.example.droiddesign.view.Everybody.EventDetailsActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

/**
 * An activity that handles QR code scanning using the camera.
 * This activity requests camera permissions, handles user permissions responses,
 * and processes the QR code scanning results.
 */
public class QrCodeScanActivity extends AppCompatActivity {

    /**
     * Firebase Storage reference for storing QR code images.
     */
    private StorageReference mStorageRef;

    /**
     * Firestore database instance for saving QR code metadata.
     */
    private FirebaseFirestore mFirestoreDb;

    /**
     * Binding instance for the activity_qr_code_scan layout.
     *
     *
     */
    private ActivityQrCodeScanBinding binding;

    private AttendanceDB attendanceDB;

    private FusedLocationProviderClient fusedLocationClient;
    private boolean isCheckedIn = false;

    /**
     * Initializes the activity and view binding.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBinding();
        initViews();

        mStorageRef = FirebaseStorage.getInstance().getReference("qrcodes");
        mFirestoreDb = FirebaseFirestore.getInstance();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        attendanceDB = new AttendanceDB();
        ImageButton backButton = findViewById(R.id.button_back2);
        backButton.setOnClickListener(v -> finish());

        Intent intent = getIntent();
        String origin = intent.getStringExtra("ORIGIN");

        if ("AddEventSecondActivity".equals(origin)) {
            TextView header = findViewById(R.id.header);
            header.setText("Recycle QRs!");
        }
    }

    /**
     * An activity result launcher for handling the permission request result for using the camera.
     * It shows the camera to the user if the permission is granted.
     */
    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    showCamera();
                } else {
                    //show why user needs permission
                }
            });

    /**
     * An activity result launcher for the QR code scanning activity.
     * It processes the scanning result and passes it to the next activity.
     */
    private ActivityResultLauncher<ScanOptions> qrCodeLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() == null) {
            Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
        } else {
            setResult(result.getContents());
        }
    });

    /**
     * Processes the QR code result and starts the EventDetailsActivity with the scanned event ID.
     *
     * @param contents The scanned content from the QR code.
     */

    private void setResult(String contents) {

        Intent returnIntent = new Intent();
        if ("AddEventSecondActivity".equals(getIntent().getStringExtra("ORIGIN"))) {
            String eventId = getIntent().getStringExtra("EVENT_ID"); // Retrieve the event ID passed from AddEventSecondActivity

            // Generate a new check-in QR code
            QRcode checkInQrCode = new QRcode(eventId, "check_in");
            String qrId = UUID.randomUUID().toString(); // Generate a unique ID for this QR code
            String type = "check_in"; // Assuming you want to store this as a check-in type

            uploadQrCode(qrId, eventId, type, contents, new OnQrCodeUploadListener() {
                @Override
                public void onQrCodeUploadSuccess(String qrUrl) {
                    // Send back the new check-in QR code ID and the original QR code URL
                    returnIntent.putExtra("checkInQrId", qrId);
                    returnIntent.putExtra("checkInQrUrl", qrUrl); // Use the contents as the QR URL
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }

                @Override
                public void onQrCodeUploadFailure(String errorMessage) {
                    Toast.makeText(QrCodeScanActivity.this, "Upload failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference qrCodeRef = db.collection("qrcodes").document(contents);

            qrCodeRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                    DocumentSnapshot qrCodeDocument = task.getResult();
                    String eventId = qrCodeDocument.getString("eventId");
                    String type = qrCodeDocument.getString("type");

                    if (eventId != null && type != null) {
                        Intent intent = new Intent(QrCodeScanActivity.this, EventDetailsActivity.class);
                        intent.putExtra("EVENT_ID", eventId);
                        intent.putExtra("ORIGIN", "QrCodeScanActivity");

                        if ("check_in".equals(type)) {
                            checkInUser(eventId);
                            startActivity(intent);
                            finish();
                        } else if ("share".equals(type)) {
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        Log.e("QrCodeScanActivity", "Event ID or Type not found in QR code document.");
                    }
                } else {
                    // No document found by ID, try to find by qrurl
                    searchByQRUrl(contents);
                }
            });
        }

    }

    private void checkInUser(String eventId) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        getCurrentLocation((latitude, longitude) -> {
            attendanceDB.checkInUser(eventId, userId, latitude, longitude);
            Toast.makeText(QrCodeScanActivity.this, "Checked in successfully", Toast.LENGTH_SHORT).show();
        });

    }

    private void searchByQRUrl(String qrUrl) {
        Log.d("QrCodeScanActivity", "Searching for QR URL: " + qrUrl);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("qrcodes").whereEqualTo("qrUrl", qrUrl).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                for (DocumentSnapshot document : task.getResult()) {
                    String eventId = document.getString("eventId");
                    if (eventId != null) {
                        checkInUser(eventId);
                        Intent intent = new Intent(QrCodeScanActivity.this, EventDetailsActivity.class);
                        intent.putExtra("EVENT_ID", eventId);
                        intent.putExtra("ORIGIN", "QrCodeScanActivity");
                        startActivity(intent);
                        finish();
                        return;
                    }
                }
            }
            Toast.makeText(QrCodeScanActivity.this, "No matching QR code found.", Toast.LENGTH_SHORT).show();
        });
    }



    private void uploadQrCode(String qrId, String eventId, String type, String contents, final OnQrCodeUploadListener listener) {
        UploadQR upload = new UploadQR(contents, eventId, type);  // Use contents directly as qrUrl
        mFirestoreDb.collection("qrcodes").document(qrId).set(upload)
                .addOnSuccessListener(aVoid -> listener.onQrCodeUploadSuccess(contents))  // Return contents as qrUrl
                .addOnFailureListener(e -> listener.onQrCodeUploadFailure(e.getMessage()));
    }

    public interface OnQrCodeUploadListener {
        void onQrCodeUploadSuccess(String qrUrl);
        void onQrCodeUploadFailure(String errorMessage);
    }

    private void getCurrentLocation(MyLocationCallback callback) {

        if (isCheckedIn) {
            return;  // If already checked in, do not proceed
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request permissions and then call getCurrentLocation again if permission granted
            return;
        }

        LocationRequest locationRequest = new LocationRequest.Builder(PRIORITY_HIGH_ACCURACY, 10000)
                .setMaxUpdateDelayMillis(5000)
                .build();

        fusedLocationClient.requestLocationUpdates(locationRequest,
                new com.google.android.gms.location.LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        if (locationResult != null && locationResult.getLastLocation() != null) {
                            double latitude = locationResult.getLastLocation().getLatitude();
                            double longitude = locationResult.getLastLocation().getLongitude();

                            fusedLocationClient.removeLocationUpdates(this);

                            if (!isCheckedIn) {
                                isCheckedIn = true;
                                callback.onLocationObtained(latitude, longitude);
                            }
                        }
                    }
                }, Looper.getMainLooper());
    }


    private interface MyLocationCallback {
        void onLocationObtained(double latitude, double longitude);
    }



    /**
     * Initializes the QR code scanner options and starts the camera.
     */
    private void showCamera() {
        ScanOptions options = new ScanOptions();
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
        options.setPrompt("Scan QR code");
        options.setCameraId(0);
        options.setBeepEnabled(false);
        options.setBarcodeImageEnabled(true);
        options.setOrientationLocked(false);

        qrCodeLauncher.launch(options);
    }



    /**
     * Initializes the view components and sets up the camera permission check.
     */
    private void initViews() {
        Button myButton = findViewById(R.id.scan_button);
        myButton.setOnClickListener(view -> {
            checkPermissionAndShowActivity(this);
        });
    }

    /**
     * Checks for camera permission. If not granted, it requests permission. Otherwise, it shows the camera.
     *
     * @param context The context in which the activity is running.
     */
    private void checkPermissionAndShowActivity(Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            showCamera();
        } else if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            Toast.makeText(context, "Camera permission required", Toast.LENGTH_SHORT).show();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    /**
     * Initializes the data binding for this activity.
     */
    private void initBinding() {
        binding = ActivityQrCodeScanBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}
