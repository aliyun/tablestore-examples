package com.aliyun.tablestore.example;

import com.aliyun.tablestore.sporttrack.TablestoreTrackReader;
import com.aliyun.tablestore.sporttrack.TablestoreTrackWriter;
import com.aliyun.tablestore.sporttrack.model.*;
import com.aliyun.tablestore.example.common.SportTrackExample;

import java.util.UUID;

public class TargetNearbyExample extends SportTrackExample {

    private TablestoreTrackWriter writer;
    private TablestoreTrackReader reader;

    public TargetNearbyExample() {
        writer = new TablestoreTrackWriter(sportTrack);
        reader = new TablestoreTrackReader(sportTrack);
    }

    public static void main(String[] args) {
        TargetNearbyExample example = new TargetNearbyExample();
        example.writeCenterPoint();
        example.writeRandomPoint();

        example.listTargetNearbyOrderbyDistance();
    }


    private SportObject centerSportObject() {
        SportObject object = new SportObject();
        object.setObjectName("user_center");
        object.setObjectId("user_center_id");
        object.setSportObjectType(SportObjectType.device);

        return object;
    }

    private int getRandomId() {
        int x = (int) (Math.random() * 20);
        return x;
    }

    private SportObject randomSportObject() {
        SportObject object = new SportObject();

        object.setObjectName("user_" + getRandomId());
        object.setObjectId(UUID.randomUUID().toString());
        object.setSportObjectType(SportObjectType.device);

        return object;
    }

    private Position centerPosition() {
        Position position = new Position();
        Coords coords = new Coords();
        coords.setLatitude(30.12883);
        coords.setLongitude(120.08545);

        position.setTimestamp(System.currentTimeMillis());
        position.setCoords(coords);

        return position;
    }

    public void writeCenterPoint() {
        SportObject object = centerSportObject();
        Position centerPosition = centerPosition();

        SportTrackMeta sportTrackMeta = new SportTrackMeta();
        sportTrackMeta.setTargetNearbyType(TargetNearbyType.PEOPLE);
        Coords coords = centerPosition.getCoords();
        String location = String.format("%f,%f", coords.getLatitude(), coords.getLongitude());
        sportTrackMeta.setLocation(location);
        sportTrackMeta.setTimestamp(centerPosition.getTimestamp());

        writer.writeTrackMeta(object, sportTrackMeta);
    }

    public void writeRandomPoint() {
        for (int i = 0; i < 100; i++) {
            SportObject object = randomSportObject();
            SportTrackMeta sportTrackMeta = new SportTrackMeta();
            sportTrackMeta.setTargetNearbyType(TargetNearbyType.PEOPLE);

            double lat = 30.12883 + getRandomId() * 0.01;
            double lon = 120.08545 + getRandomId() * 0.01;
            String location = String.format("%f,%f", lat, lon);
            sportTrackMeta.setLocation(location);
            sportTrackMeta.setTimestamp(System.currentTimeMillis());

            writer.writeTrackMeta(object, sportTrackMeta);
        }
    }

    public void listTargetNearbyOrderbyDistance() {
        reader.listTargetNearbyOrderbyDistance(TargetNearbyType.PEOPLE, centerPosition(), 10000);
    }


}
