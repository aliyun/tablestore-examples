package com.aliyun.tablestore.example.common;

import com.aliyun.tablestore.sporttrack.TablestoreSportTrackConfig;
import com.aliyun.tablestore.sporttrack.TablestoreTrack;
import com.aliyun.tablestore.sporttrack.model.*;

public class SportTrackExample {
    private String pathSeperator = "/";
    private TablestoreConf conf;
    final String metaTableName = "sporttrack_meta";
    final String dataTableName = "sporttrack_data";

    protected TablestoreTrack sportTrack;

    public SportTrackExample() {
        String os = System.getProperty("os.name");
        if (os.toLowerCase().startsWith("win")) {
            pathSeperator = "\\";
        }
        conf = TablestoreConf.newInstance(System.getProperty("user.home") + pathSeperator + "tablestoreConf.json");

        String endPoint = conf.getEndpoint();
        String accessId = conf.getAccessId();
        String accessKey = conf.getAccessKey();
        String instanceName = conf.getInstanceName();

        TablestoreSportTrackConfig config = new TablestoreSportTrackConfig(endPoint, accessId, accessKey, instanceName, metaTableName, dataTableName);
        sportTrack = new TablestoreTrack(config);
    }


    protected SportObject mockSportObject(String sportObjectType) {
        SportObject object = new SportObject();
        object.setObjectName("mock_app_uid");
        object.setObjectId("mock_device_id");
        object.setSportObjectType(sportObjectType);

        return object;
    }
}
