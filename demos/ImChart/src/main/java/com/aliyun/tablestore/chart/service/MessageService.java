package com.aliyun.tablestore.chart.service;

import com.alicloud.openservices.tablestore.model.ColumnValue;
import com.alicloud.openservices.tablestore.model.search.SearchQuery;
import com.alicloud.openservices.tablestore.model.search.query.BoolQuery;
import com.alicloud.openservices.tablestore.model.search.query.MatchPhraseQuery;
import com.alicloud.openservices.tablestore.model.search.query.TermQuery;
import com.alicloud.openservices.tablestore.timeline.TimelineStore;
import com.alicloud.openservices.tablestore.timeline.model.TimelineEntry;
import com.alicloud.openservices.tablestore.timeline.model.TimelineIdentifier;
import com.alicloud.openservices.tablestore.timeline.query.ScanParameter;
import com.alicloud.openservices.tablestore.timeline.query.SearchResult;
import com.aliyun.tablestore.chart.TimelineV2;
import com.aliyun.tablestore.chart.model.User;
import com.aliyun.tablestore.chart.model.AppMessage;

import java.util.*;

import static com.aliyun.tablestore.chart.Util.timelineIdToTimelineIdentifier;

public class MessageService implements IMessageService {
    private TimelineV2 timelineV2;

    public MessageService (TimelineV2 timelineV2) {
        this.timelineV2 = timelineV2;
    }

    public void sendSingleMessage(String receiverId, AppMessage message) {
        {
            TimelineStore store = timelineV2.getTimelineStoreTableInstance();
            TimelineIdentifier identifier = timelineIdToTimelineIdentifier(message.getTimelineId());

            store.createTimelineQueue(identifier).store(message.getTimelineMessage());
        }

        {
            TimelineStore sync = timelineV2.getTimelineSyncTableInstance();
            TimelineIdentifier identifier = timelineIdToTimelineIdentifier(receiverId);

            sync.createTimelineQueue(identifier).store(message.getTimelineMessage());
        }
    }

    public void sendGroupMessage(AppMessage message, UserService userService) {
        {
            TimelineStore store = timelineV2.getTimelineStoreTableInstance();
            TimelineIdentifier identifier = timelineIdToTimelineIdentifier(message.getTimelineId());

            store.createTimelineQueue(identifier).store(message.getTimelineMessage());
        }

        {

            List<User> groupUsers = userService.listGroupUsers(message.getTimelineId());
            TimelineStore sync = timelineV2.getTimelineSyncTableInstance();
            for (User user : groupUsers) {
                TimelineIdentifier identifier = timelineIdToTimelineIdentifier(user.getUserId());

                sync.createTimelineQueue(identifier).store(message.getTimelineMessage());
            }
        }
    }

public List<AppMessage> fetchSyncMessage(String userId, long lastSequenceId) {
    TimelineStore sync =  timelineV2.getTimelineSyncTableInstance();

    TimelineIdentifier identifier = new TimelineIdentifier.Builder()
            .addField("timeline_id", userId)
            .build();

    ScanParameter parameter = new ScanParameter()
            .scanForward(lastSequenceId)
            .maxCount(30);

    Iterator<TimelineEntry> iterator = sync.createTimelineQueue(identifier).scan(parameter);

    List<AppMessage> appMessages = new LinkedList<AppMessage>();
    int counter = 0;
    while (iterator.hasNext() && counter++ <= 30) {
        AppMessage appMessage = new AppMessage(userId, iterator.next());
        appMessages.add(appMessage);
    }

    return appMessages;
}


public List<AppMessage> fetchConversationMessage(String timelineId, long sequenceId) {
    TimelineStore store =  timelineV2.getTimelineStoreTableInstance();

    TimelineIdentifier identifier = new TimelineIdentifier.Builder()
            .addField("timeline_id", timelineId)
            .build();

    ScanParameter parameter = new ScanParameter()
            .scanBackward(sequenceId)
            .maxCount(30);

    Iterator<TimelineEntry> iterator = store.createTimelineQueue(identifier).scan(parameter);

    List<AppMessage> appMessages = new LinkedList<AppMessage>();
    int counter = 0;
    while (iterator.hasNext() && counter++ <= 30) {
        TimelineEntry timelineEntry = iterator.next();
        AppMessage appMessage = new AppMessage(timelineId, timelineEntry);

        appMessages.add(appMessage);
    }

    return appMessages;
}

    public List<AppMessage> searchMessage(String timelineId, String content) {
        TimelineStore store =  timelineV2.getTimelineStoreTableInstance();

        TermQuery termQuery = new TermQuery();
        termQuery.setFieldName("timeline_id");
        termQuery.setTerm(ColumnValue.fromString(timelineId));

        MatchPhraseQuery matchPhraseQuery = new MatchPhraseQuery();
        matchPhraseQuery.setFieldName("text");
        matchPhraseQuery.setText(content);

        BoolQuery boolQuery = new BoolQuery();
        boolQuery.setMustQueries(Arrays.asList(termQuery, matchPhraseQuery));

        SearchQuery searchQuery = new SearchQuery();
        searchQuery.setQuery(boolQuery);
        searchQuery.setLimit(30);

        List<SearchResult.Entry<TimelineEntry>> entryList = store.search(searchQuery).getEntries();
        List<AppMessage> appMessages = new LinkedList<AppMessage>();

        for (SearchResult.Entry<TimelineEntry> resultEntry : entryList) {
            String tId = resultEntry.getIdentifier().getField(0).getValue().toString();
            TimelineEntry timelineEntry = resultEntry.getData();

            AppMessage appMessage = new AppMessage(tId, timelineEntry);

            appMessages.add(appMessage);
        }

        return appMessages;
    }
}
