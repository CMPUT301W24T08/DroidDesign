package com.example.droiddesign.controller;

import com.example.droiddesign.model.User;
import com.example.droiddesign.model.UsersDB;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Controller class responsible for fetching user data from the database.
 * This class is an intermediary between the model (UsersDB) and the view.
 * It handles the logic to retrieve user data and notify the view through a listener interface.
 */
public class UserController {
    private final UsersDB usersDB;
    private UserDataListener listener;

    /**
     * Constructs a UserController with a specific instance of FirebaseFirestore.
     *
     * @param firestore An instance of FirebaseFirestore used to initialize the UsersDB model.
     */
    public UserController(FirebaseFirestore firestore) {
        this.usersDB = new UsersDB(firestore);
    }

    /**
     * Sets a listener to receive callbacks related to user data fetching operations.
     *
     * @param listener The UserDataListener to be notified of user data fetching results.
     */
    public void setUserDataListener(UserDataListener listener) {
        this.listener = listener;
    }

    /**
     * Initiates an asynchronous operation to fetch user data from the database.
     * The results are communicated back to the registered UserDataListener.
     *
     * @param userId The unique identifier of the user whose data is to be fetched.
     */
    public void fetchUserData(String userId) {
        usersDB.getUser(userId, new UsersDB.getUserCallback() {
            @Override
            public void onSuccess(User user) {
                if (listener != null) {
                    listener.onUserDataFetched(user);
                }
            }

            @Override
            public void onFailure() {
                if (listener != null) {
                    listener.onUserDataFetchFailed();
                }
            }
        });
    }

    /**
     * Interface for receiving notifications about the results of user data fetching operations.
     */
    public interface UserDataListener {

        /**
         * Called when user data has been successfully fetched from the database.
         *
         * @param user The User object containing the fetched user data.
         */
        void onUserDataFetched(User user);

        /**
         * Called when an attempt to fetch user data from the database has failed.
         */
        void onUserDataFetchFailed();
    }
}
