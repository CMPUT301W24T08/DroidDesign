package com.example.droiddesign.view;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.droiddesign.R;
import com.example.droiddesign.model.User;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CurrentAttendanceActivity extends AppCompatActivity {
    private RecyclerView attendanceListView;
    private UserListAdapter attendanceListAdapter;

    List<User> users = new ArrayList<>(); // Initialize the list
    private HashMap<String, Integer> checkedInUsers = new HashMap<>(); // Initialize the map
    private String eventId;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_attendance);

        firestore = FirebaseFirestore.getInstance();
        eventId = getIntent().getStringExtra("EVENT_ID");

        // What you need to track current attendance and milestone
        TextView milestoneTextView = findViewById(R.id.milestone_textview);
        TextView totalSignUpTextView = findViewById(R.id.total_sign_up_textview);
        TextView checkInsTextView = findViewById(R.id.check_ins_textview);
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

    private void retrieveEventAndAttendanceList() {
        firestore.collection("EventsDB").document(eventId).get().addOnCompleteListener(task -> {
            try {
                if (task.isSuccessful() && task.getResult() != null) {
                    HashMap<String, Integer> checkedInUsersMap = (HashMap<String, Integer>) task.getResult().get("checkedInUsers");
                    if (checkedInUsersMap != null) {
                        checkedInUsers.clear();
                        checkedInUsers.putAll(checkedInUsersMap);
                        users.clear(); // Clear the existing users before fetching new ones

                        for (String userId : checkedInUsersMap.keySet()) {
                            firestore.collection("Users").document(userId).get().addOnCompleteListener(userTask -> {
                                try {
                                    if (userTask.isSuccessful() && userTask.getResult() != null) {
                                        User user = userTask.getResult().toObject(User.class);
                                        if (user != null) {
                                            users.add(user);
                                            // Update the adapter's dataset and refresh the RecyclerView
                                            if (users.size() == checkedInUsersMap.keySet().size()) {
                                                attendanceListAdapter.notifyDataSetChanged();
                                            }
                                        }
                                    } else {
                                        Toast.makeText(this, "Failed to load user details.", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(this, "An error occurred while processing user data.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                } else {
                    Toast.makeText(this, "Failed to load event data.", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "An error occurred while fetching event data.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
