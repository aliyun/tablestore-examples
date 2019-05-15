package com.aliyun.tablestore.sporttrack.model;

public class TrackPosition extends Position {
    private int accuracy;

    private int altitude;

    private int altitudeAccuracy;

    private int speed;

    public int getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(int accuracy) {
        this.accuracy = accuracy;
    }

    public int getAltitude() {
        return altitude;
    }

    public void setAltitude(int altitude) {
        this.altitude = altitude;
    }

    public int getAltitudeAccuracy() {
        return altitudeAccuracy;
    }

    public void setAltitudeAccuracy(int altitudeAccuracy) {
        this.altitudeAccuracy = altitudeAccuracy;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }


}
