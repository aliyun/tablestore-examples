package com.aliyun.tablestore.chart.service;

public interface IRelationService {

    public void establishFriendship(String userA, String userB, String timelineId);

    public void breakupFriendship(String userA, String userB);

    public void joinGroup(String userId, String groupTimelineId);

    public void leaveGroup(String userId, String groupTimelineId);
}
