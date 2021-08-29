package com.aliyun.tablestore.examples.config;

import com.alicloud.openservices.tablestore.SyncClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SysClientConfig {

    @Bean
    public SyncClient getSysClinet(@Value("${ots.endpoint}") String endpoint, @Value("${ots.accessId}") String accessKeyId,
                                   @Value("${ots.accessKey}") String accessKeySecret, @Value("${ots.instanceName}") String instanceName) {
        return new SyncClient(endpoint, accessKeyId, accessKeySecret, instanceName);
    }

}
