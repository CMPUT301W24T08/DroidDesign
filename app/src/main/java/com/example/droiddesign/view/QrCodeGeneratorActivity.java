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

public class QrCodeGeneratorActivity extends AppCompatActivity {

    private StorageReference mStorageRef;
    private FirebaseFirestore mFirestoreDb;
    private Button mButtonSaveQrCode;
    private ImageView mImageViewQrCode;
    private Bitmap mQrBitmap; // To hold the generated QR code

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_generator);

        EditText editText = findViewById(R.id.edit_text);
        Button buttonGenerate = findViewById(R.id.button_generate);
        mImageViewQrCode = findViewById(R.id.qr_code);
        mButtonSaveQrCode = findViewById(R.id.button_save_qr);
        Button buttonBack = findViewById(R.id.button_back);

        mStorageRef = FirebaseStorage.getInstance().getReference("qrcodes");
        mFirestoreDb = FirebaseFirestore.getInstance();

        buttonGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QRCodeWriter qrCodeWriter = new QRCodeWriter();
                try {
                    // Retrieve the event ID passed from AddEventSecondActivity
                    Intent intent = getIntent();
                    String eventId = intent.getStringExtra("eventID");

                    if (eventId == null || eventId.trim().isEmpty()) {
                        Toast.makeText(QrCodeGeneratorActivity.this, "Event ID is missing", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Use the event ID for the QR code
                    BitMatrix bitMatrix = qrCodeWriter.encode(eventId, BarcodeFormat.QR_CODE, 200, 200);
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    mQrBitmap = barcodeEncoder.createBitmap(bitMatrix);
                    mImageViewQrCode.setImageBitmap(mQrBitmap);
                } catch (WriterException e) {
                    Log.e("QrCodeGenerator", "Error generating QR code", e);
                }
            }
        });


        mButtonSaveQrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mQrBitmap != null) {
                    uploadQrCode(mQrBitmap, editText.getText().toString());
                } else {
                    Toast.makeText(QrCodeGeneratorActivity.this, "No QR Code generated", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonBack.setOnClickListener(v -> finish());
    }

    private void uploadQrCode(Bitmap bitmap, String text) {
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
                                Upload upload = new Upload(text, uri.toString());
                                String uploadId = mFirestoreDb.collection("qrcodes").document().getId();
                                mFirestoreDb.collection("qrcodes").document(uploadId).set(upload)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(QrCodeGeneratorActivity.this, "QR Code saved", Toast.LENGTH_LONG).show();
                                                Intent resultIntent = new Intent();
                                                resultIntent.putExtra("qrCodeUrl", uri.toString());
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
