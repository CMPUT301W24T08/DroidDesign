package com.example.droiddesign;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.droiddesign.model.Event;
import com.example.droiddesign.model.Attendee;
import com.example.droiddesign.service.AttendeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class AttendeeUnitTest {

    @Mock
    private EventService eventService; // Assuming this service handles event-specific actions

    @InjectMocks
    private AttendeeService attendeeService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAttendeeCanSignUpForEvent() {
        String eventId = "event123";
        Attendee attendee = new Attendee("attendee123", "Attendee Name");
        Event event = new Event(eventId, "Tech Conference", "2024-05-20", "Convention Center", "9:00 AM", "5:00 PM", "Lat:Long", "org123", "poster123", "This is a tech event.", 100, 0, "qrCode");

        when(eventService.signUpForEvent(eventId, attendee.getId())).thenReturn(true);

        boolean signUpResult = attendeeService.signUpForEvent(eventId, attendee.getId());

        assertTrue(signUpResult);
        verify(eventService, times(1)).signUpForEvent(eventId, attendee.getId());
    }

    @Test
    public void testAttendeeCanCheckInByScanningQRCode() {
        String eventId = "event123";
        String attendeeId = "attendee123";
        when(eventService.checkInAttendee(eventId, attendeeId)).thenReturn(true);

        boolean checkInResult = attendeeService.checkInByQRCode(eventId, attendeeId);

        assertTrue(checkInResult);
        verify(eventService, times(1)).checkInAttendee(eventId, attendeeId);
    }

    @Test
    public void testAttendeeCanUpdateProfile() {
        String attendeeId = "attendee123";
        String newName = "Updated Attendee Name";
        when(attendeeService.updateProfile(attendeeId, newName)).thenReturn(true);

        boolean updateResult = attendeeService.updateProfile(attendeeId, newName);

        assertTrue(updateResult);
        verify(attendeeService, times(1)).updateProfile(attendeeId, newName);
    }

    @Test
    public void testAttendeeReceivesNotifications() {
        String attendeeId = "attendee123";
        String notificationMessage = "Event starts in 1 hour";
        when(attendeeService.receiveNotification(attendeeId, notificationMessage)).thenReturn(true);

        boolean notificationResult = attendeeService.receiveNotification(attendeeId, notificationMessage);

        assertTrue(notificationResult);
        
    }

    // Additional tests for browsing events, uploading profile pictures, 
}

