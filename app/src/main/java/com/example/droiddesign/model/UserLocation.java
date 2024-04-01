package com.example.droiddesign.model;

public class UserLocation {
    private boolean geolocationTrackingEnabled;

    public UserLocation() {
        this.geolocationTrackingEnabled = true; // default value
    }

    public boolean isGeolocationTrackingEnabled() {
        return geolocationTrackingEnabled;
    }

    public void setGeolocationTrackingEnabled(boolean geolocationTrackingEnabled) {
        this.geolocationTrackingEnabled = geolocationTrackingEnabled;

    }

    // Additional preferences as needed
}

