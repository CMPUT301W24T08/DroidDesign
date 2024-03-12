package com.example.droiddesign.view;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.droiddesign.R;
import com.example.droiddesign.databinding.ActivityQrCodeScanBinding;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

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
        Intent intent = new Intent(QrCodeScanActivity.this, EventDetailsActivity.class);
        intent.putExtra("EVENT_ID", contents);
        startActivity(intent);
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
     * Initializes the activity and view binding.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBinding();
        initViews();

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
}
