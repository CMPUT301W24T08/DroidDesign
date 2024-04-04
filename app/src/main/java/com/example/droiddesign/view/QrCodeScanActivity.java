package com.example.droiddesign.view;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.droiddesign.R;
import com.example.droiddesign.databinding.ActivityQrCodeScanBinding;
import com.example.droiddesign.model.AttendanceDB;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;


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

    private FusedLocationProviderClient fusedLocationClient;
    private AttendanceDB attendanceDB;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    /**
     * An activity result launcher for handling the permission request result for using the camera.
     * It shows the camera to the user if the permission is granted.
     */
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    showCamera();
                } else {
                    // Show why the user needs the camera permission
                    showPermissionRationale();
                }
            });

    /**
     * An activity result launcher for the QR code scanning activity.
     * It processes the scanning result and passes it to the next activity.
     */
    private final ActivityResultLauncher<ScanOptions> qrCodeLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() == null) {
            Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
        } else {
            if (getCallingActivity() != null && getCallingActivity().getClassName().equals(AddEventSecondActivity.class.getName())) {
                Uri barcodeImageUri = Uri.parse(result.getBarcodeImagePath());
                if (barcodeImageUri != null) {
                    try {
                        Bitmap qrBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), barcodeImageUri);
                        setResult(result.getContents(), qrBitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                        setResult(result.getContents());
                    }
                } else {
                    setResult(result.getContents());
                }
            } else {
                setResult(result.getContents());
            }
        }
    });

    /**
     * Processes the QR code result and starts the EventDetailsActivity with the scanned event ID.
     *
     * @param contents The scanned content from the QR code.
     */
    private void setResult(String contents) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    processCheckIn(contents, location);
                } else {
                    Toast.makeText(QrCodeScanActivity.this, "Location not available", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Processes the QR code result and starts the EventDetailsActivity with the scanned event ID.
     *
     * @param contents The scanned content from the QR code.
     * @param qrBitmap The bitmap image of the scanned QR code.
     */
    private void setResult(String contents, Bitmap qrBitmap) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("SCANNED_QR_DATA", contents);

        // Convert the Bitmap to a byte array
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        qrBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        // Add the byte array to the intent
        resultIntent.putExtra("SCANNED_QR_BITMAP", byteArray);

        setResult(RESULT_OK, resultIntent);
        finish();
    }

    /**
     * Shows a dialog explaining why the camera permission is required.
     * The dialog allows the user to grant the permission or cancel the operation.
     */
    private void showPermissionRationale() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Camera Permission Required");
        builder.setMessage("This app needs access to the camera to scan QR codes. Please grant the camera permission to continue.");
        builder.setPositiveButton("OK", (dialog, which) -> {
            // Request the camera permission again
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            // Permission denied, handle accordingly (e.g., show a message or close the activity)
            Toast.makeText(this, "Camera permission denied. Cannot scan QR codes.", Toast.LENGTH_SHORT).show();
            finish();
        });
        builder.show();
    }

    /**
     * Shows the camera to scan the QR code.
     */
    private void showCamera() {
        ScanOptions options = new ScanOptions();
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
        options.setPrompt("Scan QR code");
        options.setCameraId(0);
        options.setBeepEnabled(false);
        options.setBarcodeImageEnabled(true); // Enable capturing the barcode image
        options.setOrientationLocked(false);

        qrCodeLauncher.launch(options);
    }

    /**
     * Initializes the activity and view binding.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBinding();
        initViews();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        attendanceDB = new AttendanceDB();  // Initialize your AttendanceDB

        Button backButton = findViewById(R.id.cancelButton);
        backButton.setOnClickListener(v -> finish());
    }

    /**
     * Initializes the view components and sets up the camera permission check.
     */
    private void initViews() {
        binding.fab.setOnClickListener(view -> {
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

    /**
     * Processes the check-in for the user at the event.
     *
     * @param eventId  The event ID to check in to.
     * @param location The location of the user when checking in.
     */
    private void processCheckIn(String eventId, Location location) {
        Intent intent = new Intent(QrCodeScanActivity.this, EventDetailsActivity.class);
        intent.putExtra("EVENT_ID", eventId);
        startActivity(intent);

        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        attendanceDB.checkInUser(eventId, userId, latitude, longitude);
    }
}