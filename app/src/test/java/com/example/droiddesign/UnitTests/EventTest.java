package com.example.droiddesign.UnitTests;

import com.example.droiddesign.model.Event;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EventTest {

    @Mock
    private FirebaseFirestore mockedFirestore;

    private Event event;

    @Before
    public void setUp() {
        // Assuming Event class has a constructor or method to set Firestore instance
        event = new Event(); // Assuming a constructor without Firestore instance for simplicity

    }

    @Test
    public void constructorTest() {
        assertNotNull("Event instance should not be null", event);
    }

    @Test
    public void settingAndGettingEventName() {
        String expectedName = "Sample Event";
        event.setEventName(expectedName);
        assertEquals("Event name should match the set value", expectedName, event.getEventName());
    }

    // Similar tests for other set/get methods

    @Test
    public void saveToFirestore() {

        event.saveToFirestore();

    }

    @Test
    public void eventUpdatesCorrectly() {
        String newLocation = "New Location";
        event.setEventLocation(newLocation);
        assertEquals("Event location should be updated", newLocation, event.getEventLocation());
    }


}
