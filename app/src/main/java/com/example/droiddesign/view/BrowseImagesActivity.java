package com.example.droiddesign.view;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.droiddesign.R;
import com.example.droiddesign.model.ImageItem;
import com.example.droiddesign.model.User;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class BrowseImagesActivity extends AppCompatActivity {
	private RecyclerView imagesRecyclerView;
	private ImagesAdapter imagesAdapter;
	private List<ImageItem> imageItemList;
	private FirebaseFirestore firestore;
	private Spinner imageTypeSpinner;
	String selection;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_browse_images);

		try {
			firestore = FirebaseFirestore.getInstance();
			imageItemList = new ArrayList<>();
			imagesRecyclerView = findViewById(R.id.images_recyclerview);
			imageTypeSpinner = findViewById(R.id.image_type_spinner);

			imagesAdapter = new ImagesAdapter(imageItemList);
			int numberOfColumns = 3; // Adjust the number of columns as needed
			imagesRecyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
			imagesRecyclerView.setAdapter(imagesAdapter);

			setupSpinner();

			findViewById(R.id.button_back).setOnClickListener(v -> finish());
		} catch (Exception e) {
			Toast.makeText(this, "An error occurred during initialization: " + e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}

	private void setupSpinner() {
		try {
			ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
					R.array.image_types, android.R.layout.simple_spinner_item);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			imageTypeSpinner.setAdapter(adapter);
			imageTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
					selection = parent.getItemAtPosition(position).toString();
					if ("User Profile Pics".equals(selection)) {
						loadUserProfileImages();
					} else if ("Event Posters".equals(selection)) {
						loadEventPosters();
					}
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
				}
			});
		} catch (Exception e) {
			Toast.makeText(this, "An error occurred while setting up the spinner: " + e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}

	private void loadUserProfileImages() {
		try {
			firestore.collection("Users")
					.get()
					.addOnCompleteListener(task -> {
						if (task.isSuccessful()) {
							imageItemList.clear();
							for (DocumentSnapshot document : task.getResult()) {
								User user = document.toObject(User.class);
								if (user != null && user.getProfilePic() != null) {
									imageItemList.add(new ImageItem(user.getProfilePic(), user.getUserId()));
								}
							}
							imagesAdapter.notifyDataSetChanged();
						} else {
							Toast.makeText(BrowseImagesActivity.this, "Failed to load user images.", Toast.LENGTH_SHORT).show();
						}
					});
		} catch (Exception e) {
			Toast.makeText(this, "An error occurred while loading user profile images: " + e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}

	private void loadEventPosters() {
		try {
			firestore.collection("EventsDB")
					.get()
					.addOnCompleteListener(task -> {
						if (task.isSuccessful()) {
							imageItemList.clear();
							for (DocumentSnapshot document : task.getResult()) {
								String imageUrl = document.getString("imagePosterId");
								if (imageUrl != null) {
									imageItemList.add(new ImageItem(imageUrl, document.getString("eventId")));
								}
							}
							imagesAdapter.notifyDataSetChanged();
						} else {
							Toast.makeText(BrowseImagesActivity.this, "Failed to load event posters.", Toast.LENGTH_SHORT).show();
						}
					});
		} catch (Exception e) {
			Toast.makeText(this, "An error occurred while loading event posters: " + e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}

	public void showImageDialog(ImageItem imageItem) {
		try {
			Dialog dialog = new Dialog(this);
			dialog.setContentView(R.layout.dialog_image_preview);

			ImageView imageView = dialog.findViewById(R.id.dialog_imageview);
			Button deleteButton = dialog.findViewById(R.id.dialog_delete_button);
			Button cancelButton = dialog.findViewById(R.id.dialog_cancel_button);

			Glide.with(this).load(imageItem.getImageUrl()).override(150, 150).into(imageView);

			deleteButton.setOnClickListener(v -> {
				deleteImage(imageItem);
				imagesAdapter.notifyDataSetChanged();
				dialog.dismiss();
			}); cancelButton.setOnClickListener(v -> dialog.dismiss());

				dialog.show();
			} catch (Exception e) {
				Toast.makeText(this, "An error occurred while showing image dialog: " + e.getMessage(), Toast.LENGTH_LONG).show();
			}
		}

		private void deleteImage(ImageItem imageItem) {
			try {
				if ("User Profile Pics".equals(selection)) {
					// Delete user profile picture
					firestore.collection("Users")
							.document(imageItem.getOwnerId()) // Assuming ImageItem has an ID field
							.update("profilePic", null)
							.addOnSuccessListener(aVoid -> {
								Toast.makeText(BrowseImagesActivity.this, "Profile picture removed successfully.", Toast.LENGTH_SHORT).show();
								loadUserProfileImages();
							})
							.addOnFailureListener(e -> {
								Toast.makeText(BrowseImagesActivity.this, "Failed to remove profile picture.", Toast.LENGTH_SHORT).show();
							});
				} else if ("Event Posters".equals(selection)) {
					// Delete event poster
					firestore.collection("EventsDB")
							.document(imageItem.getOwnerId()) // Assuming ImageItem has an ID field
							.update("imagePosterId", null)
							.addOnSuccessListener(aVoid -> {
								Toast.makeText(BrowseImagesActivity.this, "Event poster removed successfully.", Toast.LENGTH_SHORT).show();
								loadEventPosters();
							})
							.addOnFailureListener(e -> {
								Toast.makeText(BrowseImagesActivity.this, "Failed to remove event poster.", Toast.LENGTH_SHORT).show();
							});
				}
			} catch (Exception e) {
				Toast.makeText(this, "An error occurred while deleting the image: " + e.getMessage(), Toast.LENGTH_LONG).show();
			}
		}	}

