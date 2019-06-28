package com.aliyun.tablestore.examples.tablecopy;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.alicloud.openservices.tablestore.SyncClient;
import com.alicloud.openservices.tablestore.TunnelClient;
import com.alicloud.openservices.tablestore.model.CreateTableRequest;
import com.alicloud.openservices.tablestore.model.DescribeTableRequest;
import com.alicloud.openservices.tablestore.model.DescribeTableResponse;
import com.alicloud.openservices.tablestore.model.ReservedThroughput;
import com.alicloud.openservices.tablestore.model.tunnel.CreateTunnelRequest;
import com.alicloud.openservices.tablestore.model.tunnel.CreateTunnelResponse;
import com.alicloud.openservices.tablestore.model.tunnel.DescribeTunnelRequest;
import com.alicloud.openservices.tablestore.model.tunnel.DescribeTunnelResponse;
import com.alicloud.openservices.tablestore.model.tunnel.ListTunnelRequest;
import com.alicloud.openservices.tablestore.model.tunnel.TunnelInfo;
import com.alicloud.openservices.tablestore.model.tunnel.TunnelType;
import com.alicloud.openservices.tablestore.tunnel.worker.TunnelWorker;
import com.alicloud.openservices.tablestore.tunnel.worker.TunnelWorkerConfig;
import com.aliyun.tablestore.examples.tablecopy.util.TableCopyConfig;

public class TableCopySample {
    private TableCopyConfig config;
    private SyncClient sourceClient;
    private TunnelClient sourceTunnelClient;
    private TunnelWorkerConfig sourceWorkerConfig;
    private TunnelWorker sourceWorker;
    private SyncClient destClient;
    private ScheduledExecutorService backgroundExecutor;

    public TableCopySample(TableCopyConfig config) {
        this.config = config;
    }

    public void working() throws Exception {
        // 1. create backup table.
        sourceClient = new SyncClient(config.getReadConf().getEndpoint(), config.getReadConf().getAccessId(),
            config.getReadConf().getAccessKey(), config.getReadConf().getInstanceName());
        destClient = new SyncClient(config.getWriteConf().getEndpoint(), config.getWriteConf().getAccessId(),
            config.getWriteConf().getAccessKey(), config.getWriteConf().getInstanceName());
        if (destClient.listTable().getTableNames().contains(config.getWriteConf().getTableName())) {
            System.out.println("Table is already exist: " + config.getWriteConf().getTableName());
        } else {
            DescribeTableResponse describeTableResponse = sourceClient.describeTable(
                new DescribeTableRequest(config.getReadConf().getTableName()));
            describeTableResponse.getTableMeta().setTableName(config.getWriteConf().getTableName());
            describeTableResponse.getTableOptions().setMaxTimeDeviation(Long.MAX_VALUE / 1000000);
            CreateTableRequest createTableRequest = new CreateTableRequest(describeTableResponse.getTableMeta(),
                describeTableResponse.getTableOptions(),
                new ReservedThroughput(describeTableResponse.getReservedThroughputDetails().getCapacityUnit()));
            destClient.createTable(createTableRequest);
            System.out.println("Create table success: " + config.getWriteConf().getTableName());
        }

        // 2. create tunnel on source table.
        sourceTunnelClient = new TunnelClient(config.getReadConf().getEndpoint(), config.getReadConf().getAccessId(),
            config.getReadConf().getAccessKey(), config.getReadConf().getInstanceName());
        List<TunnelInfo> tunnelInfos = sourceTunnelClient.listTunnel(
            new ListTunnelRequest(config.getReadConf().getTableName())).getTunnelInfos();
        String tunnelId = null;
        TunnelInfo tunnelInfo = getTunnelInfo(config.getReadConf().getTunnelName(), tunnelInfos);
        if (tunnelInfo != null) {
            tunnelId = tunnelInfo.getTunnelId();
            System.out.println(String.format("Tunnel is already exist, TunnelName: %s, TunnelId: %s",
                config.getReadConf().getTunnelName(), tunnelId));
        } else {
            CreateTunnelResponse createTunnelResponse = sourceTunnelClient.createTunnel(
                new CreateTunnelRequest(config.getReadConf().getTableName(),
                    config.getReadConf().getTunnelName(), TunnelType.BaseAndStream));
            System.out.println("Create tunnel success: " + createTunnelResponse.getTunnelId());
        }

        // 3. background thread to check whether data copy is finished.
        backgroundExecutor = Executors.newScheduledThreadPool(2, new ThreadFactory() {
            private final AtomicInteger counter = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "background-checker-" + counter.getAndIncrement());
            }
        });
        backgroundExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                DescribeTunnelResponse resp = sourceTunnelClient.describeTunnel(new DescribeTunnelRequest(
                    config.getReadConf().getTableName(), config.getReadConf().getTunnelName()
                ));
                // 已同步完成
                if (resp.getTunnelConsumePoint().getTime() > config.getReadConf().getEndTime()) {
                    System.out.println("Table copy finished, program exit!");
                    // 退出备份程序
                    shutdown();
                }
            }
        }, 0, 2, TimeUnit.SECONDS);

        // 4. begin or continue data copy.
        if (tunnelId != null) {
            sourceWorkerConfig = new TunnelWorkerConfig(
                new OtsReaderProcessor(config.getReadConf(), config.getWriteConf(), destClient));
            sourceWorkerConfig.setHeartbeatIntervalInSec(15);
            sourceWorker = new TunnelWorker(tunnelId, sourceTunnelClient, sourceWorkerConfig);
            sourceWorker.connectAndWorking();
        }
    }

    private TunnelInfo getTunnelInfo(String tunnelName, List<TunnelInfo> tunnelInfos) {
        for (TunnelInfo tunnelInfo : tunnelInfos) {
            if (tunnelName.equals(tunnelInfo.getTunnelName())) {
                return tunnelInfo;
            }
        }
        return null;
    }

    private void shutdown() {
        if (sourceWorker != null) {
            sourceWorker.shutdown();
        }
        if (sourceWorkerConfig != null) {
            sourceWorkerConfig.shutdown();
        }
        if (sourceTunnelClient != null) {
            sourceTunnelClient.shutdown();
        }
        if (sourceClient != null) {
            sourceClient.shutdown();
        }
        if (destClient != null) {
            destClient.shutdown();
        }
        if (backgroundExecutor != null) {
            backgroundExecutor.shutdown();
        }
    }

    public static void main(String[] args) {
        String confPath = "config.json";
        TableCopySample tableCopySample = new TableCopySample(TableCopyConfig.loadConfig(confPath));
        try {
            tableCopySample.working();
        } catch (Exception e) {
            e.printStackTrace();
            tableCopySample.shutdown();
        }
    }
}
