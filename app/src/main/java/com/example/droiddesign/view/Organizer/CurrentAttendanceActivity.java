package com.example.droiddesign.view.Organizer;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.droiddesign.R;
import com.example.droiddesign.model.User;
import com.example.droiddesign.view.Adapters.UserListAdapter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CurrentAttendanceActivity extends AppCompatActivity implements OnMapReadyCallback {

    /**
     * GoogleMap instance for displaying the event's check-in locations.
     */
    private GoogleMap map;

    /**
     * RecyclerView for displaying the list of attendees.
     */
    private RecyclerView attendanceListView;

    /**
     * Adapter for the attendance list RecyclerView.
     */
    private UserListAdapter attendanceListAdapter;

    /**
     * List to store the user data fetched from Firestore.
     */
    List<User> users = new ArrayList<>(); // Initialize the list

    /**
     * Map to store the number of check-ins per user, with user ID as key and check-in count as value.
     */
    private HashMap<String, Integer> checkedInUsers = new HashMap<>(); // Initialize the map

    /**
     * Event ID for which the attendance is being displayed.
     */
    private String eventId;

    /**
     * FirebaseFirestore instance for database interaction.
     */
    private FirebaseFirestore firestore;

    /**
     * TextView to display the number of check-ins.
     */
    TextView checkInsTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_attendance);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_fragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        firestore = FirebaseFirestore.getInstance();
        eventId = getIntent().getStringExtra("EVENT_ID");


        checkInsTextView = findViewById(R.id.current_attendance_textview_number);
        attendanceListView = findViewById(R.id.attendance_list_recycler_view);
        attendanceListView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the adapter with the empty list and map
        attendanceListAdapter = new UserListAdapter(users, checkedInUsers, user -> {
            // Handle user item click event if necessary
        });
        attendanceListView.setAdapter(attendanceListAdapter);

        retrieveEventAndAttendanceList();

        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            finish();
        });
    }

    /**
     * Retrieves the event attendance list from Firestore, including user details and check-in counts.
     * This method queries the "AttendanceDB" collection to find all check-ins associated with the current event.
     * For each check-in found, it fetches the corresponding user's details from the "Users" collection.
     * The method updates the local list of users and their check-in counts, and then refreshes the UI to display this data.
     */
    private void retrieveEventAndAttendanceList() {
        firestore.collection("AttendanceDB")
                .whereEqualTo("event_id", eventId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        checkedInUsers.clear();
                        users.clear(); // Clear the existing users before fetching new ones

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String userId = document.getString("user_id");
                            Long checkInCount = document.getLong("check_in_count");
                            if (userId != null && checkInCount != null) {
                                checkedInUsers.put(userId, checkInCount.intValue());

                                firestore.collection("Users").document(userId).get().addOnCompleteListener(userTask -> {
                                    if (userTask.isSuccessful() && userTask.getResult() != null) {
                                        User user = userTask.getResult().toObject(User.class);
                                        if (user != null) {
                                            users.add(user);
                                            // Update the adapter's dataset and refresh the RecyclerView
                                            if (users.size() == checkedInUsers.size()) {
                                                attendanceListAdapter.notifyDataSetChanged();
                                                checkInsTextView.setText(String.valueOf(users.size()));
                                            }
                                        }
                                    } else {
                                        Toast.makeText(CurrentAttendanceActivity.this, "Failed to load user details.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    } else {
                        Toast.makeText(CurrentAttendanceActivity.this, "Failed to load attendance data.", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    /**
     * Initializes the map and sets up the markers based on the check-in locations of attendees.
     *
     * Code is inspired by and based off of the following:
     * Adding a Map with a Marker, 2024-04-04 https://developers.google.com/maps/documentation/android-sdk/map-with-marker, APACHE 2.0 License for Sample Code
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("CurrentAttendance", "Map is ready");
        this.map = googleMap;
        // Configure the map as needed
        map.getUiSettings().setAllGesturesEnabled(true); // Disable interaction for the preview

        displayCheckInsOnMap(); // Function to display the check-in locations
    }

    /**
     * Displays check-in locations on the map by placing markers at the coordinates of each check-in.
     */
    private void displayCheckInsOnMap() {
        firestore.collection("AttendanceDB")
                .whereEqualTo("event_id", eventId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        if (task.getResult().isEmpty()) {
                            return;
                        }

                        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
                        boolean hasValidLocations = false;  // Flag to track if there are valid locations

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Double latitude = document.getDouble("latitude");
                            Double longitude = document.getDouble("longitude");

                            if (latitude != null && longitude != null) {
                                LatLng location = new LatLng(latitude, longitude);
                                map.addMarker(new MarkerOptions()
                                        .position(location)
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

                                boundsBuilder.include(location);
                                hasValidLocations = true;
                            }
                        }

                        if (hasValidLocations) {
                            LatLngBounds bounds = boundsBuilder.build();
                            map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
                            map.animateCamera(CameraUpdateFactory.zoomTo(10));
                        }
                    } else {
                        Log.e("displayCheckInsOnMap", "Error getting documents: ", task.getException());
                    }
                });
    }


}
