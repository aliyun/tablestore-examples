package com.aliyun.tablestore.chart.service;

import com.alibaba.fastjson.JSONArray;
import com.alicloud.openservices.tablestore.SyncClient;
import com.alicloud.openservices.tablestore.model.*;
import com.alicloud.openservices.tablestore.model.search.SearchQuery;
import com.alicloud.openservices.tablestore.model.search.query.BoolQuery;
import com.alicloud.openservices.tablestore.model.search.query.MatchPhraseQuery;
import com.alicloud.openservices.tablestore.model.search.query.TermQuery;
import com.alicloud.openservices.tablestore.timeline.model.TimelineIdentifier;
import com.alicloud.openservices.tablestore.timeline.model.TimelineMeta;
import com.alicloud.openservices.tablestore.timeline.query.SearchResult;
import com.aliyun.tablestore.chart.TimelineV2;
import com.aliyun.tablestore.chart.model.Conversation;
import com.aliyun.tablestore.chart.model.TimelineType;
import com.aliyun.tablestore.chart.model.User;
import com.aliyun.tablestore.chart.common.ExampleCons;
import com.aliyun.tablestore.chart.model.Group;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static com.aliyun.tablestore.chart.Util.*;

public class UserService implements IUserService {
    private SyncClient syncClient;
    private TimelineV2 timelineV2;

    private String userTableName = ExampleCons.USER_TABLE;
    private String userRelationTable = ExampleCons.USER_RELATION_TABLE;
    private String groupRelationTable = ExampleCons.GROUP_RELATION_TABLE;
    private String groupRelationGlobalIndex = ExampleCons.GROUP_RELATION_GLOBAL_INDEX;

    public UserService(SyncClient syncClient, TimelineV2 timelineV2) {
        this.syncClient = syncClient;
        this.timelineV2 = timelineV2;
    }

    public User createUser(User user) {
        RowPutChange rowPutChange = userToRowPutChange(userTableName, user);

        PutRowRequest request = new PutRowRequest(rowPutChange);
        syncClient.putRow(request);

        return user;
    }

    public User describeUser(String userId) {
        User user = null;
        PrimaryKey primaryKey = PrimaryKeyBuilder.createPrimaryKeyBuilder()
                .addPrimaryKeyColumn("user_id", PrimaryKeyValue.fromString(userId))
                .build();

        SingleRowQueryCriteria criteria = new SingleRowQueryCriteria(userTableName);
        criteria.setPrimaryKey(primaryKey);
        criteria.setMaxVersions(1);

        GetRowRequest request = new GetRowRequest();
        request.setRowQueryCriteria(criteria);

        GetRowResponse response = syncClient.getRow(request);

        if (response.getRow() != null) {
            user = rowToUser(response.getRow());
        }

        return user;
    }

    public List<User> listGroupUsers(String groupId) {
        PrimaryKey start = PrimaryKeyBuilder.createPrimaryKeyBuilder()
                .addPrimaryKeyColumn("group_id", PrimaryKeyValue.fromString(groupId))
                .addPrimaryKeyColumn("user_id", PrimaryKeyValue.INF_MIN)
                .build();

        PrimaryKey end = PrimaryKeyBuilder.createPrimaryKeyBuilder()
                .addPrimaryKeyColumn("group_id", PrimaryKeyValue.fromString(groupId))
                .addPrimaryKeyColumn("user_id", PrimaryKeyValue.INF_MAX)
                .build();

        RangeRowQueryCriteria criteria = new RangeRowQueryCriteria(groupRelationTable);
        criteria.setInclusiveStartPrimaryKey(start);
        criteria.setExclusiveEndPrimaryKey(end);
        criteria.setMaxVersions(1);
        criteria.setLimit(100);
        criteria.setDirection(Direction.FORWARD);
        criteria.addColumnsToGet(new String[] {"user_id"});

        GetRangeRequest request = new GetRangeRequest(criteria);

        GetRangeResponse response = syncClient.getRange(request);

        List<User> users = new ArrayList<User>(response.getRows().size());

        for (Row row : response.getRows()) {
            String userId = row.getPrimaryKey().getPrimaryKeyColumn("user_id").getValue().asString();
            User user = describeUser(userId);

            users.add(user);
        }

        return users;
    }

    public String getSingleConversationTimelineId(String mainUser, String subUser) {
        PrimaryKey primaryKey = PrimaryKeyBuilder.createPrimaryKeyBuilder()
                .addPrimaryKeyColumn("main_user", PrimaryKeyValue.fromString(mainUser))
                .addPrimaryKeyColumn("sub_user", PrimaryKeyValue.fromString(subUser))
                .build();

        SingleRowQueryCriteria criteria = new SingleRowQueryCriteria(userRelationTable);
        criteria.setPrimaryKey(primaryKey);
        criteria.addColumnsToGet(new String[] {"timeline_id"});
        criteria.setMaxVersions(1);

        GetRowRequest request = new GetRowRequest(criteria);

        GetRowResponse response = syncClient.getRow(request);
        Row row = response.getRow();

        if (row != null) {
            return row.getColumn("timeline_id").get(0).getValue().asString();
        }

        return null;
    }

    public Group describeGroup(String timelineId) {
        TimelineIdentifier identifier = new TimelineIdentifier.Builder()
                .addField("timeline_id", timelineId)
                .build();

        TimelineMeta meta = timelineV2.getTimelineMetaStoreInstance().read(identifier);

        if (meta != null) {
            String type = meta.getString("type");
            TimelineType timelineType = TimelineType.valueOf(type);

            if (TimelineType.GROUP.equals(timelineType)) {
                return timelineMetaToGroup(meta);
            }
        }

        return null;
    }

    public Conversation describeConversation(String userId, String timelineId) {
        TimelineIdentifier identifier = new TimelineIdentifier.Builder()
                .addField("timeline_id", timelineId)
                .build();

        TimelineMeta meta = timelineV2.getTimelineMetaStoreInstance().read(identifier);

        if (meta != null) {
            String type = meta.getString("type");
            TimelineType timelineType = TimelineType.valueOf(type);

            if (TimelineType.GROUP.equals(timelineType)) {
                Group group = timelineMetaToGroup(meta);

                return new Conversation(timelineId, group);
            } else {
                String users = meta.getString("users");
                List<String> userList = JSONArray.parseArray(users, String.class);
                String friendId = userList.get(1 - userList.indexOf(userId));

                User user = describeUser(friendId);

                return new Conversation(timelineId, user);
            }
        }

        return null;
    }

    public void createGroupConversation(Group group) {
        TimelineIdentifier identifier = new TimelineIdentifier.Builder()
                .addField("timeline_id", group.getTimelineId())
                .build();

        TimelineMeta meta = new TimelineMeta(identifier)
                .setField("type", TimelineType.GROUP.toString())
                .setField("create_time", System.currentTimeMillis());

        meta.setField("group_name", group.getGroupName());

        timelineV2.getTimelineMetaStoreInstance().insert(meta);
    }

    public void createSingleConversation(String timelineId, User mainUser, User subUser) {
        TimelineIdentifier identifier = new TimelineIdentifier.Builder()
                .addField("timeline_id", timelineId)
                .build();

        String users = Arrays.asList(mainUser.getUserId(), subUser.getUserId()).toString();

        TimelineMeta meta = new TimelineMeta(identifier)
                .setField("type", TimelineType.SINGLE.toString())
                .setField("create_time", System.currentTimeMillis())
                .setField("users", users);

        timelineV2.getTimelineMetaStoreInstance().insert(meta);
    }

    public List<Conversation> listMySingleConversations(String userId) {
        PrimaryKey start = PrimaryKeyBuilder.createPrimaryKeyBuilder()
                .addPrimaryKeyColumn("main_user", PrimaryKeyValue.fromString(userId))
                .addPrimaryKeyColumn("sub_user", PrimaryKeyValue.INF_MIN)
                .build();

        PrimaryKey end = PrimaryKeyBuilder.createPrimaryKeyBuilder()
                .addPrimaryKeyColumn("main_user", PrimaryKeyValue.fromString(userId))
                .addPrimaryKeyColumn("sub_user", PrimaryKeyValue.INF_MAX)
                .build();

        RangeRowQueryCriteria criteria = new RangeRowQueryCriteria(userRelationTable);
        criteria.setInclusiveStartPrimaryKey(start);
        criteria.setExclusiveEndPrimaryKey(end);
        criteria.setMaxVersions(1);
        criteria.setLimit(100);
        criteria.setDirection(Direction.FORWARD);
        criteria.addColumnsToGet(new String[] {"timeline_id"});

        GetRangeRequest request = new GetRangeRequest(criteria);
        GetRangeResponse response = syncClient.getRange(request);

        List<Conversation> singleConversations = new ArrayList<Conversation>(response.getRows().size());

        for (Row row : response.getRows()) {
            String timelineId = row.getColumn("timeline_id").get(0).getValue().asString();
            String subUserId = row.getPrimaryKey().getPrimaryKeyColumn("sub_user").getValue().asString();
            User friend = describeUser(subUserId);

            Conversation conversation = new Conversation(timelineId, friend);

            singleConversations.add(conversation);
        }

        return singleConversations;
    }

    public List<Conversation> listMyGroupConversations(String userId) {
        PrimaryKey start = PrimaryKeyBuilder.createPrimaryKeyBuilder()
                .addPrimaryKeyColumn("user_id", PrimaryKeyValue.fromString(userId))
                .addPrimaryKeyColumn("group_id", PrimaryKeyValue.INF_MIN)
                .build();

        PrimaryKey end = PrimaryKeyBuilder.createPrimaryKeyBuilder()
                .addPrimaryKeyColumn("user_id", PrimaryKeyValue.fromString(userId))
                .addPrimaryKeyColumn("group_id", PrimaryKeyValue.INF_MAX)
                .build();

        RangeRowQueryCriteria criteria = new RangeRowQueryCriteria(groupRelationGlobalIndex);
        criteria.setInclusiveStartPrimaryKey(start);
        criteria.setExclusiveEndPrimaryKey(end);
        criteria.setMaxVersions(1);
        criteria.setLimit(100);
        criteria.setDirection(Direction.FORWARD);
        criteria.addColumnsToGet(new String[] {"group_id"});

        GetRangeRequest request = new GetRangeRequest(criteria);
        GetRangeResponse response = syncClient.getRange(request);

        List<Conversation> groupConversations = new ArrayList<Conversation>(response.getRows().size());

        for (Row row : response.getRows()) {
            String timelineId = row.getPrimaryKey().getPrimaryKeyColumn("group_id").getValue().asString();
            Group group = describeGroup(timelineId);

            Conversation conversation = new Conversation(timelineId, group);

            groupConversations.add(conversation);
        }

        return groupConversations;
    }

    public List<Conversation> searchGroupConversation(String groupName) {

        TermQuery termQuery = new TermQuery();
        termQuery.setFieldName("type");
        termQuery.setTerm(ColumnValue.fromString(TimelineType.GROUP.toString()));

        MatchPhraseQuery matchPhraseQuery = new MatchPhraseQuery();
        matchPhraseQuery.setFieldName("group_name");
        matchPhraseQuery.setText(groupName);

        BoolQuery boolQuery = new BoolQuery();
        boolQuery.setMustQueries(Arrays.asList(termQuery, matchPhraseQuery));

        SearchQuery searchQuery = new SearchQuery();
        searchQuery.setQuery(boolQuery);
        searchQuery.setLimit(100);

        List<SearchResult.Entry<TimelineMeta>> entryList = timelineV2.getTimelineMetaStoreInstance().search(searchQuery).getEntries();
        List<Conversation> groupConversationList = new LinkedList<Conversation>();


        for (SearchResult.Entry<TimelineMeta> resultEntry : entryList) {
            TimelineMeta timelineMeta = resultEntry.getData();

            Conversation conversation = timelineMetaToConversation(timelineMeta);

            groupConversationList.add(conversation);
        }

        return groupConversationList;
    }
}
