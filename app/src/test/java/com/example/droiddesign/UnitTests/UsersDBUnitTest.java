package com.example.droiddesign.UnitTests;

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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UsersDBUnitTest {

    @Mock
    private FirebaseFirestore mockedFirestore;
    @Mock
    private CollectionReference mockedCollection;
    @Mock
    private DocumentReference mockedDocument;
    @Mock
    private Task<Void> mockedTask;

    private AutoCloseable closeable;
    private UsersDB usersDB;

    @Before
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);

        // Create a mock Task<Void> object with Mockito
        mockedTask = mock(Task.class);

        // Ensure that the mocked task returns itself when addOnSuccessListener or addOnFailureListener is called
        when(mockedTask.addOnSuccessListener(any())).thenReturn(mockedTask);
        when(mockedTask.addOnFailureListener(any())).thenReturn(mockedTask);

        // Mock Firestore setup
        when(mockedFirestore.collection("Users")).thenReturn(mockedCollection);
        when(mockedCollection.document(any(String.class))).thenReturn(mockedDocument);

        // Make sure to return the mocked task for Firestore operation methods
        when(mockedDocument.set(any())).thenReturn(mockedTask);
        when(mockedDocument.update(any(HashMap.class))).thenReturn(mockedTask);
        when(mockedDocument.delete()).thenReturn(mockedTask);

        usersDB = new UsersDB(mockedFirestore);
    }


    @After
    public void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    public void addUser_ShouldInvokeFirestoreSet() {
        User mockUser = mock(User.class);
        when(mockUser.getUserId()).thenReturn("testUserId");

        usersDB.addUser(mockUser);

        verify(mockedCollection).document("testUserId");
        verify(mockedDocument).set(any(HashMap.class));
    }

    @Test
    public void editUser_ShouldInvokeFirestoreUpdate() {
        User mockUser = mock(User.class);
        when(mockUser.getUserId()).thenReturn("testUserId");

        HashMap<String, Object> updates = new HashMap<>();
        updates.put("key", "value");

        usersDB.editUser(mockUser, updates);

        verify(mockedCollection).document("testUserId");
        verify(mockedDocument).update(updates);
    }

    @Test
    public void deleteUser_ShouldInvokeFirestoreDelete() {
        User mockUser = mock(User.class);
        when(mockUser.getUserId()).thenReturn("testUserId");

        usersDB.deleteUser(mockUser);

        verify(mockedCollection).document("testUserId");
        verify(mockedDocument).delete();
    }
}
