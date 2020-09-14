package com.example.cameraapp;

public class KeystrokeData {
    public float pressure = 0;
    public long timePressed = 0;
    public long timeReleased = 0;
    public KeystrokeData() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public KeystrokeData(float pressure, long timePressed, long timeReleased) {
        this.pressure = pressure;
        this.timePressed = timePressed;
        this.timeReleased = timeReleased;
    }
}
