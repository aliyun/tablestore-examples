package com.aliyun.tablestore.sporttrack.model;

public class TargetNearbyEntity {
    private Position position;

    private SportObject sportObject;

    private int distanceInMeterBetweenCenter;

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public SportObject getSportObject() {
        return sportObject;
    }

    public void setSportObject(SportObject sportObject) {
        this.sportObject = sportObject;
    }

    public int getDistanceInMeterBetweenCenter() {
        return distanceInMeterBetweenCenter;
    }

    public void setDistanceInMeterBetweenCenter(int distanceInMeterBetweenCenter) {
        this.distanceInMeterBetweenCenter = distanceInMeterBetweenCenter;
    }

}
