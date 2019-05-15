package com.aliyun.tablestore.sporttrack;

import com.alicloud.openservices.tablestore.timestream.TimestreamDataTable;
import com.alicloud.openservices.tablestore.timestream.model.*;
import com.alicloud.openservices.tablestore.timestream.model.filter.*;
import com.aliyun.tablestore.sporttrack.consts.Constants;
import com.aliyun.tablestore.sporttrack.model.*;
import com.aliyun.tablestore.sporttrack.utils.DistanceUtil;

import java.util.*;

import static com.alicloud.openservices.tablestore.timestream.model.filter.FilterFactory.and;

public class TablestoreTrackReader implements ITrackerReader {
    private TablestoreTrack tablestoreTrack;

    public TablestoreTrackReader(TablestoreTrack tablestoreTrack) {
        this.tablestoreTrack = tablestoreTrack;
    }

    private double computeDistance(Coords coord1, Coords coord2) {
        double distance = DistanceUtil.distance(coord1.getLatitude(), coord1.getLongitude(), coord2.getLatitude(), coord2.getLongitude());
        return distance;
    }


    @Override
    public void listTrackMeta(SportObject sportObject) {
        Filter filter = and(
                Tag.equal("objectID", sportObject.getObjectId()),
                Tag.equal("objectType", sportObject.getSportObjectType())
        );

        TimestreamMetaIterator iter = tablestoreTrack.timestreamClient.metaTable()
                .filter(filter)
                .selectAttributes("distance", "starttime")
                .fetchAll();
        System.out.print(iter.getTotalCount());
        while (iter.hasNext()) {
            TimestreamMeta meta = iter.next();
            String title = meta.getIdentifier().getName();
            long distance = meta.getAttributeAsLong(Constants.ATTRIBUTE_COL_DISTANCE);
            long timestamp = meta.getAttributeAsLong(Constants.ATTRIBUTE_COL_STARTTIME);
            System.out.println(String.format("title: %s, distance:%d meters,timestamp:%d", title, distance, timestamp));
        }
    }

    @Override
    public void getTrackData(SportObject sportObject) {
        TimestreamIdentifier identifier = new TimestreamIdentifier.Builder(sportObject.getObjectName())
                .addTag("objectID", sportObject.getObjectId())
                .addTag("objectType", sportObject.getSportObjectType())
                .build();

        TimestreamDataTable dataTable = tablestoreTrack.timestreamClient.dataTable(tablestoreTrack.config.getTrackDataTableName());
        Iterator<Point> iter = dataTable.get(identifier)
                .fetchAll();

        while (iter.hasNext()) {
            System.out.println("\t" + iter.next().getFields());
        }
    }


    @Override
    public void listTargetNearbyOrderbyDistance(String targetType, Position centerPoint, int distanceInMeter) {
        String gePoint = String.format("%f,%f", centerPoint.getCoords().getLatitude(), centerPoint.getCoords().getLongitude());
        Filter filter = and(
                //查询条件一：数据类型为 设备
                Tag.equal("objectType", SportObjectType.device),
                //查询条件二：距离中心点distanceInMeter距离的点
                Attribute.inGeoDistance(Constants.ATTRIBUTE_COL_GEO, gePoint, distanceInMeter)
        );

        TimestreamMetaIterator iter = tablestoreTrack.timestreamClient.metaTable()
                .filter(filter)
                .selectAttributes("location")
                .fetchAll();

        List<TargetNearbyEntity> list = new ArrayList<TargetNearbyEntity>();

        while (iter.hasNext()) {
            TimestreamMeta meta = iter.next();
            TargetNearbyEntity entity = new TargetNearbyEntity();

            Coords currentTargetCoords = new Coords();
            String geo = meta.getAttributeAsString(Constants.ATTRIBUTE_COL_GEO);//rows.get(i).getLatestColumn().getValue().asString();
            currentTargetCoords.setLatitude(Double.parseDouble(geo.split(",")[0]));
            currentTargetCoords.setLongitude(Double.parseDouble(geo.split(",")[1]));
            //long timestamp = rows.get(i).getLatestColumn(Constants.ATTRIBUTE_COL_TIMESTAMP).getValue().asLong();
            entity.setPosition(new Position());
            entity.getPosition().setCoords(currentTargetCoords);
            //entity.getPosition().setTimestamp(timestamp);

            SportObject object = new SportObject();

            object.setObjectName(meta.getIdentifier().getName());
            object.setObjectId(meta.getIdentifier().getTagValue(Constants.PRIMARY_KEY_OBJECTID));
            object.setSportObjectType(meta.getIdentifier().getTagValue(Constants.PRIMARY_KEY_OBJECTTYPE));
            entity.setSportObject(object);

            int distanceInMeterBetweenCenterPoint = (int) computeDistance(centerPoint.getCoords(), currentTargetCoords);
            entity.setDistanceInMeterBetweenCenter(distanceInMeterBetweenCenterPoint);

            list.add(entity);
        }


        Collections.sort(list, (TargetNearbyEntity target1, TargetNearbyEntity target2) -> target1.getDistanceInMeterBetweenCenter() - target2.getDistanceInMeterBetweenCenter());

        System.out.println("TotalCount: " + list.size());
        for (int i = 0; i < list.size(); i++) {
            System.out.println(String.format("user: %s, distance:%d meters", list.get(i).getSportObject().getObjectName(), list.get(i).getDistanceInMeterBetweenCenter()));
        }

    }
}
