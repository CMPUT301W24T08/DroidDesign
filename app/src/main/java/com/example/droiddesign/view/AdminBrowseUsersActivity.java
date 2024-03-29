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

        firestore = FirebaseFirestore.getInstance();

        usersRecyclerView = findViewById(R.id.admin_users_recyclerview);
        usersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the adapter with the users list
        usersListAdapter = new UserListAdapter(users, null, user -> {
            Intent detailIntent = new Intent(AdminBrowseUsersActivity.this, ProfileSettingsActivity.class);
            detailIntent.putExtra("USER_ID", user.getUserId());
            startActivity(detailIntent);
        });
        usersRecyclerView.setAdapter(usersListAdapter);

        fetchAllUsers();

        ImageButton backButton = findViewById(R.id.button_back);
        backButton.setOnClickListener(v -> {
            finish();
        });
    }

    protected void onResume() {
        super.onResume();
        fetchAllUsers();
    }

    private void fetchAllUsers() {
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
    }
}
