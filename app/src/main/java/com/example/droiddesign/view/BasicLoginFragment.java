package com.example.droiddesign.view;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.droiddesign.R;
import com.example.droiddesign.model.SharedPreferenceHelper;
import com.example.droiddesign.model.User;
import com.example.droiddesign.model.UsersDB;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * The BasicLoginFragment class allows users to create a new account with email, password, and role.
 * It communicates user creation status back to the attached activity using the UserCreationListener interface.
 */
public class BasicLoginFragment extends DialogFragment {

    /**
     * Listener interface for communication with the activity that hosts this fragment.
     */

    interface UserCreationListener{
        void userCreated();
    }

    /**
     * FirebaseAuth instance to handle user authentication.
     */
    public FirebaseAuth mAuth;

    private EditText editUserName;
    private EditText editEmail;
    private EditText editCompany;
    private EditText editPhoneNumber;
    private Spinner roleSpinner;
    private Button createAccountButton;
    private Button skipButton;


    /**
     * The activity that implements UserCreationListener for callback purposes.
     */
    private UserCreationListener listener;
    private SharedPreferenceHelper prefsHelper;


    /**
     * Called when the fragment is first attached to its context. Ensures that the context implements
     * the required UserCreationListener interface.
     *
     * @param context The context the fragment is attached to.
     */
    @Override
    public void onAttach(@NonNull Context context){
        super.onAttach(context);
        if(context instanceof UserCreationListener){
            listener = (UserCreationListener) context;
        } else {
            throw new RuntimeException(context+" must implement UserCreationListener");
        }
        prefsHelper = new SharedPreferenceHelper(requireContext());

    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_sign_up, container, false);
        if (view != null) {
            Dialog dialog = getDialog();
            if (dialog != null && dialog.getWindow() != null) {
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            }
        }
        // Initialize views
	    assert view != null;
	    editUserName = view.findViewById(R.id.edit_user_name);
        editEmail = view.findViewById(R.id.edit_email);
        editCompany = view.findViewById(R.id.edit_company);
        editPhoneNumber = view.findViewById(R.id.edit_phone_number);
        roleSpinner = view.findViewById(R.id.spinner_role);
        createAccountButton = view.findViewById(R.id.button_create_account);
        skipButton = view.findViewById(R.id.skip_account_creation);


        // Set up the role spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.role_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(adapter);

        // Set click listener for create account button
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get user input from EditText fields
                String userName = editUserName.getText().toString().trim();
                String email = editEmail.getText().toString().trim();
                String company = editCompany.getText().toString().trim();
                String phoneNumber = editPhoneNumber.getText().toString().trim();
                String role = roleSpinner.getSelectedItem().toString();

                // Perform validation or other operations with the user input TODO: maybe error checking
                // ...
                // Call the createUser method to authenticate with Firebase and save the user details to Firestore
                createUser(userName, email, role, company, phoneNumber);
                // Show a toast message as an example
                Toast.makeText(getContext(), "Account created successfully!", Toast.LENGTH_SHORT).show();

                // Close the dialog
                dismiss();
            }
        });

        // Set click listener for skip button
        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the activity and start a new activity
                Intent intent = new Intent(getActivity(), RoleSelectionActivity.class);
                startActivity(intent);
                requireActivity().finish();//getActivity().finish();
            }
        });

        return view;
    }

    /**
     * Creates the dialog instance for the login fragment, initializing the authentication handler,
     * user input fields, and setting up the dialog interface.
     *
     * @param savedInstanceState If the fragment is being re-created from a previous saved state, this is the state.
     * @return The AlertDialog for user login.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        return dialog;
    }

    /**
     * Creates a new user account with Firebase anonymoous and adds the user information to Firestore.
     *
     * @param userName    The user's email address.
     * @param email    The user's chosen password.
     * @param role     The user's selected role.
     * @param company    The user's email address.
     * @param phoneNumber The user's chosen password.
     */


    public void createUser(String userName, String email, String role, String company, String phoneNumber){
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        UsersDB userdb = new UsersDB(firestore);
        mAuth.signInAnonymously()
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser user = mAuth.getCurrentUser();
                            User newUser = null;
                            if (user != null) {
                                newUser = new User(user.getUid(), role);
                                newUser.setUserName(userName);
                                newUser.setEmail(email);
                                newUser.setRegistered(String.valueOf(true));
                                newUser.setCompany(company);
                                newUser.setPhone(phoneNumber);
                                // Save user profile to SharedPreferences
                                prefsHelper.saveUserProfile(user.getUid(), role, email);
                            }
	                        assert newUser != null;
	                        userdb.addUser(newUser);
                            listener.userCreated();
                            // Close the dialog
                            dismiss();
                            // Close the dialog, finish the activity, and start a new activity
                            Intent intent = new Intent(getActivity(), EventMenuActivity.class);
                            startActivity(intent);
                            requireActivity().finish();
                        }

                    }
                });
    }
}