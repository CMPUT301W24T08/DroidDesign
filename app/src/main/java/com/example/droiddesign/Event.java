package com.example.droiddesign;

import java.util.ArrayList;
import java.util.Date;

public class Event {
    // Dummy class that we can either forget about or flesh out more.
    // ArrayList<Organizer> organizers;
    String eventName;
    Date date;
    String location;

    public Event(String eventName, Date date, String location) {
        this.eventName = eventName;
        this.date = date;
        this.location = location;
    }
}