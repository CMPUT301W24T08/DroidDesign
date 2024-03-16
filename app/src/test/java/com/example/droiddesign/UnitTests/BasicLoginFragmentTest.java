package com.example.droiddesign.UnitTests;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import com.example.droiddesign.model.User;
import com.example.droiddesign.view.BasicLoginFragment;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.ExecutionException;

public class BasicLoginFragmentTest {
	@Mock
	private FirebaseAuth mockAuth;
	@Mock
	private FirebaseFirestore mockFirestore;
	@Mock
	private CollectionReference mockCollectionReference;
	@Mock
	private DocumentReference mockDocumentReference;
	@Mock
	private Task<AuthResult> mockTask;
	@Mock
	private FirebaseUser mockUser;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testCreateUser() throws ExecutionException, InterruptedException {
		// Create a mock user input
		String userName = "testUserName";
		String email = "testEmail@example.com";
		String role = "testRole";
		String company = "testCompany";
		String phoneNumber = "testPhoneNumber";

		// Mock the FirebaseAuth signInAnonymously() method to return the mock Task
		when(mockAuth.signInAnonymously()).thenReturn(mockTask);

		// Create an instance of BasicLoginFragment
		BasicLoginFragment basicLoginFragment = new BasicLoginFragment();

		// Set the mock FirebaseAuth instance to the BasicLoginFragment
		basicLoginFragment.mAuth = mockAuth;

		// Mock the FirebaseAuth getCurrentUser() method to return the mock FirebaseUser
		when(mockAuth.getCurrentUser()).thenReturn(mockUser);

		// Create a mock User instance
		User mockUser = new User("testUserId", role);
		mockUser.setUserName(userName);
		mockUser.setEmail(email);
		mockUser.setRegistered(String.valueOf(true));
		mockUser.setCompany(company);
		mockUser.setPhone(phoneNumber);

		// Mock the FirebaseFirestore getInstance() method to return the mock Firestore instance
		when(mockFirestore.collection("users")).thenReturn(mockCollectionReference);
		when(mockCollectionReference.document(mockUser.getUserId())).thenReturn(mockDocumentReference);

		// Mock the Task isSuccessful() method to return true
		when(mockTask.isSuccessful()).thenReturn(true);

		// Mock the DocumentReference set() method to return a successful Task
		Task<Void> mockWriteTask = Tasks.forResult(null);
		when(mockDocumentReference.set(mockUser)).thenReturn(mockWriteTask);

		// Call the createUser() method
		basicLoginFragment.createUser(userName, email, role, company, phoneNumber);

		// Assert that the user is not null
		assertNotNull(mockUser);
	}
}
