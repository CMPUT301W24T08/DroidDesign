package com.example.droiddesign;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.example.droiddesign.model.Event;
import com.example.droiddesign.model.UserProfile;
import com.example.droiddesign.service.AdminService;
import com.example.droiddesign.repository.EventRepository;
import com.example.droiddesign.repository.UserProfileRepository;
import com.example.droiddesign.repository.ImageRepository;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class AdminUnitTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private ImageRepository imageRepository;

    @InjectMocks
    private AdminService adminService;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAdminCanDeleteEvent() {
        String eventId = "event123";
        doNothing().when(eventRepository).deleteById(eventId);

        adminService.deleteEvent(eventId);

        verify(eventRepository, times(1)).deleteById(eventId);
    }

    @Test
    public void testAdminCanRemoveUserProfile() {
        String userId = "user123";
        doNothing().when(userProfileRepository).deleteById(userId);

        adminService.removeUserProfile(userId);

        verify(userProfileRepository, times(1)).deleteById(userId);
    }

    @Test
    public void testAdminCanRemoveImage() {
        String imageId = "image123";
        doNothing().when(imageRepository).deleteById(imageId);

        adminService.removeImage(imageId);

        verify(imageRepository, times(1)).deleteById(imageId);
    }

    @Test
    public void testAdminCanBrowseEvents() {
        List<Event> mockEvents = Arrays.asList(
                new Event("event1", "Event One", "2024-01-01", "Location One", "9:00", "17:00", "Geo1", "org1", "poster1", "Description one", 100, 0, "qrCode1"),
                new Event("event2", "Event Two", "2024-02-02", "Location Two", "10:00", "18:00", "Geo2", "org2", "poster2", "Description two", 200, 0, "qrCode2")
        );
        when(eventRepository.findAll()).thenReturn(mockEvents);

        List<Event> events = adminService.listAllEvents();

        assertNotNull(events);
        assertEquals(2, events.size());
        verify(eventRepository, times(1)).findAll();
    }

    @Test
    public void testAdminCanBrowseUserProfiles() {
        List<UserProfile> mockUserProfiles = Arrays.asList(
                new UserProfile("user1", "User One", "user1@example.com", "Image1"),
                new UserProfile("user2", "User Two", "user2@example.com", "Image2")
        );
        when(userProfileRepository.findAll()).thenReturn(mockUserProfiles);

        List<UserProfile> userProfiles = adminService.listAllUserProfiles();

        assertNotNull(userProfiles);
        assertEquals(2, userProfiles.size());
        verify(userProfileRepository, times(1)).findAll();
    }

    // Additional tests for browsing and managing images 
}

