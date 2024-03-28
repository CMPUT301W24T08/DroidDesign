package com.example.droiddesign.view;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.droiddesign.R;

import java.util.ArrayList;
import java.util.List;

public class BrowseImagesActivity extends AppCompatActivity {
	private RecyclerView imagesRecyclerView;
	private ImagesAdapter imagesAdapter;
	private List<ImageItem> imageItemList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_browse_images);

		imagesRecyclerView = findViewById(R.id.images_recyclerview);
		imageItemList = new ArrayList<>();
		// Assume ImageItem is a model class you've defined for image data
		imagesAdapter = new ImagesAdapter(imageItemList);
		imagesRecyclerView.setAdapter(imagesAdapter);
		imagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

		// Set up the RecyclerView with a GridLayoutManager
		int numberOfColumns = 3; // Adjust the number of columns as needed
		imagesRecyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
		imagesRecyclerView.setAdapter(imagesAdapter);


		// Load images here...
		loadImages();

		findViewById(R.id.button_back).setOnClickListener(v -> finish());

	}

	private void loadImages() {
		// Implement your logic to load images into imageItemList and notify the adapter
	}
}
