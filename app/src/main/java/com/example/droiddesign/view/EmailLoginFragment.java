package com.example.droiddesign.view;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;


import com.example.droiddesign.R;
import com.example.droiddesign.model.User;
import com.example.droiddesign.model.UsersDB;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class EmailLoginFragment extends DialogFragment{
    private FirebaseAuth mAuth;
    private EditText email;
    private EditText password;


    interface UserLoginListener{
        void userLoggedIn();
        void userFailedLogin();
    }
    private UserLoginListener listener;

    @Override
    public void onAttach(@NonNull Context context){
        super.onAttach(context);
        if(context instanceof UserLoginListener){
            listener = (UserLoginListener) context;
        } else {
            throw new RuntimeException(context+" must implement UserLoginListener");
        }

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();

        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_login_email, null);

        email = view.findViewById(R.id.edit_login_email);

        password = view.findViewById(R.id.edit_login_password);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .setTitle("Log In")
                .setNegativeButton("Cancel",null)
                .setPositiveButton("Enter",(dialog,which)->{
                    String useremail = email.getText().toString();
                    String userpassword = password.getText().toString();
                    LogInUser(useremail,userpassword);
                }).create();


    }


    private void LogInUser(String email, String password){
        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser user = mAuth.getCurrentUser();
                            listener.userLoggedIn();
                        } else {
                            listener.userFailedLogin();
                        }
                    }

                });
    }


}

