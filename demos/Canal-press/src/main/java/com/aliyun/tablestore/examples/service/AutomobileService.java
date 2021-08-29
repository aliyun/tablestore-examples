package com.aliyun.tablestore.examples.service;


import com.aliyun.tablestore.examples.bean.Automobile.AutomobileBean;
import com.aliyun.tablestore.examples.bean.Automobile.NextMove;
import com.aliyun.tablestore.examples.dao.user.AutomobileMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;


import javax.annotation.PreDestroy;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


@Service
public class AutomobileService {

    private Logger logger = LoggerFactory.getLogger(AutomobileService.class);

    @Autowired
    private AutomobileMapper automobileMapper;

    private Random r = new Random();

    private volatile boolean hasDestroy = false;

    public void generateData(int carNum, int point) {
       for (int i = 0; i < carNum; i++) {
           if (hasDestroy) {
               break;
           }
           try {
               String carId = i + "";
               logger.info("start generate data for car : " + carId);
               generateData(carId, point);
               logger.info("finish generate data for car : " + carId);
           } catch (Exception e) {
               logger.error("Error while generate car data", e);
           }
       }
    }


    private void generateData(String carId, int point) {
        String cardMd5 = DigestUtils.md5DigestAsHex(carId.getBytes());

        double oilConsumption =  r.nextDouble() * 0.07 + 0.05; // 油耗
        int speed = r.nextInt(80) + 40;

        LocalDateTime time = LocalDateTime.now().withSecond(0).withNano(0);

        NextMove move = null;
        AutomobileBean preRecord = buildInitRecord();

        List<AutomobileBean> list = new ArrayList<>();

        for (int i = 0; i < point && !hasDestroy; i++) {
            if (move == null || move.getMinuteLeft() <= 0) {
                move = getNextMove();
            }

            AutomobileBean nextRecord = buildNextRecord(move, preRecord, speed, oilConsumption);
            nextRecord.setCarId(carId);

            nextRecord.setCarMd5(cardMd5);
            nextRecord.setCarTimestamp(time.toInstant(ZoneOffset.of("+8")).toEpochMilli());
            nextRecord.buildDBInfo();

            list.add(nextRecord);
            preRecord = nextRecord;
            move.passOneMinute();
            time = time.plusSeconds(1);
            if (list.size() > 1000) {
                automobileMapper.batchInsertHis(list);
                list = new ArrayList<>();
            }
        }

        if (!CollectionUtils.isEmpty(list)) {
            automobileMapper.batchInsertHis(list);
        }

        automobileMapper.insert(preRecord);
    }



    private AutomobileBean buildNextRecord(NextMove move, AutomobileBean preRecord, int speed, double oilConsumption) {
        double longitude = preRecord.getLongitude();
        double latitude = preRecord.getLatitude();
        AutomobileBean result = new AutomobileBean();

        if (!move.isStop()) {
            if (move.getDirection() == 0) {
                longitude = longitude - speed / 60.0 / 0.1 / 1000;
            } else if (move.getDirection() == 1) {
                longitude = longitude + speed / 60.0 / 0.1 / 1000;
            } else if (move.getDirection() == 2) {
                latitude = latitude - speed / 60.0 / 0.1 / 1000;
            } else if (move.getDirection() == 3) {
                latitude = latitude + speed / 60.0 / 0.1 / 1000;
            }
        }

        double oil = move.isStop() ? preRecord.getOil() : preRecord.getOil() - speed * oilConsumption / 60;
        if (oil < 10) {
            oil = oiling();
        }

        result.setLongitude(longitude);
        result.setLatitude(latitude);
        result.setVelocity(move.isStop() ? 0 : speed);
        result.setOil(oil);
        result.setMileage(move.isStop() ? preRecord.getMileage() : preRecord.getMileage() + speed / 60.0);

        result.setTemperatureIn(new BigDecimal(preRecord.getTemperatureIn() + getTemperatureChange()).setScale(1, BigDecimal.ROUND_HALF_DOWN).doubleValue());
        result.setTemperatureOut(new BigDecimal(preRecord.getTemperatureOut() + getTemperatureChange()).setScale(1, BigDecimal.ROUND_HALF_DOWN).doubleValue());
        result.setTyrePressure(preRecord.getTyrePressure());
        result.setTimeMinute(move.isStop() ? preRecord.getTimeMinute() : preRecord.getTimeMinute() + 1);

        return result;
    }

    private double getTemperatureChange() {
        int ran = r.nextInt(10);
        if (ran == 0) {
            return 0.1;
        } else if (ran == 1) {
            return -0.1;
        } else {
            return 0.0;
        }
    }

    private AutomobileBean buildInitRecord() {

        AutomobileBean bean = new AutomobileBean();
        double longitude = r.nextDouble() * 1.3 + 120.4;
        double latitude = r.nextDouble() * 0.81 + 30.88;

        bean.setLatitude(latitude);
        bean.setLongitude(longitude);

        double temperatureIn = r.nextDouble() * 3 + 24;
        bean.setTemperatureIn(new BigDecimal(temperatureIn).setScale(1, BigDecimal.ROUND_HALF_DOWN).doubleValue());
        double temperatureOut = r.nextDouble() * 20 + 10;
        bean.setTemperatureOut(new BigDecimal(temperatureOut).setScale(1, BigDecimal.ROUND_HALF_DOWN).doubleValue());
        bean.setMileage(r.nextInt(10000));
        bean.setTyrePressure(r.nextDouble() * 0.3 + 2.2);
        bean.setOil(oiling());
        bean.setTimeMinute(0);

        return bean;
    }

    private double oiling() {
        return r.nextDouble() * 30 + 100;
    }

    private NextMove getNextMove() {
        boolean stop = r.nextInt(20) <= 5;
        int minute = r.nextInt(170) + 30;
        int direction = r.nextInt(4);

        NextMove m = new NextMove();
        m.setStop(stop);
        m.setMinuteLeft(minute);
        m.setDirection(direction);
        return m;
    }

    @PreDestroy
    private void destroy() {
        hasDestroy = true;
        logger.info(this.getClass().getSimpleName() + "will be destroyed");
    }

    public void createTable() {
        automobileMapper.createTableIfNotExitsAutoMobile();
        automobileMapper.createTableIfNotExitsHisAutoMobile();
    }
}
