package com.example.droiddesign.model;


import android.util.Log;


import androidx.annotation.NonNull;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.HashMap;


public class EventDB {
    private FirebaseFirestore eventDb;
    private CollectionReference eventCollection;


    public EventDB(FirebaseFirestore eventDb){
        this.eventDb = eventDb;
        this.eventCollection = eventDb.collection("events");
    }
    public void addEvent(Event event) {


        HashMap<String, Object> data = (HashMap<String, Object>) event.toMap();


        eventCollection.document(event.getHashId())
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


    public void editEvent(Event event, HashMap<String, Object>updates){


        eventCollection= eventDb.collection("events");


        DocumentReference eventDoc = eventCollection.document(event.getHashId());
        eventDoc.update(updates)
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


    public void deleteEvent(Event event){
        eventCollection.document(event.getHashId()).delete();
    }


}
