package com.aliyun.tablestore.example.model;

import com.alicloud.openservices.tablestore.model.*;
import com.aliyun.tablestore.example.utils.Utils;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.codec.digest.DigestUtils;

import static com.aliyun.tablestore.example.consts.ColumnConsts.*;

public class OrderDO {

    private String orderId;

    private Long orderStatus;

    private Long orderTime;

    private Long payTime;

    private Long deliverTime;

    private Long receiveTime;

    private String productId;

    private String productName;

    private String productType;

    private String consumerId;

    private String consumerName;

    private String consumerCell;

    private String consumerAddress;

    public String getOrderId() {
        return orderId;
    }

    public OrderDO setOrderId(String orderId) {
        this.orderId = orderId;
        return this;
    }

    public Long getOrderStatus() {
        return orderStatus;
    }

    public OrderDO setOrderStatus(Long orderStatus) {
        this.orderStatus = orderStatus;
        return this;
    }

    public Long getOrderTime() {
        return orderTime;
    }

    public OrderDO setOrderTime(Long orderTime) {
        this.orderTime = orderTime;
        return this;
    }

    public Long getPayTime() {
        return payTime;
    }

    public OrderDO setPayTime(Long payTime) {
        this.payTime = payTime;
        return this;
    }

    public Long getDeliverTime() {
        return deliverTime;
    }

    public OrderDO setDeliverTime(Long deliverTime) {
        this.deliverTime = deliverTime;
        return this;
    }

    public Long getReceiveTime() {
        return receiveTime;
    }

    public OrderDO setReceiveTime(Long receiveTime) {
        this.receiveTime = receiveTime;
        return this;
    }

    public String getProductId() {
        return productId;
    }

    public OrderDO setProductId(String productId) {
        this.productId = productId;
        return this;
    }

    public String getProductName() {
        return productName;
    }

    public OrderDO setProductName(String productName) {
        this.productName = productName;
        return this;
    }

    public String getProductType() {
        return productType;
    }

    public OrderDO setProductType(String productType) {
        this.productType = productType;
        return this;
    }

    public String getConsumerId() {
        return consumerId;
    }

    public OrderDO setConsumerId(String consumerId) {
        this.consumerId = consumerId;
        return this;
    }

    public String getConsumerName() {
        return consumerName;
    }

    public OrderDO setConsumerName(String consumerName) {
        this.consumerName = consumerName;
        return this;
    }

    public String getConsumerCell() {
        return consumerCell;
    }

    public OrderDO setConsumerCell(String consumerCell) {
        this.consumerCell = consumerCell;
        return this;
    }

    public String getConsumerAddress() {
        return consumerAddress;
    }

    public OrderDO setConsumerAddress(String consumerAddress) {
        this.consumerAddress = consumerAddress;
        return this;
    }

    public RowPutChange toRowPutChange(String tableName) {
        RowPutChange rowPutChange = new RowPutChange(tableName);
        // primary key
        PrimaryKey primaryKey = new PrimaryKey(new PrimaryKeyColumn[]{
                new PrimaryKeyColumn(ORDER_ID_MD5, PrimaryKeyValue.fromString(DigestUtils.md5Hex(orderId))),
                new PrimaryKeyColumn(ORDER_ID, PrimaryKeyValue.fromString(orderId))
        });
        rowPutChange.setPrimaryKey(primaryKey);
        // attribute columns
        rowPutChange
                .addColumn(ORDER_STATUS, ColumnValue.fromLong(orderStatus))
                .addColumn(ORDER_TIME, ColumnValue.fromLong(orderTime))
                .addColumn(PAY_TIME, ColumnValue.fromLong(payTime))
                .addColumn(DELIVER_TIME, ColumnValue.fromLong(deliverTime))
                .addColumn(RECEIVE_TIME, ColumnValue.fromLong(receiveTime))
                .addColumn(PRODUCT_ID, ColumnValue.fromString(productId))
                .addColumn(PRODUCT_NAME, ColumnValue.fromString(productName))
                .addColumn(PRODUCT_TYPE, ColumnValue.fromString(productType))
                .addColumn(CONSUMER_ID, ColumnValue.fromString(consumerId))
                .addColumn(CONSUMER_NAME, ColumnValue.fromString(consumerName))
                .addColumn(CONSUMER_CELL, ColumnValue.fromString(consumerCell))
                .addColumn(CONSUMER_ADDRESS, ColumnValue.fromString(consumerAddress));
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

    public static OrderDO fromRow(Row row) {
        OrderDO orderDO = new OrderDO();
        orderDO.setOrderId(getPrimaryKeyColumnValue(row, ORDER_ID, String.class));
        orderDO.setOrderStatus(getLatestColumnValue(row, ORDER_STATUS, Long.class));
        orderDO.setOrderTime(getLatestColumnValue(row, ORDER_TIME, Long.class));
        orderDO.setPayTime(getLatestColumnValue(row, PAY_TIME, Long.class));
        orderDO.setDeliverTime(getLatestColumnValue(row, DELIVER_TIME, Long.class));
        orderDO.setReceiveTime(getLatestColumnValue(row, RECEIVE_TIME, Long.class));
        orderDO.setProductId(getLatestColumnValue(row, PRODUCT_ID, String.class));
        orderDO.setProductName(getLatestColumnValue(row, PRODUCT_NAME, String.class));
        orderDO.setProductType(getLatestColumnValue(row, PRODUCT_TYPE, String.class));
        orderDO.setConsumerId(getLatestColumnValue(row, CONSUMER_ID, String.class));
        orderDO.setConsumerName(getLatestColumnValue(row, CONSUMER_NAME, String.class));
        orderDO.setConsumerCell(getLatestColumnValue(row, CONSUMER_CELL, String.class));
        orderDO.setConsumerAddress(getLatestColumnValue(row, CONSUMER_ADDRESS, String.class));
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
