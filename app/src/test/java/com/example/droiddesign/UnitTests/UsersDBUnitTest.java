package com.example.droiddesign.UnitTests;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.droiddesign.model.User;
import com.example.droiddesign.model.UsersDB;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;

public class UsersDBUnitTest {

    @Mock
    private FirebaseFirestore mockedFirestore;

    @Mock
    private CollectionReference mockedCollection;

    @Mock
    private DocumentReference mockedDocument;

    private AutoCloseable closeable;

    private UsersDB usersDB;

    @Before
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this); // Initialize mocks and keep the closeable for later

        Task<Void> mockTask = mock(Task.class);
        when(mockTask.addOnSuccessListener(any())).thenReturn(mockTask); // Return the same mock Task for chaining
        when(mockTask.addOnFailureListener(any())).thenReturn(mockTask); // Return the same mock Task for chaining

        // Use the same mockTask for set, update, and delete operations
        when(mockedDocument.set(any())).thenReturn(mockTask); // Use the mock Task with chaining behavior for set
        when(mockedDocument.update(any(HashMap.class))).thenReturn(mockTask);
        when(mockedDocument.delete()).thenReturn(mockTask);

        // Setup the mock behavior for Firestore
        when(mockedFirestore.collection("Users")).thenReturn(mockedCollection);
        when(mockedCollection.document(any(String.class))).thenReturn(mockedDocument);

        usersDB = new UsersDB(mockedFirestore);
    }

    @After
    public void tearDown() throws Exception {
        closeable.close(); // Close the resources after tests are done
    }

    @Test
    public void testAddUser() {
        // Mock user object to be added
        User mockUser = mock(User.class);
        when(mockUser.getUserId()).thenReturn("testUserId");

        // Call the method to be tested
        usersDB.addUser(mockUser);

        // Verify that Firestore's document() and set() methods were called with the correct arguments
        verify(mockedCollection).document("testUserId");
        verify(mockedDocument).set(any(HashMap.class));
    }

    @Test
    public void testEditUser() {
        // Mock user object to be edited
        User mockUser = mock(User.class);
        when(mockUser.getUserId()).thenReturn("testUserId");

        // Mock updates
        HashMap<String, Object> updates = new HashMap<>();
        updates.put("key", "value");

        // Call the method to be tested
        usersDB.editUser(mockUser, updates);

        // Verify that Firestore's document() and update() methods were called with the correct arguments
        verify(mockedCollection).document("testUserId");
        verify(mockedDocument).update(updates);
    }

    @Test
    public void testDeleteUser() {
        // Mock user object to be deleted
        User mockUser = mock(User.class);
        when(mockUser.getUserId()).thenReturn("testUserId");

        // Call the method to be tested
        usersDB.deleteUser(mockUser);

        // Verify that Firestore's document() and delete() methods were called with the correct arguments
        verify(mockedCollection).document("testUserId");
        verify(mockedDocument).delete();
    }
}