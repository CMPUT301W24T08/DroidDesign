package com.example.droiddesign.view;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.droiddesign.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.droiddesign.controller.Upload;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;


public class ImageUploadActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    private Button mButtonChooseImage;
    private Button mButtonUpload;
    private EditText mEditTextFileName;
    private ImageView mImageView;

    private StorageReference mStorageRef;
    private FirebaseFirestore mFirestoreDb;
    private ProgressBar mProgressBar;
    private StorageTask mUploadTask;

    private Uri mImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_upload);

        mButtonChooseImage = findViewById(R.id.button_choose_image);
        mButtonUpload = findViewById(R.id.button_upload);
        mEditTextFileName = findViewById(R.id.edit_text_file_name);
        mImageView = findViewById(R.id.image_preview);
        mProgressBar = findViewById(R.id.progress_bar);
        Button buttonBack = findViewById(R.id.button_back_upload);

        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        mFirestoreDb = FirebaseFirestore.getInstance(); // Initialize Firestore

        mButtonChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        mButtonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(ImageUploadActivity.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    uploadFile();
                }
            }
        });


        buttonBack.setOnClickListener(v -> finish());
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile() {
        if (mImageUri != null) {
            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));

            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(() -> mProgressBar.setProgress(0), 500);

                            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Upload upload = new Upload(mEditTextFileName.getText().toString().trim(), uri.toString());
                                    String uploadId = mFirestoreDb.collection("uploads").document().getId();
                                    mFirestoreDb.collection("uploads").document(uploadId).set(upload)
                                            .addOnSuccessListener(documentReference -> {
                                                Toast.makeText(ImageUploadActivity.this, "Upload successful", Toast.LENGTH_LONG).show();
                                                Intent resultIntent = new Intent();
                                                resultIntent.putExtra("imagePosterUrl", uri.toString());
                                                setResult(RESULT_OK, resultIntent);
                                                finish();
                                            })
                                            .addOnFailureListener(e -> Toast.makeText(ImageUploadActivity.this, "Upload failed", Toast.LENGTH_LONG).show());
                                }
                            });
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(ImageUploadActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        mProgressBar.setProgress(0);
                    })
                    .addOnProgressListener(taskSnapshot -> {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        mProgressBar.setProgress((int) progress);
                    });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }


    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mImageUri = data.getData();
            Picasso.get().load(mImageUri).into(mImageView);
        }
    }
}
