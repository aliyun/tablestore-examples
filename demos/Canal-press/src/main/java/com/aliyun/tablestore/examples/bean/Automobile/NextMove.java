package com.aliyun.tablestore.examples.bean.Automobile;



public class NextMove {

    private boolean stop = false;

    // 分钟为单位
    private int minuteLeft;

    private int direction;

    public boolean isStop() {
        return stop;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    public int getMinuteLeft() {
        return minuteLeft;
    }

    public void setMinuteLeft(int minuteLeft) {
        this.minuteLeft = minuteLeft;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public void passOneMinute() {
        minuteLeft--;
    }
}
