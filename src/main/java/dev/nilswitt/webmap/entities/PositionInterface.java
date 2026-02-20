package dev.nilswitt.webmap.entities;

import java.time.Instant;

public interface PositionInterface  {
    double getLatitude();
    double getLongitude();
    double getAltitude();
    double getAccuracy();
    Instant getTimestamp();
    void setTimestamp(Instant timestamp);
    void setLatitude(double latitude);
    void setLongitude(double longitude);
    void setAltitude(double altitude);
    void setAccuracy(double accuracy);
}
