package com.aliyun.tablestore.chart.model;

public class User {
    private String userId;
    private String userName;
    private Sexuality sexuality;

    public String getUserId() {
        return userId;
    }

    public User setUserId(String userId) {
        this.userId = userId;

        return this;
    }

    public String getUserName() {
        return userName;
    }

    public User setUserName(String userName) {
        this.userName = userName;

        return this;
    }

    public Sexuality getSexuality() {
        return sexuality;
    }

    public User setSexuality(Sexuality sexuality) {
        this.sexuality = sexuality;

        return this;
    }
}
