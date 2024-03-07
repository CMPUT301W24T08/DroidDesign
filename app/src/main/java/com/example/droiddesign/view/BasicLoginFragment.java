package com.example.droiddesign.view;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;


import com.example.droiddesign.R;
import com.example.droiddesign.model.Admin;
import com.example.droiddesign.model.Attendee;
import com.example.droiddesign.model.Organizer;
import com.example.droiddesign.model.User;
import com.example.droiddesign.model.UsersDB;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.ArrayList;


public class BasicLoginFragment extends DialogFragment {
    private FirebaseAuth mAuth;
    private EditText email;
    private EditText password;

    private ArrayList<String> rolesList = new ArrayList<>();

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();

        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_user_login, null);
        rolesList.add("Admin");
        rolesList.add("Organizer");
        rolesList.add("Attendee");
        Spinner spinner = view.findViewById(R.id.spinner_role);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, rolesList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

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
                    String role = (String) spinner.getSelectedItem();
                    createUser(useremail,userpassword,role);
                }).create();


    }


    private void createUser(String email, String password, String role){
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        UsersDB userdb = new UsersDB(firestore);
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser user = mAuth.getCurrentUser();
                            User newUser = null;
                            switch(role) {
                                case "Admin":
                                    newUser = new Admin(user.getUid(), role);
                                    break;
                                case "Attendee":
                                    newUser = new Attendee(user.getUid(), role);
                                    break;
                                case "Organizer":
                                    newUser = new Organizer(user.getUid(), role, user.getEmail());
                                    break;
                                default:
                                    newUser = new Attendee(user.getUid(), role);
                                    break;
                            }
                            userdb.addUser(newUser);


                        }


                    }
                });
    }
}
