package com.example.droiddesign.model;


import android.util.Log;


import androidx.annotation.NonNull;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


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


}
