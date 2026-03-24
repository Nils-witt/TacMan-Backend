package dev.nilswitt.tacman.entities;

import java.time.Instant;

public interface PositionInterface  {
    Double getLatitude();
    Double getLongitude();
    Double getAltitude();
    Double getAccuracy();
    Instant getTimestamp();
    void setTimestamp(Instant timestamp);
    void setLatitude(Double latitude);
    void setLongitude(Double longitude);
    void setAltitude(Double altitude);
    void setAccuracy(Double accuracy);
}
