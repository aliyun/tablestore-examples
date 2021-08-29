package com.aliyun.tablestore.examples.dao.user;


import com.aliyun.tablestore.examples.bean.OrderContract;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface UserMapper {

    Integer count();

    void insert(OrderContract item);

    void batchInsert(@Param("list") List<OrderContract> list);
}