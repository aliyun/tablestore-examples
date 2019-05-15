package com.aliyun.tablestore.example;

import com.aliyun.tablestore.sporttrack.TablestoreTrackWriter;
import com.aliyun.tablestore.sporttrack.model.*;
import com.aliyun.tablestore.example.common.SportTrackExample;

import java.util.ArrayList;
import java.util.List;

public class UploadSportTrackExample extends SportTrackExample {
    private TablestoreTrackWriter writer;

    public UploadSportTrackExample() {
        writer = new TablestoreTrackWriter(sportTrack);
    }

    public static void main(String[] args) {
        UploadSportTrackExample example = new UploadSportTrackExample();
        try {
            example.uploadSportTrackOrder();
            example.uploadSportTrack();
        } catch (InterruptedException ex) {
            System.out.println(ex.toString());
        }
    }

    public void uploadSportTrackOrder() {
        SportObject object = mockSportObject(SportObjectType.trackOrder);
        object.setObjectName("4月17日晚上骑行");
        SportTrackOrder order = new SportTrackOrder();
        order.setSportTrackType(SportType.RIDING);
        order.setDistance(3000);
        order.setStartTime(1557316433L);
        order.setEndTime(1557317753L);
        writer.writeTrackOrderMeta(object, order);
    }


    public void uploadSportTrack() throws InterruptedException {
        SportObject object = mockSportObject(SportObjectType.device);

        List<TrackPosition> positions = new ArrayList<TrackPosition>();
        for (int i = 0; i < 1000; i++) {
            Coords coords = new Coords();
            coords.setLatitude(30.1288339742);
            coords.setLongitude(120.0854587555 + 0.0001 * i);

            TrackPosition position = new TrackPosition();
            position.setAccuracy(10);
            position.setAltitude(2);
            position.setAltitudeAccuracy(1);
            position.setSpeed(5);
            position.setTimestamp(System.currentTimeMillis());
            Thread.sleep(10);
            position.setCoords(coords);

            positions.add(position);
        }

        writer.writeTrackPosition(object, positions);
    }

}
