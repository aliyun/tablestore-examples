package com.aliyun.tablestore.grid.utils;

import com.alicloud.openservices.tablestore.model.ColumnValue;

public class ValueUtil {

    public static ColumnValue toColumnValue(Object value) {
        if (value instanceof Long) {
            return ColumnValue.fromLong((Long) value);
        } else if (value instanceof Integer) {
            return ColumnValue.fromLong(((Integer) value).longValue());
        } else if (value instanceof Double) {
            return ColumnValue.fromDouble((Double) value);
        } else if (value instanceof String) {
            return ColumnValue.fromString((String) value);
        } else if (value instanceof Boolean) {
            return ColumnValue.fromBoolean((Boolean) value);
        } else if (value instanceof byte[]) {
            return ColumnValue.fromBinary((byte[]) value);
        } else {
            throw new IllegalArgumentException("unsupported type: " + value.getClass());
        }
    }
    
    public static Object toObject(ColumnValue value) {
        switch (value.getType()) {
            case INTEGER: {
                return value.asLong();
            }
            case STRING: {
                return value.asString();
            }
            case BOOLEAN: {
                return value.asBoolean();
            }
            case DOUBLE: {
                return value.asDouble();
            }
            case BINARY: {
                return value.asBinary();
            }
            default: {
                throw new RuntimeException("unexpected");
            }
        }
    }
}
