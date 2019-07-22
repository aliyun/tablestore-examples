package com.aliyun.tablestore.chart.service;

import com.aliyun.tablestore.chart.model.Conversation;
import com.aliyun.tablestore.chart.model.Group;
import com.aliyun.tablestore.chart.model.User;

import java.util.List;

public interface IUserService {

    public User createUser(User user);

    public User describeUser(String userId);

    public List<User> listGroupUsers(String groupId);

    public String getSingleConversationTimelineId(String mainUser, String subUser);

    public Group describeGroup(String timelineId);

    public Conversation describeConversation(String userId, String timelineId);

    public void createGroupConversation(Group group);

    public void createSingleConversation(String timelineId, User mainUser, User subUser);

    public List<Conversation> listMySingleConversations(String userId);

    public List<Conversation> listMyGroupConversations(String userId);

    public List<Conversation> searchGroupConversation(String groupName);

}
