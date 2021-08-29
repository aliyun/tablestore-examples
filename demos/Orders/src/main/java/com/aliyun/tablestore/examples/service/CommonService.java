package com.aliyun.tablestore.examples.service;

import com.aliyun.tablestore.examples.bean.ConsumerOrderCount;
import com.aliyun.tablestore.examples.dao.dla.DLAMapper;
import com.aliyun.tablestore.examples.dao.user.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommonService {

    @Autowired
    UserMapper userMapper;

    @Autowired
    DLAMapper dLAMapper;

    public void testMysql() {
        int c = userMapper.count();
        System.out.println("count is:" + c);
    }


    public void testDla() {
        List<ConsumerOrderCount> list = dLAMapper.getOrderByConsumers();

        System.out.println("A");

    }

}
