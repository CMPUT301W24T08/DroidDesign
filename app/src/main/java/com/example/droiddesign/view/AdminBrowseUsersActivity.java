package com.example.droiddesign.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.droiddesign.R;
import com.example.droiddesign.model.User;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AdminBrowseUsersActivity extends AppCompatActivity {
    private RecyclerView usersRecyclerView;
    private UserListAdapter usersListAdapter;
    List<User> users = new ArrayList<>();
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_browse_users);

        try {
            firestore = FirebaseFirestore.getInstance();
            usersRecyclerView = findViewById(R.id.admin_users_recyclerview);
            usersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

            // Initialize the adapter with the users list
            usersListAdapter = new UserListAdapter(users, null, user -> {
                try {
                    Intent detailIntent = new Intent(AdminBrowseUsersActivity.this, ProfileSettingsActivity.class);
                    detailIntent.putExtra("USER_ID", user.getUserId());
                    startActivity(detailIntent);
                } catch (Exception e) {
                    Toast.makeText(AdminBrowseUsersActivity.this, "Error opening user details.", Toast.LENGTH_SHORT).show();
                }
            });
            usersRecyclerView.setAdapter(usersListAdapter);

            fetchAllUsers();
        } catch (Exception e) {
            Toast.makeText(this, "Error initializing user list.", Toast.LENGTH_SHORT).show();
        }

        ImageButton backButton = findViewById(R.id.button_back);
        backButton.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            fetchAllUsers();
        } catch (Exception e) {
            Toast.makeText(this, "Error refreshing user list.", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchAllUsers() {
        try {
            firestore.collection("Users").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    List<User> fetchedUsers = task.getResult().toObjects(User.class);
                    users.clear();
                    users.addAll(fetchedUsers);
                    usersListAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(AdminBrowseUsersActivity.this, "Failed to load users.", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, "Error fetching users from database.", Toast.LENGTH_SHORT).show();
        }
    }
}
