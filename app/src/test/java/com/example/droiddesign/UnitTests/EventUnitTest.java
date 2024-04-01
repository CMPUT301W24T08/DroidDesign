package com.example.droiddesign.UnitTests;

import static org.junit.Assert.assertEquals;

import com.example.droiddesign.model.Event;

import org.junit.Before;
import org.junit.Test;

public class EventUnitTest {

	private Event event;

	@Before
	public void setUp() {
		event = new Event();
	}

	@Test
	public void testGettersAndSetters() {

		String testString = "Test";
		event.setEventName(testString);
		assertEquals(testString, event.getEventName());

		event.setEventLocation(testString);
		assertEquals(testString, event.getEventLocation());

		event.setEventDate(testString);
		assertEquals(testString, event.getEventDate());

		event.setStartTime(testString);
		assertEquals(testString, event.getStartTime());

		event.setEndTime(testString);
		assertEquals(testString, event.getEndTime());

		event.setDate(testString);
		assertEquals(testString, event.getDate());

		event.setGeolocation(testString);
		assertEquals(testString, event.getGeolocation());

		event.setOrganizerOwnerId(testString);
		assertEquals(testString, event.getOrganizerOwnerId());

		event.setImagePosterId(testString);
		assertEquals(testString, event.getImagePosterId());

		event.setDescription(testString);
		assertEquals(testString, event.getDescription());

		Integer testInteger = 100;
		event.setSignupLimit(testInteger);
		assertEquals(testInteger, event.getSignupLimit());

		event.setAttendeesCount(testInteger);
		assertEquals(testInteger, event.getAttendeesCount());

		event.setShareQrCode(testString, testString);
		assertEquals(testString, event.getShareQrCode());

	}
}