package com.example.droiddesign;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

import com.example.droiddesign.model.Event;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;

public class EventTest {

	private FirebaseFirestore mockFirestore;
	private Event testEvent;

	@Before
	public Event mockEvent() {
		// Create a test event
		String eventId = "event123";
		String eventName = "Example Conference";
		String eventDate = "2024-12-31";
		String eventLocation = "Convention Center";
		String startTime = "9:00 AM";
		String endTime = "5:00 PM";
		String geolocation = "Latitude: 123, Longitude: 456";
		String organizerOwnerId = "organizer123";
		String imagePosterId = "poster123";
		String description = "This is a test event.";
		Integer signupLimit = 100;
		Integer attendeesCount = 50;
		String qrCode = "qr123";

		// Create an instance of Event using real-life information
		testEvent = new Event(eventId, eventName, eventDate, eventLocation, startTime, endTime, geolocation,
				organizerOwnerId, imagePosterId, description, signupLimit, attendeesCount, qrCode);
		return testEvent;
	}

	@Test
	public void testEventInitialization() {
		// Test valid input and property initialization
		Event event = new Event(
				"eventId",
				"eventName",
				"eventDate",
				"eventLocation",
				"startTime",
				"endTime",
				"geolocation",
				"organizerOwnerId",
				"imagePosterId",
				"description",
				100,
				0,
				"qrCode"
		);
		assertNotNull(event);
		assertEquals("eventId", event.getEventId());
		assertEquals("eventName", event.getEventName());
		assertEquals("eventDate", event.getEventDate());
		assertEquals("eventLocation", event.getEventLocation());
		assertEquals("startTime", event.getStartTime());
		assertEquals("endTime", event.getEndTime());
		assertEquals("geolocation", event.getGeolocation());
		assertEquals("organizerOwnerId", event.getOrganizerOwnerId());
		assertEquals("imagePosterId", event.getImagePosterId());
		assertEquals("description", event.getDescription());
		assertEquals(Integer.valueOf(100), event.getSignupLimit());
		assertEquals(Integer.valueOf(0), event.getAttendeesCount());
		assertEquals("qrCode", event.getQrCode());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidInput() {
		// Test with invalid input parameters
		Event event = new Event(
				null, // Invalid eventId
				"eventName",
				"eventDate",
				"eventLocation",
				"startTime",
				"endTime",
				"geolocation",
				"organizerOwnerId",
				"imagePosterId",
				"description",
				100,
				0,
				"qrCode"
		);
	}

	@Test
	public void testSaveToFirestore() {
		testEvent = mockEvent();
		// Mock the Firestore instance
		mockFirestore = mock(FirebaseFirestore.class);

		// Call the method to save the event to Firestore
		testEvent.saveToFirestore();

		// TODO: change activity anc check it has the view
	}

//	@Test
//	public void testLoadFromFirestore() {
//		// Mock DocumentSnapshot
//		DocumentSnapshot mockDocumentSnapshot = mock(DocumentSnapshot.class);
//		when(mockDocumentSnapshot.exists()).thenReturn(true);
//		when(mockDocumentSnapshot.toObject(Event.class)).thenReturn(testEvent);
//
//		// Mock Firestore get() method
//		when(mockFirestore.collection(anyString()).document(anyString()).get()).thenReturn(mockTask);
//
//		// Call the method to load event from Firestore
//		Event.loadFromFirestore("testEventId", new Event.FirestoreCallback() {
//			@Override
//			public void onCallback(Event event) {
//				// Verify that the loaded event is not null
//				assertNotNull(event);
//
//				// Verify that the loaded event matches the test event
//				assertEquals(testEvent.getEventId(), event.getEventId());
//				assertEquals(testEvent.getEventName(), event.getEventName());
//				// Add assertions for other fields as needed
//			}
//		});
//	}

//	@Test
//	public void testSavedToFirestore() {
//		// Create an instance of Event
//		Event testEvent = new Event(/* pass necessary constructor arguments */);
//
//		// Mock the FirebaseFirestore instance
//		FirebaseFirestore mockFirestore = mock(FirebaseFirestore.class);
//		testEvent.setFirestore(mockFirestore);
//
//		// Mock Firestore document reference
//		DocumentReference mockDocumentReference;
//		when(mockFirestore.collection(anyString()).document(anyString())).thenReturn(mockDocumentReference);
//
//		// Call the method to save the event to Firestore
//		testEvent.saveToFirestore();
//
//		// Verify that FirebaseFirestore set() method is called
//		verify(mockFirestore.collection(anyString()).document(anyString())).set(anyMap(), any());
//	}

}

