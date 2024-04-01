package com.example.droiddesign.view;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.example.droiddesign.controller.MessageEvent;
import com.example.droiddesign.model.SharedPreferenceHelper;
import com.example.droiddesign.model.User;
import com.example.droiddesign.model.UsersDB;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class BasicLoginFragment extends DialogFragment {

    public FirebaseAuth mAuth;
    private EditText editUserName;
    private EditText editEmail;
    private EditText editCompany;
    private EditText editPhoneNumber;
    private Spinner roleSpinner;
    private UserCreationListener listener;
    private SharedPreferenceHelper prefsHelper;
    private String profilePicUrl;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        profilePicUrl = event.getMessage();
        Log.d("BasicLoginFragment", "Received profile picture URL: " + profilePicUrl);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof UserCreationListener) {
            listener = (UserCreationListener) context;
        } else {
            throw new RuntimeException(context + " must implement UserCreationListener");
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
        assert view != null;
        initializeViews(view);
        registerEventBus();
        setupListeners(view);

        return view;
    }



    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        return dialog;
    }

    public void createUser(String userName, String email, String role, String company, String phoneNumber) {
        try {
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            UsersDB userdb = new UsersDB(firestore);
            mAuth.signInAnonymously()
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                User newUser = null;
                                if (user != null) {
                                    newUser = new User(user.getUid(), role);
                                    newUser.setUserName(userName);
                                    newUser.setEmail(email);
                                    newUser.setRegistered(String.valueOf(true));
                                    newUser.setCompany(company);
                                    newUser.setPhone(phoneNumber);

                                    profilePicUrl = determineProfilePicUrl(userName);

                                    newUser.setProfilePic(profilePicUrl);
                                    prefsHelper.saveUserProfile(user.getUid(), role, email);
                                }

                                assert newUser != null;
                                userdb.addUser(newUser);
                                listener.userCreated();
                                navigateToEventMenu();
                            } else {
                                Log.e("BasicLoginFragment", "Authentication failed.", task.getException());
                                Toast.makeText(getContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } catch (Exception e) {
            Log.e("BasicLoginFragment", "Error creating user", e);
            Toast.makeText(getContext(), "Error creating account", Toast.LENGTH_SHORT).show();
        }
    }

    private void initializeViews(View view) {
        editUserName = view.findViewById(R.id.edit_user_name);
        editEmail = view.findViewById(R.id.edit_email);
        editCompany = view.findViewById(R.id.edit_company);
        editPhoneNumber = view.findViewById(R.id.edit_phone_number);
        roleSpinner = view.findViewById(R.id.spinner_role);
    }

    private void registerEventBus() {
        try {
            EventBus.getDefault().register(this);
        } catch (Exception e) {
            Log.e("BasicLoginFragment", "Error registering EventBus", e);
        }
    }

    private void setupListeners(View view) {
        Button createAccountButton = view.findViewById(R.id.button_create_account);
        Button skipButton = view.findViewById(R.id.skip_account_creation);
        Button profilePicButton = view.findViewById(R.id.button_profile_picture);

        createAccountButton.setOnClickListener(v -> {
            try {
                String userName = editUserName.getText().toString().trim();
                String email = editEmail.getText().toString().trim();
                String company = editCompany.getText().toString().trim();
                String phoneNumber = editPhoneNumber.getText().toString().trim();
                String role = roleSpinner.getSelectedItem().toString();

                createUser(userName, email, role, company, phoneNumber);
                Toast.makeText(getContext(), "Account created successfully!", Toast.LENGTH_SHORT).show();
                dismiss();
            } catch (Exception e) {
                Log.e("BasicLoginFragment", "Error creating user account", e);
                Toast.makeText(getContext(), "Error creating account", Toast.LENGTH_SHORT).show();
            }
        });

        profilePicButton.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(getActivity(), AddProfilePictureActivity.class);
                intent.putExtra("image_url", profilePicUrl);
                startActivity(intent);
            } catch (Exception e) {
                Log.e("BasicLoginFragment", "Error opening profile picture activity", e);
                Toast.makeText(getContext(), "Error setting profile picture", Toast.LENGTH_SHORT).show();
            }
        });

        skipButton.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(getActivity(), RoleSelectionActivity.class);
                startActivity(intent);
                requireActivity().finish();
            } catch (Exception e) {
                Log.e("BasicLoginFragment", "Error skipping account creation", e);
                Toast.makeText(getContext(), "Error skipping account creation", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String determineProfilePicUrl(String userName) {
        try {
            if (profilePicUrl == null || profilePicUrl.isEmpty()) {
                String initials = getInitials(userName);
                return "https://ui-avatars.com/api/?name=" + initials + "&background=random";
            }
            return profilePicUrl;
        } catch (Exception e) {
            Log.e("BasicLoginFragment", "Error determining profile picture URL", e);
            return "";
        }
    }

    private String getInitials(String userName) {
        try {
            if (userName == null || userName.isEmpty()) {
                return "XX"; // Return some default initials
            }

            String[] nameParts = userName.split(" ");
            StringBuilder initials = new StringBuilder();
            for (String part : nameParts) {
                if (!part.isEmpty()) {
                    initials.append(part.charAt(0));
                }
            }

            return initials.toString().toUpperCase();
        } catch (Exception e) {
            Log.e("BasicLoginFragment", "Error getting initials for profile picture", e);
            return "XX"; // Return some default initials in case of an error
        }
    }

    private void navigateToEventMenu() {
        try {
            if (isAdded() && getActivity() != null) {
                Intent intent = new Intent(getActivity(), EventMenuActivity.class);
                startActivity(intent);
                requireActivity().finish();
            }
        } catch (Exception e) {
            Log.e("BasicLoginFragment", "Error navigating to event menu", e);
            Toast.makeText(getContext(), "Error navigating to event menu", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            EventBus.getDefault().unregister(this);
        } catch (Exception e) {
            Log.e("BasicLoginFragment", "Error unregistering EventBus", e);
        }
    }

    interface UserCreationListener {
        void userCreated();
    }
}
