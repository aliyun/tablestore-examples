package com.aliyun.tablestore.example;

import com.alicloud.openservices.tablestore.timeline.model.TimelineMessage;
import com.aliyun.tablestore.chart.TimelineV2;
import com.aliyun.tablestore.chart.model.*;
import com.aliyun.tablestore.chart.service.MessageService;
import com.aliyun.tablestore.chart.service.RelationService;
import com.aliyun.tablestore.chart.service.UserService;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.aliyun.tablestore.chart.Util.longToDateString;

public class ClientRequestExample extends BaseExample {
    private TimelineV2 timelineV2;
    private UserService userService;
    private RelationService relationService;
    private MessageService messageService;

    public static void main(String[] args) throws InterruptedException {
        ClientRequestExample example = new ClientRequestExample();

        example.timelineV2 = new TimelineV2(example.syncClient);
        example.userService = new UserService(example.syncClient, example.timelineV2);
        example.relationService = new RelationService(example.syncClient);
        example.messageService = new MessageService(example.timelineV2);

        {
            example.insert10Users();
            example.create5GroupConversation();

            example.makeFriendWithUser();
            example.joinGroupAndListGroupUsers();
        }

        {
            example.listMySingleConversations();
            example.listMyGroupConversations();
        }

        {
            example.sendSingleConversationMessage();
            example.sendGroupConversationMessage();

            example.checkSyncTableAndCountUnreadMessages();
            example.showConversationMessagesByChartWindow();
        }

        {

            try {
                System.out.println("wait for 10 seconds ...");
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            example.searchContentInCertainConversation();
            example.searchGroupConversationByName();
        }
    }


    public void insert10Users() {
        System.out.println("\n[Create New Users]");
        for (int i = 1; i <= 10; i++) {
            User user = new User()
                    .setUserId("user_" + i)
                    .setUserName("名字" + i)
                    .setSexuality(i % 2 == 0 ? Sexuality.FEMALE : Sexuality.MALE);

            userService.createUser(user);
        }

        for (int i = 1; i <= 10; i++) {
            User user = userService.describeUser("user_" + i);
            System.out.println(String.format("\tuser_id: %s, user_name: %s, sexuality: %s", user.getUserId(), user.getUserName(), user.getSexuality()));
        }
    }

    public void create5GroupConversation() {
        System.out.println("\n[Create New Groups]");
        for (int i = 1; i <= 5; i++) {
            String groupId = "group_" + i;
            String groupName = "表格存储群" + i;

            Group group = new Group(groupId, groupName).setCreatedTime(System.currentTimeMillis());

            userService.createGroupConversation(group);

            relationService.joinGroup("user_1", groupId);
            relationService.joinGroup("user_2", groupId);
            relationService.joinGroup("user_3", groupId);

            System.out.println(String.format(
                    "\tgroup_id: %s, group_name: %s, create_time: %s  ->   user_1,user_2,user_3 joinGroup",
                    group.getTimelineId(), group.getGroupName(), group.getCreatedTime()));
        }
    }

    public void makeFriendWithUser() {
        System.out.println("\n[Create New SingleConversation]");
        User mainUser = userService.describeUser("user_1");

        for (int i = 2; i <= 5; i++) {
            String timelineId = "single_" + i;
            User subUser = userService.describeUser("user_" + i);

            userService.createSingleConversation(timelineId, mainUser, subUser);
            relationService.establishFriendship(mainUser.getUserId(), subUser.getUserId(), timelineId);
        }

        {
            System.out.println("\t[Establish Friendship] user_1 make friend with: user_2 to user_5");
            printSingleConversations("user_1");
            printSingleConversations("user_2");
        }

        {
            relationService.breakupFriendship("user_1", "user_5");
            System.out.println("\t[Breakup Friendship] user_1 breakup with user_5");
            printSingleConversations("user_1");
            printSingleConversations("user_5");
        }
    }


    public void joinGroupAndListGroupUsers() {
        System.out.println("\n[New User Join Group and Check]");
        String groupId = "group_1";

        {
            relationService.leaveGroup("user_4", groupId);
            printGroupUsers("Before", groupId);
        }

        {
            relationService.joinGroup("user_4", groupId);
            printGroupUsers("After", groupId);
        }
    }


    public void listMySingleConversations() {
        System.out.println("\n[List User Single Conversation]:  -> user_1");
        String userId = "user_1";

        List<Conversation> conversations = userService.listMySingleConversations(userId);

        for (Conversation conversation : conversations) {
            String timelineId = conversation.getTimelineId();
            TimelineType type = conversation.getType();
            User user = conversation.getUser();

            System.out.println(String.format("\t[timelineId: %s]: type: %s, userId: %s, userName: %s, sexuality: %s",
                    timelineId, type, user.getUserId(), user.getUserName(), user.getSexuality()));
        }
        System.out.println(String.format("\t[Total Count]: %d", conversations.size()));

    }

    public void listMyGroupConversations() {
        System.out.println("\n[List User Group Conversation]:  -> user_1");
        String userId = "user_1";

        List<Conversation> conversations = userService.listMyGroupConversations(userId);

        for (Conversation conversation : conversations) {
            String timelineId = conversation.getTimelineId();
            TimelineType type = conversation.getType();
            Group group = conversation.getGroup();

            System.out.println(String.format("\t[timelineId: %s]: type: %s, groupName: %s, createAt: %s",
                    timelineId, type, group.getGroupName(), longToDateString(group.getCreatedTime())));
        }
        System.out.println(String.format("\t[Total Count]: %d", conversations.size()));

    }


    public void searchGroupConversationByName() {
        System.out.println("\n[Search Group Conversation by Name]:  -> '群2'");

        List<Conversation> conversations = userService.searchGroupConversation("群2");

        for (Conversation conversation : conversations) {
            String timelineId = conversation.getTimelineId();
            TimelineType type = conversation.getType();
            Group group = conversation.getGroup();

            System.out.println(String.format("\t[timelineId: %s]: type: %s, groupName: %s, createAt: %s",
                    timelineId, type, group.getGroupName(), longToDateString(group.getCreatedTime())));
        }
        System.out.println(String.format("\t[Total Count]: %d", conversations.size()));
    }


    public void sendSingleConversationMessage() {
        System.out.println("\n[Send SingleConversation Message]");
        User receiver = userService.describeUser("user_1");

        for (int i = 2; i <= 5; i++) {
            String userId = "user_" + i;
            User sender = userService.describeUser(userId);
            String timelineId = userService.getSingleConversationTimelineId(sender.getUserId(), receiver.getUserId());

            TimelineMessage message = new TimelineMessage()
                    .setField("text", "我们是好朋友了" + i)
                    .setField("type", TimelineType.SINGLE.toString())
                    .setField("sender", sender.getUserId())
                    .setField("send_time", System.currentTimeMillis())
                    .setField("conversation", sender.getUserId());

            AppMessage appMessage = new AppMessage(timelineId, message);

            if (timelineId != null) {
                messageService.sendSingleMessage(receiver.getUserId(), appMessage);
                System.out.println(String.format("\t[%s told %s]: %s", sender.getUserId(), receiver.getUserId(),
                        appMessage.getTimelineMessage().getString("text")));
            } else {
                System.out.println(String.format("\t[%s told %s]: %s", sender.getUserId(), receiver.getUserId(),
                        appMessage.getTimelineMessage().getString("text") + "\t\tFailed Cause Not Friend Yet!"));
            }
        }
    }


    public void sendGroupConversationMessage() {
        System.out.println("\n[Send GroupConversation Message]");
        String groupId = "group_1";
        Group group = userService.describeGroup(groupId);

        {
            User sender = userService.describeUser("user_1");

            TimelineMessage message = new TimelineMessage()
                    .setField("text", "今天谁使用表格存储了")
                    .setField("type", TimelineType.GROUP.toString())
                    .setField("sender", sender.getUserId())
                    .setField("send_time", System.currentTimeMillis())
                    .setField("conversation", group.getTimelineId());

            AppMessage appMessage = new AppMessage(groupId, message);

            messageService.sendGroupMessage(appMessage, userService);
            System.out.println(String.format("\t[%s told %s]: %s", sender.getUserId(), groupId,
                    appMessage.getTimelineMessage().getString("text")));
        }

        for (int i = 2; i <= 4; i ++) {
            User sender = userService.describeUser("user_" + i);

            TimelineMessage message = new TimelineMessage()
                    .setField("text", "表格+1")
                    .setField("type", TimelineType.GROUP.toString())
                    .setField("sender", sender.getUserId())
                    .setField("send_time", System.currentTimeMillis())
                    .setField("conversation", group.getTimelineId());

            AppMessage appMessage = new AppMessage(groupId, message);

            messageService.sendGroupMessage(appMessage, userService);
            System.out.println(String.format("\t[%s told %s]: %s", sender.getUserId(), groupId,
                    appMessage.getTimelineMessage().getString("text")));
        }

    }


    public void checkSyncTableAndCountUnreadMessages() {
        System.out.println("\n[Count Unread Message]");

        long syncCheckpoint = 0;
        Map<String, Object> newMesageAgg = new HashMap<String, Object>();
        List<AppMessage> messages = messageService.fetchSyncMessage("user_1", syncCheckpoint + 1);

        for (AppMessage appMessage : messages) {
            String conversation = appMessage.getTimelineMessage().getString("conversation");
            if (!newMesageAgg.containsKey(conversation)) {
                Map<String, Object> counter = new HashMap<String, Object>();
                counter.put("unread", 0);

                newMesageAgg.put(conversation, counter);
            }

            Map<String, Object> counter = (Map<String, Object>)newMesageAgg.get(conversation);
            counter.put("unread", (Integer)counter.get("unread") + 1);
            counter.put("latestMessage", appMessage.getTimelineMessage().getString("text"));

            syncCheckpoint = appMessage.getSequenceId() > syncCheckpoint ? (appMessage.getSequenceId()) : syncCheckpoint;
        }

        {
            for (String conversation : newMesageAgg.keySet()) {
                Map<String, Object> counter = (Map<String, Object>)newMesageAgg.get(conversation);
                System.out.println(String.format("\tconversation: %s, newUnread: %d, latestMessage: %s",
                        conversation, (Integer)counter.get("unread"), (String)counter.get("latestMessage")));
            }
            System.out.println(String.format("\t[Total Count]: %s, [Update SyncCheckpoint]: %d", messages.size(), syncCheckpoint));

        }
    }


    public void showConversationMessagesByChartWindow() {
        System.out.println("\n[Scan Conversation Message]: -> group_1");

        long conversationCheckpoint = Long.MAX_VALUE;
        String conversation = "group_1";

        List<AppMessage> messages = messageService.fetchConversationMessage(conversation, conversationCheckpoint - 1);

        for (AppMessage appMessage : messages) {
            String sender = appMessage.getTimelineMessage().getString("sender");
            String text = appMessage.getTimelineMessage().getString("text");
            long sendTime = appMessage.getTimelineMessage().getLong("send_time");

            conversationCheckpoint = appMessage.getSequenceId();

            System.out.println(String.format("\t[%s say @%s]: %s", sender, longToDateString(sendTime), text));
        }
        System.out.println(String.format("\t[Total Count]: %s, [Update SyncCheckpoint]: %d", messages.size(), conversationCheckpoint));
    }


    public void searchContentInCertainConversation() {
        System.out.println("\n[Search Message in Conversation]: -> group_1 -> '表格'");

        String conversation = "group_1";

        List<AppMessage> messages = messageService.searchMessage(conversation, "表格");
        for (AppMessage appMessage : messages) {
            String sender = appMessage.getTimelineMessage().getString("sender");
            String text = appMessage.getTimelineMessage().getString("text");
            long sendTime = appMessage.getTimelineMessage().getLong("send_time");

            System.out.println(String.format("\t[%s say @%s]: %s", sender, longToDateString(sendTime), text));
        }
        System.out.println(String.format("\t[Total Count]: %s", messages.size()));
    }


    private void printGroupUsers(String status, String groupId)         {
        List<User> userList = userService.listGroupUsers(groupId);
        List<String> userInfoList = new LinkedList<String>();

        for (User user : userList) {
            userInfoList.add(user.getUserId() + ":" + user.getUserName() + ":" + user.getSexuality());
        }

        System.out.println(String.format("\t[%s Join] group_id: %s, userIds: %s", status, groupId, userInfoList.toString()));
    }


    private void printSingleConversations(String userId)         {
        List<Conversation> conversations = userService.listMySingleConversations(userId);
        List<String> conInfoList = new LinkedList<String>();

        for (Conversation con : conversations) {
            conInfoList.add(con.getTimelineId() + ":" + con.getType() + ":" + con.getUser().getUserId() + ":" + con.getUser().getUserName());
        }
        System.out.println(String.format("\t\t[Friends List] user_id: %s, friends: %s", userId, conInfoList.toString()));
    }

}
