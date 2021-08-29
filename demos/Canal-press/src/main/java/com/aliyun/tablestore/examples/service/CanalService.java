package com.aliyun.tablestore.examples.service;

import com.aliyun.tablestore.examples.bean.OrderContract;
import com.aliyun.tablestore.examples.dao.user.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class CanalService {

    @Autowired
    private UserMapper userMapper;

    private Random r = new Random();

    private volatile boolean running = true;

    public void pressForCanal(int rps, int threads) {
        ExecutorService service = Executors.newFixedThreadPool(threads);
        for (int i = 0; i < threads; i++) {
            service.submit(()->runInWhile(rps));
        }
    }

    private void runInWhile(int rps) {

        while (running) {
            try {
                long t1= System.currentTimeMillis();
                System.out.println("start insert orders");
                List<OrderContract> list = new ArrayList<>();
                Set<String> keys = new HashSet<>();
                for (int i = 0; i < rps; i++) {
                    OrderContract order = createOrder();
                    if (keys.contains(order.getoId())) {
                        i--;
                    } else {
                        keys.add(order.getoId());
                        list.add(order);
                    }
                }

                userMapper.batchInsertCanal(list);
                System.out.println("finish insert orders. Size :" + list.size());

                long t2 = System.currentTimeMillis();
                long useTime = t2 - t1;
                if (useTime > 1000L) {
                    System.out.println("using:" + useTime);

                } else {
                    try {
                        System.out.println("sleeping:" + (1000L - useTime) + "(ms)");
                        Thread.sleep(1000L - useTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Thread.currentThread().interrupt();
                    }
                }
            } catch (Exception e) {
                System.out.println("ABC:"+Thread.currentThread().isInterrupted());
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

    @PreDestroy
    public void destory() {
        running = false;
    }

}
