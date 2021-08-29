package com.aliyun.tablestore.examples.dao.user;


import com.aliyun.tablestore.examples.bean.OrderContract;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author:wjup
 * @Date: 2018/9/26 0026
 * @Time: 15:20
 */
@Mapper
public interface UserMapper {

    void batchInsertCanal(@Param("list") List<OrderContract> list);
}