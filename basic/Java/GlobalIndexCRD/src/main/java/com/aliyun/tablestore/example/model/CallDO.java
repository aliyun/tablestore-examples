package com.aliyun.tablestore.example.model;

import com.alicloud.openservices.tablestore.model.*;
import com.aliyun.tablestore.example.utils.Utils;
import com.fasterxml.jackson.core.JsonProcessingException;

import static com.aliyun.tablestore.example.consts.ColumnConsts.*;

public class CallDO {

    private Long cellNumber;

    private Long startTime;

    private Long calledNumber;

    private Long duration;

    private Long baseStationNumber;

    public Long getCellNumber() {
        return cellNumber;
    }

    public CallDO setCellNumber(Long cellNumber) {
        this.cellNumber = cellNumber;
        return this;
    }

    public Long getStartTime() {
        return startTime;
    }

    public CallDO setStartTime(Long startTime) {
        this.startTime = startTime;
        return this;
    }

    public Long getCalledNumber() {
        return calledNumber;
    }

    public CallDO setCalledNumber(Long calledNumber) {
        this.calledNumber = calledNumber;
        return this;
    }

    public Long getDuration() {
        return duration;
    }

    public CallDO setDuration(Long duration) {
        this.duration = duration;
        return this;
    }

    public Long getBaseStationNumber() {
        return baseStationNumber;
    }

    public CallDO setBaseStationNumber(Long baseStationNumber) {
        this.baseStationNumber = baseStationNumber;
        return this;
    }

    public RowPutChange toRowPutChange(String tableName) {
        RowPutChange rowPutChange = new RowPutChange(tableName);
        // primary key
        PrimaryKey primaryKey = new PrimaryKey(new PrimaryKeyColumn[]{
                new PrimaryKeyColumn(CELL_NUMBER, PrimaryKeyValue.fromLong(cellNumber)),
                new PrimaryKeyColumn(START_TIME, PrimaryKeyValue.fromLong(startTime))
        });
        rowPutChange.setPrimaryKey(primaryKey);
        // attribute columns
        rowPutChange
                .addColumn(CALLED_NUMBER, ColumnValue.fromLong(calledNumber))
                .addColumn(DURATION, ColumnValue.fromLong(duration))
                .addColumn(BASE_STATION_NUMBER, ColumnValue.fromLong(baseStationNumber));
        return rowPutChange;
    }

    @SuppressWarnings("unchecked")
    private static <T> T getPrimaryKeyColumnValue(Row row, String name, Class<T> valueType) {
        PrimaryKeyColumn pkColumn = row.getPrimaryKey().getPrimaryKeyColumn(name);
        if (String.class == valueType) {
            return (T) pkColumn.getValue().asString();
        } else if (byte[].class == valueType) {
            return (T) pkColumn.getValue().asBinary();
        } else if (Long.class == valueType || long.class == valueType) {
            return (T) Long.valueOf(pkColumn.getValue().asLong());
        }
        throw new UnsupportedOperationException("Unknown pk type " + valueType);
    }

    @SuppressWarnings("unchecked")
    private static <T> T getLatestColumnValue(Row row, String name, Class<T> valueType) {
        Column column = row.getLatestColumn(name);

        if (String.class == valueType) {
            return (T) column.getValue().asString();
        } else if (Long.class == valueType || long.class == valueType) {
            return (T) Long.valueOf(column.getValue().asLong());
        } else if (boolean.class == valueType || Boolean.class == valueType) {
            return (T) Boolean.valueOf(column.getValue().asBoolean());
        } else if (double.class == valueType || Double.class == valueType) {
            return (T) Double.valueOf(column.getValue().asDouble());
        }
        throw new UnsupportedOperationException("Unknown type " + valueType);
    }

    public static CallDO fromRow(Row row) {
        CallDO orderDO = new CallDO();
        orderDO.setCellNumber(getPrimaryKeyColumnValue(row, CELL_NUMBER, Long.class));
        orderDO.setStartTime(getLatestColumnValue(row, START_TIME, Long.class));
        orderDO.setCalledNumber(getLatestColumnValue(row, CALLED_NUMBER, Long.class));
        orderDO.setDuration(getLatestColumnValue(row, DURATION, Long.class));
        orderDO.setBaseStationNumber(getLatestColumnValue(row, BASE_STATION_NUMBER, Long.class));
        return orderDO;
    }

    @Override
    public String toString() {
        try {
            return Utils.OBJECT_MAPPER.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return super.toString();
        }
    }
}
