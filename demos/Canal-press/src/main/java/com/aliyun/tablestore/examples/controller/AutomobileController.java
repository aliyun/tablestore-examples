package com.aliyun.tablestore.examples.controller;

import com.aliyun.tablestore.examples.bean.OperationResult;
import com.aliyun.tablestore.examples.service.AutomobileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/car")
public class AutomobileController {

    @Autowired
    private AutomobileService automobileService;

    @PostMapping("/press")
    public OperationResult press(@RequestParam("carNum") int num,
                                 @RequestParam(value = "point", defaultValue = "300") int point){
        automobileService.createTable();
        Thread t = new Thread(() ->  automobileService.generateData(num, point));
        t.start();
        return OperationResult.toResult(true);
    }



    @PostMapping("/prepareTable")
    public OperationResult press(){
        automobileService.createTable();
        return OperationResult.toResult(true);
    }

}
