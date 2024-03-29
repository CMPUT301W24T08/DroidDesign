package com.example.droiddesign.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.droiddesign.R;
import com.example.droiddesign.model.SharedPreferenceHelper;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public class SendAnnouncementActivity extends AppCompatActivity {
	private Button sendButton;
	private TextView titleEditText;
	private TextView messageEditText;
	private TextView dateEditText;
	private FirebaseFirestore firestore;
	private CollectionReference attendeeListRef;
	private RecyclerView announcementsRecyclerView;
	private AnnouncementAdapter announcementAdapter;
	private List<Map<String, Object>> announcementList = new ArrayList<>();
	private String userId, userRole,eventId;
	SharedPreferenceHelper prefsHelper;
	private APIService apiService;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_send_announcement);
		prefsHelper = new SharedPreferenceHelper(this);
		String savedUserId = prefsHelper.getUserId();
		if (savedUserId != null) {
			// Use the userId from SharedPreferences
			userId = savedUserId;
			userRole = prefsHelper.getRole();
		} //At this point, user details are valid

		// Inflate the button based on user role
		findViewById(R.id.send_button).setVisibility("organizer".equalsIgnoreCase(userRole) ? View.VISIBLE : View.GONE );
		// Initialize Retrofit and APIService
		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl("https://fcm.googleapis.com/")
				.addConverterFactory(GsonConverterFactory.create())
				.build();
		apiService = retrofit.create(APIService.class);

		eventId = getIntent().getStringExtra("EVENT_ID");
		if (eventId == null || eventId.isEmpty()) {
			Toast.makeText(this, "Event ID is missing.", Toast.LENGTH_LONG).show();
			finish();
			return;
		}

		firestore = FirebaseFirestore.getInstance();
		attendeeListRef = firestore.collection("EventsDB").document(eventId).collection("attendeeList");
		firestore.collection("EventsDB").document(eventId)
				.get()
				.addOnSuccessListener(documentSnapshot -> {
					String organizerOwnerId = documentSnapshot.getString("organizerOwnerId");
					findViewById(R.id.only_owner).setVisibility(organizerOwnerId != null && organizerOwnerId.equals(userId) ? View.VISIBLE : View.GONE);
				})
				.addOnFailureListener(e -> Log.e("VisibilityCheck", "Error", e));
		titleEditText = findViewById(R.id.title_edit_text);
		messageEditText = findViewById(R.id.message_edit_text);
		sendButton = findViewById(R.id.send_button);

		sendButton.setOnClickListener(v -> {
			String title = titleEditText.getText().toString().trim();
			String message = messageEditText.getText().toString().trim();

			if (title.isEmpty() || message.isEmpty()) {
				Toast.makeText(this, "Please write a message.", Toast.LENGTH_SHORT).show();
			} else {
				saveMessage(title, message);
			}
		});
		announcementsRecyclerView = findViewById(R.id.organizer_message_recyclerview);
		announcementsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

		announcementAdapter = new AnnouncementAdapter(announcementList);
		announcementsRecyclerView.setAdapter(announcementAdapter);

		fetchAnnouncements();

		ImageButton backButton = findViewById(R.id.back_button);
		backButton.setOnClickListener(v -> {
			finish();
		});
	}

	private void saveMessage(String title, String message) {
		if (eventId == null || eventId.trim().isEmpty()) {
			Toast.makeText(this, "Event ID is not set.", Toast.LENGTH_SHORT).show();
			return;
		}

		// Create a new message map to be saved
		Map<String, Object> messageMap = new HashMap<>();
		messageMap.put("title", title);
		messageMap.put("message", message);
		messageMap.put("date", new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));

		// Save the message under OrganizerMessages of the specific event
		firestore.collection("EventsDB").document(eventId)
				.update("organizerMessages", FieldValue.arrayUnion(messageMap))
				.addOnSuccessListener(documentReference -> {
					// Clear the input fields after successful save
					titleEditText.setText("");
					messageEditText.setText("");
					// After sending the message, refresh the activity to show updated data
					refreshActivity();
					notifyAttendees(title);
				})
				.addOnFailureListener(e -> Toast.makeText(this, "Failed to send message.", Toast.LENGTH_SHORT).show());
	}

	private void fetchAnnouncements() {
		firestore.collection("EventsDB").document(eventId)
				.get()
				.addOnSuccessListener(documentSnapshot -> {
					if (documentSnapshot.exists() && documentSnapshot.contains("organizerMessages")) {
						List<Map<String, Object>> unsortedMessages = (List<Map<String, Object>>) documentSnapshot.get("organizerMessages");
						if (unsortedMessages != null) {
							// Sort messages by date in descending order
							List<Map<String, Object>> sortedMessages = new ArrayList<>(unsortedMessages);
							sortedMessages.sort((map1, map2) -> ((String) map2.get("date")).compareTo((String) map1.get("date")));

							// Now use sortedMessages to update your RecyclerView
							announcementList.clear();
							announcementList.addAll(sortedMessages);
							announcementAdapter.notifyDataSetChanged();
						}
					}
				})
				.addOnFailureListener(e -> Log.e("FetchAnnouncementsError", "Error loading announcements", e));
	}

	private void refreshActivity() {
		Intent intent = new Intent(this, SendAnnouncementActivity.class);
		intent.putExtra("EVENT_ID", eventId); // Pass the event ID back to the activity
		finish();
		startActivity(intent);
	}

	private void notifyAttendees(String title) {
		firestore.collection("EventsDB").document(eventId).get()
				.addOnSuccessListener(documentSnapshot -> {
					List<String> attendeeList = (List<String>) documentSnapshot.get("attendeeList");
					if (attendeeList != null) {
						// Prepare the list of tokens for the attendees
						List<String> tokens = new ArrayList<>();
						for (String userId : attendeeList) {
							// Fetch each user's token and add it to the tokens list
							firestore.collection("Users").document(userId).get()
									.addOnSuccessListener(userSnapshot -> {
										String token = userSnapshot.getString("fcmToken");
										if (token != null && !token.isEmpty()) {
											tokens.add(token);
											if (tokens.size() == attendeeList.size()) {
												// All tokens are collected, send them to your server/cloud function to dispatch notifications
												sendNotificationsToTokens(title, tokens);
											}
										}
									});
						}
					}
				})
				.addOnFailureListener(e -> Log.e("NotifyAttendees", "Failed to get attendee list for event: " + eventId));
	}
	private void sendNotificationsToTokens(String title, List<String> tokens) {
		// Prepare the payload
		JSONObject payload = new JSONObject();
		try {
			JSONObject notification = new JSONObject();
			notification.put("title", "New message from " + eventId);
			notification.put("body", title);

			payload.put("registration_ids", new JSONArray(tokens));
			payload.put("notification", notification);
		} catch (JSONException e) {
			e.printStackTrace();
			return;
		}

		// Define the MediaType for the request body
		MediaType JSON = MediaType.parse("application/json; charset=utf-8");
		// Create the request body
		RequestBody requestBody = RequestBody.create(JSON, payload.toString());

		// Build the request
		Request request = new Request.Builder()
				.url("https://fcm.googleapis.com/fcm/send")
				.addHeader("Authorization", "key=AAAA1Lwwer4:APA91bHuSelA6Mkvst7R_BZ7Vf2ot9gafIXbpW0e3NyVLAIN60xpGuRRc_QjM0jPYyIT0J4PxBejgGQOo5NuRLfOZn_M9C4m6Pl9uv_CTwKgKRimllR_00ZsVtTHghZ86yAxGuXGUGiE")
				.post(requestBody)
				.build();

		// Create a new OkHttpClient instance
		OkHttpClient client = new OkHttpClient();

		// Asynchronously send the request
		client.newCall(request).enqueue(new Callback() {
			@Override
			public void onFailure(okhttp3.Call call, IOException e) {
				Log.e("sendNotificationsToTokens", "Failed to send notifications", e);
			}

			@Override
			public void onResponse(okhttp3.Call call, Response response) throws IOException {
				if (!response.isSuccessful()) {
					// Handle the failure
					Log.e("sendNotificationsToTokens", "Failed to send notifications: " + response);
				} else {
					// Handle success
					Log.d("sendNotificationsToTokens", "Notifications sent successfully");
				}
			}

		});
	}


	public interface APIService {
		@Headers({
				"Content-Type:application/json",
				"Authorization:key=YOUR_SERVER_KEY" // Replace YOUR_SERVER_KEY with your actual server key from Firebase Console
		})
		@POST("fcm/send")
		Call<MyResponse> sendNotifcation(@Body NotificationSender sender);
	}

	public class MyResponse {
		public int success;
	}
	public class Data {
		private String title;
		private String message;

		public Data(String title, String message) {
			this.title = title;
			this.message = message;
		}

		// Getters and Setters
		public String getTitle() { return title; }
		public void setTitle(String title) { this.title = title; }
		public String getMessage() { return message; }
		public void setMessage(String message) { this.message = message; }
	}

	public class NotificationSender {
		public Data data;
		public String to;

		public NotificationSender(Data data, String to) {
			this.data = data;
			this.to = to;
		}

		// Getters and Setters
		public Data getData() { return data; }
		public void setData(Data data) { this.data = data; }
		public String getTo() { return to; }
		public void setTo(String to) { this.to = to; }
	}

}
