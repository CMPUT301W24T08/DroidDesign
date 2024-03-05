package com.example.droiddesign.view;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.droiddesign.R;
import com.example.droiddesign.model.GenerateQRCode;
import com.google.android.material.button.MaterialButton;

import com.bumptech.glide.Glide;

public class AddEventSecondActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event_second);

        MaterialButton cancelButton = findViewById(R.id.button_cancel);
        AutoCompleteTextView dropdownMenu = findViewById(R.id.QR_menu);
        String[] listItems = new String[]{"Generate New QR", "Use Existing QR"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.list_item,
                listItems);

        dropdownMenu.setAdapter(adapter);

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
                finish();
            }
        });


    }
}
