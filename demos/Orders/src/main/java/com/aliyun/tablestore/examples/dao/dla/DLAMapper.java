package com.aliyun.tablestore.examples.dao.dla;

import com.aliyun.tablestore.examples.bean.ConsumerOrderCount;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DLAMapper {
    List<ConsumerOrderCount> getOrderByConsumers();
}
