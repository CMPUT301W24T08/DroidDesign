package com.example.droiddesign.view;

import static com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.droiddesign.R;
import com.example.droiddesign.databinding.ActivityQrCodeScanBinding;
import com.example.droiddesign.model.AttendanceDB;
import com.example.droiddesign.model.Event;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;

/**
 * An activity that handles QR code scanning using the camera.
 * This activity requests camera permissions, handles user permissions responses,
 * and processes the QR code scanning results.
 */
public class QrCodeScanActivity extends AppCompatActivity {

    /**
     * Binding instance for the activity_qr_code_scan layout.
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

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        attendanceDB = new AttendanceDB();
        ImageButton backButton = findViewById(R.id.button_back2);
        backButton.setOnClickListener(v -> finish());
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
     * @param qrCodeId The scanned content from the QR code.
     */

    private void setResult(String qrCodeId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference qrCodeRef = db.collection("qrcodes").document(qrCodeId);

        qrCodeRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot qrCodeDocument = task.getResult();
                String eventId = qrCodeDocument.getString("eventId");
                String type = qrCodeDocument.getString("type");

                if (eventId != null && type != null) {
                    Intent intent = new Intent(QrCodeScanActivity.this, EventDetailsActivity.class);
                    intent.putExtra("EVENT_ID", eventId);


                    if ("check_in".equals(type)) {
                        // Get user ID
                        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        Log.d("QrCodeScanActivity", "Checking in user");

                        // Fetch location and check in
                        getCurrentLocation((latitude, longitude) -> {
                            Log.d("QrCodeScanActivity", "Checking in userrr");
                            attendanceDB.checkInUser(eventId, userId, latitude, longitude);
                        });

                        Event.loadFromFirestore(eventId, event -> {
                            if (event != null) {
                                // Additional event processing if needed
                            }
                        });
                    } else if ("share".equals(type)) {
                        // Handle share logic if needed
                    }

                    startActivity(intent);
                    finish();
                } else {
                    Log.e("QrCodeScanActivity", "Event ID or Type not found in QR code document.");
                }
            } else {
                Log.e("QrCodeScanActivity", "Failed to fetch QR code document.", task.getException());
            }
        });
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
