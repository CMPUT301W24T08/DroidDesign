package com.example.droiddesign;

import static com.google.common.base.CharMatcher.any;
import static com.google.common.base.Verify.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

import com.example.droiddesign.model.Event;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;

public class EventTest {

	private FirebaseFirestore mockFirestore;
	private Event testEvent;

	@Before
	public void setUp() {
		// Mock FirebaseFirestore
		mockFirestore = mock(FirebaseFirestore.class);
		Event.setFirestore(mockFirestore);

		// Create a test event
		testEvent = new Event(
				"testEventId",
				"Test Event",
				"2024-01-20",
				"10:00 AM",
				"12:00 PM",
				"Test Location",
				"testOrganizerId",
				"testImagePosterId",
				"Test Description",
				100,
				0,
				"testQrCode"
		);
	}

	@Test
	public void testSaveToFirestore() {
		// Mock the Firestore instance
		mockFirestore = mock(FirebaseFirestore.class);
		testEvent.setFirestore(mockFirestore);

		// Mock Firestore document reference
		when(mockFirestore.collection(anyString()).document(anyString())).thenReturn(mockDocumentReference);

		// Call the method to save the event to Firestore
		testEvent.saveToFirestore();

		// Verify that FirebaseFirestore set() method is called
		verify(mockFirestore.collection(anyString()).document(anyString())).set(anyMap(), any());
	}

	@Test
	public void testLoadFromFirestore() {
		// Mock DocumentSnapshot
		DocumentSnapshot mockDocumentSnapshot = mock(DocumentSnapshot.class);
		when(mockDocumentSnapshot.exists()).thenReturn(true);
		when(mockDocumentSnapshot.toObject(Event.class)).thenReturn(testEvent);

		// Mock Firestore get() method
		when(mockFirestore.collection(anyString()).document(anyString()).get()).thenReturn(mockTask);

		// Call the method to load event from Firestore
		Event.loadFromFirestore("testEventId", new Event.FirestoreCallback() {
			@Override
			public void onCallback(Event event) {
				// Verify that the loaded event is not null
				assertNotNull(event);

				// Verify that the loaded event matches the test event
				assertEquals(testEvent.getEventId(), event.getEventId());
				assertEquals(testEvent.getEventName(), event.getEventName());
				// Add assertions for other fields as needed
			}
		});
	}
}

