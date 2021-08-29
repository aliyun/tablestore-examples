package com.aliyun.tablestore.examples.controller;

import com.aliyun.tablestore.examples.service.RdsService;
import com.aliyun.tablestore.examples.service.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/test")
public class TypeController {

    @Autowired
    CommonService commonService;
    @Autowired
    RdsService rdsService;


    /**
     * 测试连接MySQL
     */
    @GetMapping("/mysql")
    public void mysql(){
        commonService.testMysql();
    }

    /**
     * 测试以MySQL方式连接DLA
     */
    @GetMapping("/dla")
    public void dla(){
        commonService.testDla();
    }

    /**
     * 向MySQL表中压入订单单数
     */
    @GetMapping("/initOrders")
    public void initOrders(){
        rdsService.initOrders();
    }

}
