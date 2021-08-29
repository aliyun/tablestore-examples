package com.aliyun.tablestore.examples.controller;


import com.aliyun.tablestore.examples.bean.BrandAndPriceRange;
import com.aliyun.tablestore.examples.bean.ConsumerTradeValue;
import com.aliyun.tablestore.examples.bean.OperationResult;
import com.aliyun.tablestore.examples.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/index")
public class IndexController {

    @Autowired
    private SearchService searchService;

    // 多元索引应用
    @PostMapping("/searchBrandConsumer")
    public Object searchBrandConsumer(@RequestBody String brand){
        List<String> list = searchService.getUserByBrand(brand);
        return OperationResult.toResult(list);
    }

    // 搜索购买过某品牌且商品名称中含有某关键词的客户
    @PostMapping("/searchByBrandAndKey")
    public Object searchByBrandAndKey(@RequestBody BrandAndPriceRange param){
        List<String> list = searchService.searchByBrandAndKey(param.getBrand(), param.getHigh(), param.getLow());
        return OperationResult.toResult(list);
    }

    // 搜索购买了包含关键字商品的客户
    @PostMapping("/searchByKeyInProductName")
    public Object searchByKeyInProductName(@RequestBody String key){
        List<String> list = searchService.searchByKeyInProductName(key);
        return OperationResult.toResult(list);
    }



    // 根据二级索引客户id搜索该客户成交的订单
    @PostMapping("/searchOrderByCId")
    public Object searchOrderByCId(@RequestBody String id){
        List<String> list = searchService.searchByCId(id);
        return OperationResult.toResult(list);
    }

    // 根据二级索引客户id搜索该客户成交的订单
    @PostMapping("/searchPIdByCId")
    public Object searchPIdByCId(@RequestBody String id){
        List<String> list = searchService.searchPIdByCId(id);
        return OperationResult.toResult(list);
    }

    // 统计交易量最大的100个买家
    @PostMapping("/getMaxTradeConsumers100")
    public Object getMaxTradeConsumers100(){
        List<ConsumerTradeValue> res = searchService.getMaxTradeCustomer100();
        return OperationResult.toResult(res);
    }

    // 多组合查询
    @PostMapping("/getCombinationQuery")
    public Object getCombinationQuery(){
        searchService.getCombinationQuery();
        return OperationResult.toResult(true);
    }


}
