package com.example.droiddesign.model;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class User {
	protected String userId;
	protected String userName;
	protected Boolean registered;
	private String role;
	protected String email;
	protected String phone;
	private double latitude;
	private double longitude;
	private List<String> eventsList;

	// Firestore collection name
	private static final String COLLECTION_NAME = "User";
	protected ArrayList<String> rolesList;
	// Firestore instance
	protected transient FirebaseFirestore db = FirebaseFirestore.getInstance();

	// Default constructor
	public User() {
	}

	// Default constructor

	public User(String userId, String role) {
		this.userId = userId;
		this.role = role;
	}

	/*
		constructor for unregistered users
		 */
	public User(String userId, String role, Boolean registered) {
		this.userId = userId;
		this.role = role;
		this.registered = registered;
	}
	/*
	constructor for registered users
	 */
	public User(String userId, String userName, String role, Boolean registered, String email, String phone) {
		this.userId = userId;
		this.userName = userName;
		this.registered = registered;
		this.role = role;
		this.email = email;
		this.phone = phone;
	}

	// Method to save unregistered user to Firestore
	public void saveUnregisteredUser() {
		// Create a map to store user data
		Map<String, Object> userData = new HashMap<>();
		userData.put("userID", this.userId);
		userData.put("role", this.role);
		userData.put("Registered", this.registered);
		// TODO: Add Geolocation, AttendedEventsList, and OrganizedEventsList as needed

		// Save to Firestore
		FirebaseFirestore.getInstance().collection(COLLECTION_NAME).document(this.userId).set(userData)
				.addOnSuccessListener(aVoid -> System.out.println("Unregistered user saved successfully"))
				.addOnFailureListener(e -> System.out.println("Error saving unregistered user: " + e.getMessage()));
	}

	// Method to save registered user to Firestore
	public void saveRegisteredUser() {
		// Create a map to store user data
		Map<String, Object> userData = new HashMap<>();
		userData.put("userID", this.userId);
		userData.put("userName", this.userName);
		userData.put("role", this.role);
		userData.put("Registered", this.registered);
		userData.put("email", this.email);
		userData.put("phone", this.phone);
		// TODO: Add Geolocation, AttendedEventsList, and OrganizedEventsList as needed

		// Save to Firestore
		FirebaseFirestore.getInstance().collection(COLLECTION_NAME).document(this.userId).set(userData)
				.addOnSuccessListener(aVoid -> System.out.println("Registered user saved successfully"))
				.addOnFailureListener(e -> System.out.println("Error saving registered user: " + e.getMessage()));
	}

	// Method to load unregistered user from Firestore
	public static void loadUnregisteredUser(String userId, FirestoreCallback callback) {
		FirebaseFirestore.getInstance().collection(COLLECTION_NAME).document(userId).get()
				.addOnSuccessListener(documentSnapshot -> {
					if (documentSnapshot.exists()) {
						User user = documentSnapshot.toObject(User.class);
						callback.onCallback(user);
					} else {
						callback.onCallback(null);
					}
				})
				.addOnFailureListener(e -> {
					// Handle error
					callback.onCallback(null);
				});
	}

	public static Task<User> loadFromFirestore(String userId) {
		FirebaseFirestore db = FirebaseFirestore.getInstance();
		DocumentReference docRef = db.collection("userDB").document(userId);

		// Create a new TaskCompletionSource
		TaskCompletionSource<User> taskCompletionSource = new TaskCompletionSource<>();

		docRef.get().addOnCompleteListener(task -> {
			if (task.isSuccessful() && task.getResult() != null) {
				DocumentSnapshot document = task.getResult();
				User user = document.toObject(User.class);

				if (user == null) { // If the document does not exist or failed to parse
					taskCompletionSource.setException(new Exception("User not found or parsing failed"));
				} else {
					// Successfully constructed User object
					taskCompletionSource.setResult(user);
				}
			} else {
				taskCompletionSource.setException(Objects.requireNonNull(task.getException()));
			}
		});

		return taskCompletionSource.getTask();
	}
	// Method to load the user from Firestore
	public void ladFromFirestore(String userId) {
		DocumentReference docRef = db.collection(COLLECTION_NAME).document(userId);
		docRef.get().addOnCompleteListener(task -> {
			if (task.isSuccessful() && task.getResult() != null) {
				Map<String, Object> data = task.getResult().getData();
				if (data != null) {
					this.userId = (String) data.get("UserID");
					this.userName = (String) data.get("Username");
					this.email = (String) data.get("Email");
					this.phone = (String) data.get("Phone");
					this.registered = (Boolean) data.get("Registered");
					// Handle other fields like Type, Geolocation, AttendedEventsList, and OrganizedEventsList
				}
			} else {
				System.out.println("Error loading user: " + task.getException());
			}
		});
	}

	// Getters and setters
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Boolean getRegistered() {
		return registered;
	}

	public void setRegistered(Boolean registered) {
		this.registered = registered;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public List<String> getEventsList() {
		return eventsList;
	}

	public void setEventsList(List<String> eventsList) {
		this.eventsList = eventsList;
	}

	// Callback interface for asynchronous Firestore operations
	public interface FirestoreCallback {
		void onCallback(User user);
	}
}
