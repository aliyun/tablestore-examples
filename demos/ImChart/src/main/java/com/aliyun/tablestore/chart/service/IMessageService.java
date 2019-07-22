package com.aliyun.tablestore.chart.service;

import com.aliyun.tablestore.chart.model.AppMessage;

import java.util.List;

public interface IMessageService {

    /**
     * 发送消息后，写入存储库，然后依据会话用户列表写扩散到同步库
     *
     * 写扩散方式建议用户通过任务队列，做异步的写扩散。
     */
    public void sendSingleMessage(String friendId, AppMessage message);

    public void sendGroupMessage(AppMessage message, UserService userService);

    /**
     * 从同步库获取未读消息，依据lastSequenceId标识已读位置
     *
     * 在客户端或者应用内将消息按会话分组，统计各会话新的未读消息数，并完成会话未读数的累加。
     */
    public List<AppMessage> fetchSyncMessage(String userId, long lastSequenceId);

    /**
     * 从存储库获取消息，通过sequenceId做倒序范围查询；默认返回30条
     */
    public List<AppMessage> fetchConversationMessage(String timelineId, long sequenceId);

    public List<AppMessage> searchMessage(String timelineId, String content);
}
