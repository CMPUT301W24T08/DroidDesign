package com.example.droiddesign.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.droiddesign.R;
import com.example.droiddesign.model.Organizer;
import com.example.droiddesign.model.User;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class BasicLoginFragment extends DialogFragment {
    private FirebaseAuth mAuth;
    private EditText email;
    private EditText password;


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_user_login, null);
        email = view.findViewById(R.id.edit_text_username_text);
        password = view.findViewById(R.id.editTextTextPassword);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .setTitle("Sign Up")
                .setNegativeButton("Cancel",null)
                .setPositiveButton("Add",(dialog,which)->{
                    String useremail = email.getText().toString();
                    String userpassword = password.getText().toString();
                    createUser(useremail,userpassword);
                }).create();

    }

    private void createUser(String email, String password){
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser user = mAuth.getCurrentUser();
                        }
                    }
                });
    }
}
