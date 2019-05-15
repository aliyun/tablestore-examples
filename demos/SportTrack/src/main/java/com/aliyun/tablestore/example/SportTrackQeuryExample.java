package com.aliyun.tablestore.example;

import com.aliyun.tablestore.sporttrack.TablestoreTrackReader;
import com.aliyun.tablestore.sporttrack.model.SportObject;
import com.aliyun.tablestore.sporttrack.model.SportObjectType;
import com.aliyun.tablestore.example.common.SportTrackExample;


public class SportTrackQeuryExample extends SportTrackExample {
    private TablestoreTrackReader reader;

    public SportTrackQeuryExample() {
        reader = new TablestoreTrackReader(sportTrack);
    }

    public static void main(String[] args) {
        SportTrackQeuryExample example = new SportTrackQeuryExample();

        example.listTrackMeta();
        example.getTrackData();
    }

    public void listTrackMeta() {
        SportObject object = mockSportObject(SportObjectType.trackOrder);
        reader.listTrackMeta(object);
    }

    public void getTrackData() {
        SportObject object = mockSportObject(SportObjectType.trackOrder);
        reader.getTrackData(object);
    }
}
