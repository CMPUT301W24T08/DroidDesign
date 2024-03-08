package com.example.droiddesign.model;


import android.util.Log;


import androidx.annotation.NonNull;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import org.w3c.dom.Document;

import java.util.HashMap;


/**
 * Provides functionality to interact with the 'Users' collection in Firestore.
 */
public class UsersDB {
    private FirebaseFirestore usersDB;
    private CollectionReference userCollection;

    /**
     * Constructs a UsersDB object to interact with the Firestore users collection.
     *
     * @param usersDB The instance of FirebaseFirestore to use.
     */
    public UsersDB(FirebaseFirestore usersDB) {
        this.usersDB = usersDB;
        this.userCollection = usersDB.collection("Users");
    }

    /**
     * Adds a new user to the Firestore database.
     *
     * @param user The User object to add to Firestore.
     */
    public void addUser(User user) {
        HashMap<String, Object> data = user.toMap();
        userCollection.document(user.getUserId())
                .set(data)
                .addOnSuccessListener(unused -> Log.d("Firestore", "DocumentSnapshot successfully written"))
                .addOnFailureListener(e -> Log.d("Firestore", "DocumentSnapshot failed to write"));
    }

    /**
     * Updates the details of an existing user in Firestore.
     *
     * @param user The User object to update.
     * @param updates A map containing the fields to update.
     */
    public void editUser(User user, HashMap<String, Object> updates) {
        DocumentReference userDoc = userCollection.document(user.getUserId());
        userDoc.update(updates)
                .addOnSuccessListener(unused -> Log.d("Firestore", "DocumentSnapshot successfully updated"))
                .addOnFailureListener(e -> Log.d("Firestore", "Error updating document"));
    }

    /**
     * Deletes a user from Firestore.
     *
     * @param user The User object to delete.
     */
    public void deleteUser(User user) {
        userCollection.document(user.getUserId()).delete();
    }

    /**
     * Fetches a user from Firestore based on the user ID and executes a callback based on the result.
     *
     * @param userId The ID of the user to fetch.
     * @param callback The callback to execute once the user is fetched or if there is an error.
     */
    public void getUser(String userId, getUserCallback callback) {
        DocumentReference docRef = userCollection.document(userId);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            User user = null;
            String role = documentSnapshot.getString("role");
            switch (role) {
                case "Admin":
                    user = documentSnapshot.toObject(Admin.class);
                    break;
                case "Organizer":
                    user = documentSnapshot.toObject(Organizer.class);
                    break;
                case "Attendee":
                    user = documentSnapshot.toObject(Attendee.class);
                    break;
            }
            callback.onSuccess(user);
        }).addOnFailureListener(e -> callback.onFailure());
    }

    /**
     * Callback interface for fetching a user from Firestore.
     */
    interface getUserCallback {
        /**
         * Called when the user is successfully fetched from Firestore.
         *
         * @param user The User object fetched.
         */
        void onSuccess(User user);

        /**
         * Called when there is a failure in fetching the user.
         */
        void onFailure();
    }
}
