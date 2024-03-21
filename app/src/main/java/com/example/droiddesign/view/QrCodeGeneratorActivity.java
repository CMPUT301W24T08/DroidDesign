package com.example.droiddesign.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.droiddesign.R;
import com.example.droiddesign.controller.Upload;
import com.example.droiddesign.controller.UploadQR;
import com.example.droiddesign.model.QRcode;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.ByteArrayOutputStream;

/**
 * Activity for generating and uploading a QR code.
 * This activity allows users to generate a QR code based on an input string (event ID)
 * and upload the generated QR code image to Firebase Storage.
 */

public class QrCodeGeneratorActivity extends AppCompatActivity {

    /**
     * Firebase Storage reference for storing QR code images.
     */
    private StorageReference mStorageRef;

    /**
     * Firestore database instance for saving QR code metadata.
     */
    private FirebaseFirestore mFirestoreDb;

    /**
     * Bitmap object to hold the generated QR code image.
     */

    private QRcode shareQrCode, checkInQrCode;

    /**
     * Initializes the activity, setting up UI components and button click listeners.
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied. Otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_generator);

        EditText editText = findViewById(R.id.edit_text);
        Button buttonGenerate = findViewById(R.id.button_generate);
        /**
         * Image view to display the generated QR code.
         */
        ImageView mImageViewQrCode = findViewById(R.id.qr_code);
        /**
         * Button to trigger saving the generated QR code.
         */
        Button mButtonSaveQrCode = findViewById(R.id.button_save_qr);
        Button buttonBack = findViewById(R.id.button_back);

        mStorageRef = FirebaseStorage.getInstance().getReference("qrcodes");
        mFirestoreDb = FirebaseFirestore.getInstance();

        buttonGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                String eventId = intent.getStringExtra("eventID");

                if (eventId == null || eventId.trim().isEmpty()) {
                    Toast.makeText(QrCodeGeneratorActivity.this, "Event ID is missing", Toast.LENGTH_SHORT).show();
                }

                shareQrCode = new QRcode(eventId, "share");
                checkInQrCode = new QRcode(eventId, "check_in");

                mImageViewQrCode.setImageBitmap(shareQrCode.getmQrBitmap());
            }
        });




        mButtonSaveQrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shareQrCode.getmQrBitmap() != null && checkInQrCode.getmQrBitmap() != null) {
                    uploadQrCode(shareQrCode);
                    uploadQrCode(checkInQrCode);
                } else {
                    Toast.makeText(QrCodeGeneratorActivity.this, "No QR Code generated", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonBack.setOnClickListener(v -> finish());
    }

    /**
     * Uploads the generated QR code image to Firebase Storage and saves its metadata to Firestore.
     * @param qrCode The QRcode to be uploaded to the firestore.
     */

    private void uploadQrCode(QRcode qrCode) {
        Bitmap bitmap = qrCode.getmQrBitmap();
        String qrId = qrCode.getQrId();
        String eventId = qrCode.getEventId();
        String eventName = qrCode.getEventName();
        String type = qrCode.getType();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        StorageReference fileRef = mStorageRef.child(System.currentTimeMillis() + ".png");

        fileRef.putBytes(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                // Assuming 'Upload' class has been updated to include new fields
                                UploadQR upload = new UploadQR(uri.toString(), qrId, eventId, eventName, type);
                                String uploadId = mFirestoreDb.collection("qrcodes").document().getId();
                                mFirestoreDb.collection("qrcodes").document(uploadId).set(upload)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(QrCodeGeneratorActivity.this, "QR Code and details saved", Toast.LENGTH_LONG).show();
                                                Intent resultIntent = new Intent();
                                                // Differentiating the result based on the QR code type
                                                if ("share".equalsIgnoreCase(type)) {
                                                    resultIntent.putExtra("shareQrUrl", uri.toString());
                                                } else if ("check_in".equalsIgnoreCase(type)) {
                                                    resultIntent.putExtra("checkInQrUrl", uri.toString());
                                                }
                                                setResult(RESULT_OK, resultIntent);
                                                finish();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(QrCodeGeneratorActivity.this, "Save failed", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(QrCodeGeneratorActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
