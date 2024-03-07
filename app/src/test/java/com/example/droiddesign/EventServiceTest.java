import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EventServiceTest {

    @Test
    void testAttendeeSignUpForEvent() {
        EventService eventService = new EventService();
        String eventId = "evt123";
        String attendeeId = "user123";
        
        boolean signUpResult = eventService.signUpForEvent(eventId, attendeeId);
        assertTrue(signUpResult);
        
        // Assuming there's a method to check if an attendee has signed up for an event
        assertTrue(eventService.isAttendeeSignedUpForEvent(eventId, attendeeId));
    }
}
