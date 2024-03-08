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


public class UsersDB {
    private FirebaseFirestore usersDB;
    private CollectionReference userCollection;


    public UsersDB(FirebaseFirestore usersDB){
        this.usersDB = usersDB;
        this.userCollection = usersDB.collection("Users");
    }
    public void addUser(User user) {

        HashMap<String, Object> data = user.toMap();

        userCollection.document(user.getUserId())
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("Firestore","DocumentSnapshot successfully written");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Firestore","DocumentSnapshot failed to write");
                    }
                });
    }


    public void editUser(User user, HashMap<String, Object>updates){

        DocumentReference userDoc = userCollection.document(user.getUserId());
        userDoc.update(updates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("Firestore","DocumentSnapshot successfully written");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Firestore","DocumentSnapshot failed to write");
                    }
                });

    }


    public void deleteUser(User user){
        userCollection.document(user.getUserId()).delete();
    }

    public void getUser(String userId, getUserCallback callback) {
        DocumentReference docRef = userCollection.document(userId);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = null;
                String role = documentSnapshot.getString("role");
                if(role.equals("Admin")){
                    user = documentSnapshot.toObject(Admin.class);
                } else if (role.equals("Organizer")) {
                    user = documentSnapshot.toObject(Organizer.class);
                } else if (role.equals("Attendee")){
                    user = documentSnapshot.toObject(Attendee.class);
                }
                callback.onSuccess(user);
            }
        });
    }

    interface getUserCallback{
        void onSuccess(User user);
        void onFailure();
    }
}