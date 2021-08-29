package com.aliyun.tablestore.examples.controller;


import com.aliyun.tablestore.examples.bean.OperationResult;
import com.aliyun.tablestore.examples.service.CanalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/canal")
public class CanalController {



    @Autowired
    private CanalService canalService;

    @PostMapping("/press")
    public OperationResult press(@RequestParam("rps") int rps, @RequestParam("threads") int threads){
        canalService.pressForCanal(rps, threads);
        return OperationResult.toResult(true);
    }


}
