package com.aliyun.tablestore.sporttrack;

import com.aliyun.tablestore.sporttrack.model.SportObject;
import com.aliyun.tablestore.sporttrack.model.SportTrackMeta;
import com.aliyun.tablestore.sporttrack.model.SportTrackOrder;
import com.aliyun.tablestore.sporttrack.model.TrackPosition;

import java.util.List;

public interface ITrackWriter {

    /**
     * 写入位置点meta信息，包括附近的人、附近的跑步路线
     * @param sportObject
     * @param sportTrackMeta
     */
    void writeTrackMeta(SportObject sportObject, SportTrackMeta sportTrackMeta);

    /**
     * 写入跑步、骑行等运动记录信息
     * @param sportObject
     * @param sportTrackOrder
     */
    void writeTrackOrderMeta(SportObject sportObject, SportTrackOrder sportTrackOrder);

    /**
     * 写入轨迹点信息
     * @param sportObject
     * @param positions
     */
    void writeTrackPosition(SportObject sportObject, List<TrackPosition> positions);
}
