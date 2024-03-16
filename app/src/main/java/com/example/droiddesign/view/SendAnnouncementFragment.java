package com.example.droiddesign.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.example.droiddesign.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SendAnnouncementFragment extends Fragment {
	private EditText announcementEditText;
	private Button sendButton;

	private FirebaseFirestore firestore;
	private CollectionReference attendeeListRef;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		firestore = FirebaseFirestore.getInstance();
		attendeeListRef = firestore.collection("events").document("eventid").collection("attendeeList");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_send_announcement, container, false);
		announcementEditText = view.findViewById(R.id.announcement_edit_text);
		sendButton = view.findViewById(R.id.send_button);
		sendButton.setOnClickListener(v -> sendAnnouncement());
		return view;
	}

	private void sendAnnouncement() {
		String announcement = announcementEditText.getText().toString();
		if (!announcement.isEmpty()) {
			Map<String, Object> announcementData = new HashMap<>();
			announcementData.put("announcement", announcement);
			attendeeListRef.document("announcement").set(announcementData)
					.addOnSuccessListener(aVoid -> {
						// Notification sent successfully
						announcementEditText.setText("");
					})
					.addOnFailureListener(e -> {
						// Failed to send notification
					});
		}
	}
}
