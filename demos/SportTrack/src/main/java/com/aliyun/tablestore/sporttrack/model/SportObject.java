package com.aliyun.tablestore.sporttrack.model;


public class SportObject {
    private String objectName;

    private String objectId;

    private String sportObjectType;


    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getSportObjectType() {
        return sportObjectType;
    }

    public void setSportObjectType(String sportObjectType) {
        this.sportObjectType = sportObjectType;
    }

}
