package com.aliyun.tablestore.sporttrack;

public class TablestoreSportTrackConfig {
    private String accessId;
    private String accessKey;
    private String endPoint;
    private String instanceName;

    private String trackMetaTableName;
    private String trackDataTableName;

    public TablestoreSportTrackConfig(String endPoint, String accessId, String accessKey, String instanceName,
                                      String trackMetaTableName, String trackDataTableName) {
        this.endPoint = endPoint;
        this.accessId = accessId;
        this.accessKey = accessKey;
        this.instanceName = instanceName;
        this.trackMetaTableName = trackMetaTableName;
        this.trackDataTableName = trackDataTableName;
    }


    public String getAccessId() {
        return accessId;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public String getTrackMetaTableName() {
        return trackMetaTableName;
    }

    public String getTrackDataTableName() {
        return trackDataTableName;
    }

}
