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
 * EmailLoginFragment provides a dialog for users to log in using their email and password.
 * On successful login, it triggers the userLoggedIn callback; on failure, it triggers the userFailedLogin callback.
 */
public class EmailLoginFragment extends DialogFragment{

    /**
     * Instance of FirebaseAuth used for authentication purposes.
     */
    private FirebaseAuth mAuth;

    /**
     * EditText field for user email input.
     */
    private EditText email;

    /**
     * EditText field for user password input.
     */
    private EditText password;

    /**
     * Interface for communicating login success or failure back to the activity.
     */
    interface UserLoginListener{
        void userLoggedIn();
        void userFailedLogin();
    }

    /**
     * Listener to communicate login events back to the hosting activity.
     */

    private UserLoginListener listener;

    /**
     * Attaches the fragment to its context and initializes the listener for user login actions.
     * @param context The context in which the fragment is running.
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
     * Creates the dialog view for user login, setting up fields for email and password input and defining positive and negative actions.
     * @param savedInstanceState A Bundle containing the fragment's previously saved state.
     * @return A new AlertDialog instance for user login.
     */
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

    /**
     * Attempts to log in the user with the provided email and password, informing the activity of success or failure through the defined interface.
     * @param email The email input from the user.
     * @param password The password input from the user.
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