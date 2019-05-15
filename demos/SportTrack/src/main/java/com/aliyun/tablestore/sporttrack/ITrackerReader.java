package com.aliyun.tablestore.sporttrack;

import com.aliyun.tablestore.sporttrack.model.Position;
import com.aliyun.tablestore.sporttrack.model.SportObject;

public interface ITrackerReader {

    /**
     * 获取所有的跑步记录
     * @param sportObject
     */
    void listTrackMeta(SportObject sportObject);

    /**
     * 获取一次跑步、骑行的轨迹点
     * @param sportObject
     */
    void getTrackData(SportObject sportObject);

    /**
     * 获取distanceInMeter范围内，附近的人、附近的跑步路线 信息，根据距离排序
     * @param targetType
     * @param centerPoint
     * @param distanceInMeter
     */
    void listTargetNearbyOrderbyDistance(String targetType, Position centerPoint, int distanceInMeter);
}
