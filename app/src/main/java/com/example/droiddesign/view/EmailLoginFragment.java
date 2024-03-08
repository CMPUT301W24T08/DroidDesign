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

/**
 * A DialogFragment that provides an interface for user login using email and password.
 * It uses Firebase Authentication to sign in the user and verify credentials then communicates the login result
 * back to the hosting activity through the UserLoginListener interface.
 */
public class EmailLoginFragment extends DialogFragment{
    private FirebaseAuth mAuth;
    private EditText email;
    private EditText password;

    /**
     * Interface to be implemented by the LaunchScreen activity to handle login success or failure callbacks.
     */
    interface UserLoginListener{

        /**
         * Called when the user is successfully logged in.
         */
        void userLoggedIn(FirebaseUser user);

        /**
         * Called when the login attempt fails.
         */
        void userFailedLogin();

        void userLoggedIn();
    }
    private UserLoginListener listener;

    /**
     * Called when the fragment is first attached to its context.
     * {@inheritDoc}
     *
     * @throws RuntimeException if the context does not implement UserLoginListener.
     */
    @Override
    public void onAttach(@NonNull Context context){
        super.onAttach(context);
        if(context instanceof UserLoginListener){
            listener = (UserLoginListener) context;
        } else {
            throw new RuntimeException(context+" must implement UserLoginListener");
        }

    }

    /**
     * Creates and returns the dialog for user login.
     * {@inheritDoc}
     *
     * @return A Dialog instance for the user login.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();

        View view = getLayoutInflater().inflate(R.layout.fragment_login_email, null);

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

    /**
     * Attempts to log in the user using the provided email and password.
     *
     * @param email    The user's email address.
     * @param password The user's password.
     */
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

