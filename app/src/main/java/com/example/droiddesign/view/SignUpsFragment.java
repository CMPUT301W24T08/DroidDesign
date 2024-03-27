package com.example.droiddesign.view;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.droiddesign.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class SignUpsActivity extends AppCompatActivity {
	private RecyclerView attendanceListView;
	private RecyclerView.Adapter attendanceListAdapter;

	private List<String> attendanceList;
	private String eventId;
	private FirebaseFirestore firestore;
	private CollectionReference attendeeListRef;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_sign_ups);

		// Initialize Firestore and the references
		firestore = FirebaseFirestore.getInstance();
		eventId = getIntent().getStringExtra("EVENT_ID");
		attendeeListRef = firestore.collection("EventsDB").document(eventId).collection("attendeeList");

		// Initialize the RecyclerView
		attendanceListView = findViewById(R.id.signup_recyclerview);
		attendanceListView.setLayoutManager(new LinearLayoutManager(this));
		attendanceList = new ArrayList<>();
		attendanceListAdapter = new UserListAdapter(attendanceList);
		attendanceListView.setAdapter(attendanceListAdapter);

		retrieveAttendanceList();
	}

	private void retrieveAttendanceList() {
		attendeeListRef.get().addOnCompleteListener(task -> {
			if (task.isSuccessful() && task.getResult() != null) {
				for (DocumentSnapshot document : task.getResult().getDocuments()) {
					String userName = document.getString("userName");
					if (userName != null) {
						attendanceList.add(userName);
					}
				}
				attendanceListAdapter.notifyDataSetChanged();
			} else {
				Toast.makeText(SignUpsActivity.this, "Failed to load attendance list.", Toast.LENGTH_SHORT).show();
			}
		});
	}
}
