package com.aliyun.tablestore.examples.service;


import com.aliyun.tablestore.examples.bean.OrderContract;
import com.aliyun.tablestore.examples.dao.user.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class RdsService {

    @Autowired
    private UserMapper userMapper;

    Random r = new Random();

    private volatile boolean running = true;

//    @PostConstruct
    public void initOrders() {
        while (running) {
            try {
                int size = r.nextInt(1000);
                insertIntoOrders(size);

                Thread.sleep(4000L);
            } catch (InterruptedException e) {
               break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private OrderContract createOrder() {
        long now = System.currentTimeMillis();
        LocalDateTime nowT = LocalDateTime.now();
        int cNumber = r.nextInt(1000 * 10000); // 一千万用户
        String userId = "user" + cNumber;
        String oId = now + "_" + userId;

        OrderContract item = new OrderContract();
        item.setoId(oId);
        item.setCreateTime(nowT);
        item.setPayTime(nowT);
        item.setHasPaid(true);
        item.setcId(userId);
        item.setcName("客户" + cNumber);

        int count = r.nextInt(10) + 1;
        item.setpCount(count);   // 商品数量


        double price = r.nextDouble() * 1000d;   // 单价1到1000
        item.setpPrice(price);

        int storeId = r.nextInt(5000); //5000个店铺
        item.setsId("store" + storeId);
        item.setsName("旗舰店" + storeId);
        item.setTotalPrice(item.getpPrice() * item.getpCount());

        int brandId = r.nextInt(5000);
        item.setpBrand("品牌" + brandId);

        int productId = r.nextInt(100);
        item.setpId(item.getsId() +"_" + productId);
        item.setpName("产品" + item.getpId());

        return item;
    }



    public void insertIntoOrders(int size) {
        System.out.println("start insert orders");
        List<OrderContract> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            OrderContract order = createOrder();
            list.add(order);
        }

        userMapper.batchInsert(list);
        System.out.println("finish insert orders.");
    }

    @PreDestroy
    private void destroy(){
        running = false;
    }
}
