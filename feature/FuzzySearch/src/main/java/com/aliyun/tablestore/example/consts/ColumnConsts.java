package com.aliyun.tablestore.example.consts;

/**
 * @author hydrogen
 */
public interface ColumnConsts {
    // pk columns
    String ORDER_ID_MD5 = "order_id_md5";
    String ORDER_ID = "order_id";

    // attribute columns
    String ORDER_STATUS = "order_status";

    String ORDER_TIME = "order_time";
    String PAY_TIME = "pay_time";
    String DELIVER_TIME = "deliver_time";
    String RECEIVE_TIME = "receive_time";

    String PRODUCT_ID = "product_id";
    String PRODUCT_NAME = "product_name";
    String PRODUCT_TYPE = "product_type";

    String CONSUMER_ID = "consumer_id";
    String CONSUMER_NAME = "consumer_name";
    String CONSUMER_CELL = "consumer_cell";
    String CONSUMER_ADDRESS = "address";
}
