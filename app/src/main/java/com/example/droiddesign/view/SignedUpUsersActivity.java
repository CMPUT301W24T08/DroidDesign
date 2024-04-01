package com.example.droiddesign.view;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.droiddesign.R;
import com.example.droiddesign.model.User;
import com.example.droiddesign.view.UserListAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class SignedUpUsersActivity extends AppCompatActivity {
    private RecyclerView usersRecyclerView;
    private UserListAdapter usersListAdapter;
    List<User> users = new ArrayList<>();
    private String eventId;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signed_up_users);

        try {
            firestore = FirebaseFirestore.getInstance();
            eventId = getIntent().getStringExtra("EVENT_ID");

            usersRecyclerView = findViewById(R.id.signup_recyclerview);
            usersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

            usersListAdapter = new UserListAdapter(users, null, user -> {
                // Handle user item click event if necessary
            });
            usersRecyclerView.setAdapter(usersListAdapter);

            retrieveAttendees();

            ImageButton backButton = findViewById(R.id.button_back);
            backButton.setOnClickListener(v -> {
                finish();
            });
        } catch (Exception e) {
            Toast.makeText(this, "An error occurred during initialization: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void retrieveAttendees() {
        firestore.collection("EventsDB").document(eventId).get().addOnCompleteListener(task -> {
            try {
                if (task.isSuccessful() && task.getResult() != null) {
                    List<String> attendeeList = (List<String>) task.getResult().get("attendeeList");
                    if (attendeeList != null) {
                        users.clear(); // Clear the existing users before fetching new ones

                        for (String userId : attendeeList) {
                            firestore.collection("Users").document(userId).get().addOnCompleteListener(userTask -> {
                                try {
                                    if (userTask.isSuccessful() && userTask.getResult() != null) {
                                        User user = userTask.getResult().toObject(User.class);
                                        if (user != null) {
                                            users.add(user);
                                            // Update the adapter's dataset and refresh the RecyclerView
                                            usersListAdapter.notifyDataSetChanged();
                                        }
                                    } else {
                                        Toast.makeText(this, "Failed to load user details.", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (Exception e) {
                                    Toast.makeText(this, "Error processing user data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                } else {
                    Toast.makeText(this, "Failed to load event data.", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(this, "Error fetching attendees: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
