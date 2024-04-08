package com.example.droiddesign.UiTests;

import com.example.droiddesign.view.Everybody.FirebaseServiceUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class FirebaseUtilEventMockTest {

	private FirebaseServiceUtils firebaseServiceUtils;


	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
//		mockFirebaseWrapper = mock(FirebaseWrapper.class);
//		roleSelectionActivity = new RoleSelectionActivity();
//		roleSelectionActivity.setFirebaseWrapper(mockFirebaseWrapper);
//		when(mockFirebaseAuth.getCurrentUser()).thenReturn(mockFirebaseUser);
//		when(mockFirebaseUser.getUid()).thenReturn("uniqueUserId123");

		// Initialize FirebaseServiceUtils with mocks
		FirebaseServiceUtils.initialize();
	}

	@Test
	public void testFirebaseInitialization() {
		// Create an instance of the activity
//		EventMenuActivity activity = new EventMenuActivity();
//		activity.onCreate(null);  // Passing null for the Bundle as it's not used directly
//		assertNotNull("Firestore instance should not be null", FirebaseServiceUtils.getFirestore());
//		assertNotNull("FirebaseAuth instance should not be null", FirebaseServiceUtils.getFirebaseAuth());

		// Verify that the correct Firebase instances are retrieved
//		assert FirebaseServiceUtils.getFirestore() == mockFirestore;
//		assert FirebaseServiceUtils.getFirebaseAuth() == mockFirebaseAuth;

		// Verify the activity retrieves the current user ID correctly
//		String userId = FirebaseServiceUtils.getCurrentUserId();
//		verify(mockFirebaseAuth).getCurrentUser();
//		assert "uniqueUserId123".equals(userId);

		// Mock and verify a Firestore document fetch
//		String testPath = "events/sampleEvent";
//		DocumentReference mockDocumentReference = mock(DocumentReference.class);
//		when(mockFirestore.document(testPath)).thenReturn(mockDocumentReference);
//
//		Task<String> mockTask = mock(Task.class);
//		when(mockDocumentReference.get()).thenReturn(mockTask);

//		FirebaseServiceUtils.getEventId(testPath);
//		verify(mockFirestore).document(testPath);
//		verify(mockDocumentReference).get();
	}
}
