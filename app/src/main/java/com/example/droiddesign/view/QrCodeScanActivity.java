package com.example.droiddesign.view;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;
import com.google.firebase.firestore.FieldValue;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.droiddesign.databinding.ActivityQrCodeScanBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class QrCodeScanActivity extends AppCompatActivity {

    private ActivityQrCodeScanBinding binding;
    private FirebaseFirestore mFirestoreDb;

    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    showCamera();
                } else {
                    Toast.makeText(QrCodeScanActivity.this, "Camera permission is required to scan QR codes.", Toast.LENGTH_LONG).show();
                }
            });

    private ActivityResultLauncher<ScanOptions> qrCodeLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() == null) {
            Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
        } else {
            updateEventWithUserId(result.getContents());
        }
    });

    private void updateEventWithUserId(String eventId) {
        mFirestoreDb = FirebaseFirestore.getInstance();
        DocumentReference eventRef = mFirestoreDb.collection("EventsDB").document(eventId);

        eventRef.update("attendeeList", FieldValue.arrayUnion("123"))
                .addOnSuccessListener(aVoid -> Toast.makeText(QrCodeScanActivity.this, "Attendee added successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(QrCodeScanActivity.this, "Failed to update attendee list", Toast.LENGTH_SHORT).show());
    }


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBinding();
        initViews();
    }

    private void initViews() {
        binding.fab.setOnClickListener(view -> {
            checkPermissionAndShowActivity(this);
        });
    }

    private void checkPermissionAndShowActivity(Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            showCamera();
        } else if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            Toast.makeText(context, "Camera permission is necessary to scan QR codes.", Toast.LENGTH_LONG).show();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void initBinding() {
        binding = ActivityQrCodeScanBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}
