package com.example.droiddesign.view;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;

import com.example.droiddesign.R;

public class AppSettingsActivity extends AppCompatActivity {

	private SwitchCompat switchGeolocation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_settings);

		// Setup UI components
		setupBackButton();
		setupSwitchGeolocation();
		setupSpinner();
	}

	private void setupBackButton() {
		ImageButton backButton = findViewById(R.id.button_back);
		backButton.setOnClickListener(v -> finish());
	}

	private void setupSwitchGeolocation() {
		switchGeolocation = findViewById(R.id.switch_geo_location);
		Drawable thumbDrawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.thumb_selector);
		Drawable trackDrawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.track_selector);
		switchGeolocation.setThumbDrawable(thumbDrawable);
		switchGeolocation.setTrackDrawable(trackDrawable);

		switchGeolocation.setOnCheckedChangeListener((buttonView, isChecked) -> {
			Toast.makeText(AppSettingsActivity.this,
					"Geolocation is " + (isChecked ? "enabled" : "disabled"),
					Toast.LENGTH_SHORT).show();
		});
	}

	private void setupSpinner() {
		Spinner spinner = findViewById(R.id.settings_spinner);
		ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(this,
				R.layout.custom_spinner_item,
				new String[]{"Selected Events", "None", "All Events"});
		adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				String selectedItem = parent.getItemAtPosition(position).toString();
				Toast.makeText(AppSettingsActivity.this,
						"Selected notification preference: " + selectedItem,
						Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				spinner.setSelection(adapter.getPosition("Selected Events"));
				Toast.makeText(AppSettingsActivity.this,
						"Notification preference reverted to default: Selected Events",
						Toast.LENGTH_SHORT).show();
			}
		});
	}
}
