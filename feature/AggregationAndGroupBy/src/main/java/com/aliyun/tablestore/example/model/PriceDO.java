package com.aliyun.tablestore.example.model;

import com.alicloud.openservices.tablestore.model.*;
import com.aliyun.tablestore.example.utils.Utils;
import com.fasterxml.jackson.core.JsonProcessingException;

import static com.aliyun.tablestore.example.consts.ColumnConsts.*;

public class PriceDO {

    private Long id;

    private String name;

    private Double price;

    private String brand;

    private Long seller;


    public Long getId() {
        return id;
    }

    public PriceDO setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public PriceDO setName(String name) {
        this.name = name;
        return this;
    }

    public Double getPrice() {
        return price;
    }

    public PriceDO setPrice(Double price) {
        this.price = price;
        return this;
    }

    public String getBrand() {
        return brand;
    }

    public PriceDO setBrand(String brand) {
        this.brand = brand;
        return this;
    }

    public Long getSeller() {
        return seller;
    }

    public PriceDO setSeller(Long seller) {
        this.seller = seller;
        return this;
    }

    public RowPutChange toRowPutChange(String tableName) {
        RowPutChange rowPutChange = new RowPutChange(tableName);
        // primary key
        PrimaryKey primaryKey = new PrimaryKey(new PrimaryKeyColumn[]{
                new PrimaryKeyColumn(ID, PrimaryKeyValue.fromLong(id))
        });
        rowPutChange.setPrimaryKey(primaryKey);
        // attribute columns
        rowPutChange
                .addColumn(NAME, ColumnValue.fromString(name))
                .addColumn(PRICE, ColumnValue.fromDouble(price))
                .addColumn(BRAND, ColumnValue.fromString(brand))
                .addColumn(SELLER, ColumnValue.fromLong(seller));
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

    public static PriceDO fromRow(Row row) {
        PriceDO priceDO = new PriceDO();
        priceDO.setId(getPrimaryKeyColumnValue(row, ID, Long.class));
        priceDO.setName(getLatestColumnValue(row, NAME, String.class));
        priceDO.setPrice(getLatestColumnValue(row, PRICE, Double.class));
        priceDO.setBrand(getLatestColumnValue(row, BRAND, String.class));
        priceDO.setSeller(getLatestColumnValue(row, SELLER, Long.class));
        return priceDO;
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
