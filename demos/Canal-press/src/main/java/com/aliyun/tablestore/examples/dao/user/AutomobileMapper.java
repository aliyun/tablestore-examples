package com.aliyun.tablestore.examples.dao.user;

import com.aliyun.tablestore.examples.bean.Automobile.AutomobileBean;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AutomobileMapper {

    void batchInsertHis(List<AutomobileBean> list);

    void insert(@Param("item") AutomobileBean bean);

    void createTableIfNotExitsAutoMobile();

    void createTableIfNotExitsHisAutoMobile();
}
