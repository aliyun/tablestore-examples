package com.aliyun.tablestore.sporttrack;

import com.alicloud.openservices.tablestore.timestream.TimestreamDataTable;
import com.alicloud.openservices.tablestore.timestream.TimestreamMetaTable;
import com.alicloud.openservices.tablestore.timestream.model.Point;
import com.alicloud.openservices.tablestore.timestream.model.TimestreamIdentifier;
import com.alicloud.openservices.tablestore.timestream.model.TimestreamMeta;
import com.aliyun.tablestore.sporttrack.model.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class TablestoreTrackWriter implements ITrackWriter {
    private TablestoreTrack tablestoreTrack;

    public TablestoreTrackWriter(TablestoreTrack tablestoreTrack) {
        this.tablestoreTrack = tablestoreTrack;
    }

    private TimestreamIdentifier buildIdentifier(SportObject sportObject) {
        // identifier
        TimestreamIdentifier identifier = new TimestreamIdentifier.Builder(sportObject.getObjectName())
                .addTag("objectID", sportObject.getObjectId())
                .addTag("objectType", sportObject.getSportObjectType())
                .build();

        return identifier;
    }


    @Override
    public void writeTrackMeta(SportObject sportObject, SportTrackMeta sportTrackMeta) {
        TimestreamIdentifier identifier = buildIdentifier(sportObject);

        TimestreamMeta meta = new TimestreamMeta(identifier)
                .addAttribute("location", sportTrackMeta.getLocation())
                .addAttribute("timestamp", sportTrackMeta.getTimestamp())
                .addAttribute("targetnearybytype", sportTrackMeta.getTargetNearbyType());
        TimestreamMetaTable metaTable = tablestoreTrack.timestreamClient.metaTable();
        // write meta
        metaTable.put(meta);
    }

    @Override
    public void writeTrackOrderMeta(SportObject sportObject, SportTrackOrder sportTrackOrder) {
        TimestreamIdentifier identifier = buildIdentifier(sportObject);

        TimestreamMeta meta = new TimestreamMeta(identifier)
                .addAttribute("sporttracktype", sportTrackOrder.getSportTrackType())
                .addAttribute("distance", sportTrackOrder.getDistance())
                .addAttribute("starttime", sportTrackOrder.getStartTime())
                .addAttribute("endtime", sportTrackOrder.getEndTime());
        TimestreamMetaTable metaTable = tablestoreTrack.timestreamClient.metaTable();
        // write meta
        metaTable.put(meta);
    }

    @Override
    public void writeTrackPosition(SportObject sportObject, List<TrackPosition> positions) {
        TimestreamIdentifier identifier = buildIdentifier(sportObject);

        TimestreamDataTable dataTable = tablestoreTrack.timestreamClient.dataTable(tablestoreTrack.config.getTrackDataTableName());
        for (int i = 0; i < positions.size(); i++) {
            Point point = new Point.Builder(positions.get(i).getTimestamp(), TimeUnit.MILLISECONDS)
                    .addField("lat", positions.get(i).getCoords().getLatitude())
                    .addField("lot", positions.get(i).getCoords().getLongitude())
                    .addField("accuracy", positions.get(i).getAccuracy())
                    .addField("altitude", positions.get(i).getAltitude())
                    .addField("altitudeAccuracy", positions.get(i).getAltitudeAccuracy())
                    .addField("speed", positions.get(i).getSpeed())
                    .build();

            // write data
            dataTable.asyncWrite(identifier, point);
        }
    }
}
