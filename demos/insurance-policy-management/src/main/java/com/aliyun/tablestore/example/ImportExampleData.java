package com.aliyun.tablestore.example;

import com.alicloud.openservices.tablestore.model.BatchWriteRowRequest;
import com.aliyun.tablestore.example.consts.NameConsts;
import com.aliyun.tablestore.example.model.PolicyDO;
import com.aliyun.tablestore.example.utils.ClientAndConfig;
import com.aliyun.tablestore.example.utils.Utils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author hydrogen
 */
public class ImportExampleData extends BaseExample {

    private AtomicLong policyIdGenerator = new AtomicLong();

    private static final int POLICY_ID_MAX_DIGITS = 15;

    private static final int USER_ID_LEFT_PAD = 10;
    private static final int MAX_APPLIER_COUNT = 1000_000;


    private static final int MAX_BROKER_COUNT = 10_000;

    private static final String POLICY_ID_PREFIX = "POLICY_";
    private static final String USER_ID_PREFIX = "USER_";
    private static final String BROKER_USER_ID_PREFIX = "BROKER_";

    public ImportExampleData(ClientAndConfig clientAndConfig) {
        super(clientAndConfig);
    }

    public static void main(String[] args) {
        new ImportExampleData(Utils.getClientAndConfig(args)).main();
    }

    private <T> T randomFrom(T[] candidates) {
        return candidates[RandomUtils.nextInt(0, candidates.length)];
    }

    private String randomName() {
        return randomFrom(NameConsts.FIRST_NAME_CANDIDATES) + " " + randomFrom(NameConsts.FAMILY_NAME_CANDIDATES);
    }

    private String randomGender() {
        return RandomUtils.nextInt(0, 2) == 1 ? "male" : "female";
    }

    private String randomId() {
        return Integer.toString(RandomUtils.nextInt(100, 1000))
                + RandomUtils.nextInt(100, 1000)
                + RandomUtils.nextInt(1900, 2010)
                + StringUtils.leftPad(Integer.toString(RandomUtils.nextInt(1, 13)), 2, '0')
                + StringUtils.leftPad(Integer.toString(RandomUtils.nextInt(1, 31)), 2, '0')
                + StringUtils.leftPad(Integer.toString(RandomUtils.nextInt(0, 10000)), 4, '0');
    }

    private PolicyDO randomPolicyDO() {
        PolicyDO policyDO = new PolicyDO();
        long currentId = policyIdGenerator.getAndIncrement();
        policyDO.setPolicyId(POLICY_ID_PREFIX + StringUtils.leftPad(Long.toString(currentId), POLICY_ID_MAX_DIGITS, '0'));
        policyDO.setProductName(randomFrom(NameConsts.PRODUCT_NAME_CANDIDATES));
        policyDO.setOperateTime(System.currentTimeMillis() - RandomUtils.nextLong(0, 5 * 365 * 24 * 60 * 60 * 1000L));
        policyDO.setEffectiveTime(policyDO.getOperateTime() + RandomUtils.nextLong(0, 5 * 30 * 24 * 60 * 60 * 1000L));
        policyDO.setExpirationTime(policyDO.getEffectiveTime() + RandomUtils.nextLong(0, 10 * 365 * 24 * 60 * 60 * 1000L));

        policyDO.setApplierUserId(USER_ID_PREFIX + StringUtils.leftPad(Long.toString(RandomUtils.nextLong(0, MAX_APPLIER_COUNT)), USER_ID_LEFT_PAD, '0'));
        policyDO.setBrokerUserId(BROKER_USER_ID_PREFIX + StringUtils.leftPad(Long.toString(RandomUtils.nextLong(0, MAX_BROKER_COUNT)), USER_ID_LEFT_PAD, '0'));

        policyDO.setApplierName(randomName());
        policyDO.setApplierId(randomId());
        policyDO.setApplierGender(randomGender());

        policyDO.setInsuredName(randomName());
        policyDO.setInsuredId(randomId());
        policyDO.setInsuredGender(randomGender());

        policyDO.setPremium(RandomUtils.nextLong(0, 100_000));
        policyDO.setProfit(RandomUtils.nextLong(500_000, 1000_000));

        int beneficiaryCount = RandomUtils.nextInt(1, 5);
        List<PolicyDO.BeneficiaryInfo> beneficiaryInfos = new ArrayList<>(beneficiaryCount);

        long percentage = 100L;
        int i = 0;
        while (percentage > 0) {
            long currentPercentage;
            if (i >= beneficiaryCount) {
                currentPercentage = percentage;
            } else {
                currentPercentage = RandomUtils.nextLong(1, percentage + 1);
            }

            percentage -= currentPercentage;

            PolicyDO.BeneficiaryInfo beneficiaryInfo = new PolicyDO.BeneficiaryInfo();
            beneficiaryInfo.setName(randomName())
                    .setGender(randomGender())
                    .setId(randomId());
            beneficiaryInfo.setBenefitPercentage(currentPercentage);

            beneficiaryInfos.add(beneficiaryInfo);
            i++;
        }

        policyDO.setBeneficiaryInfo(beneficiaryInfos);

        return policyDO;
    }

    @Override
    protected void doMain() {
        final int maxCount = 10_000_000;
        final int batchCount = 100;
        final int batchThreadCount = 10;
        final CountDownLatch doneFlag = new CountDownLatch(batchThreadCount);
        final AtomicInteger importCounter = new AtomicInteger();
        for (int i = 0; i < batchThreadCount; i++) {
            new Thread(() -> {
                while (true) {
                    try {
                        int currentCount = importCounter.getAndAdd(batchCount);
                        if (currentCount >= maxCount) {
                            break;
                        }
                        BatchWriteRowRequest batchWriteRowRequest = new BatchWriteRowRequest();
                        for (int j = 0; j < batchCount; j++) {
                            batchWriteRowRequest.addRowChange(randomPolicyDO().toRowPutChange(tableName));
                        }
                        syncClient.batchWriteRow(batchWriteRowRequest);

                        System.out.println((currentCount + batchCount) + " done");


                    } catch (Exception ex) {
                        importCounter.addAndGet(-1 * batchCount);
                        System.out.println("Exception retry");
                    }
                }
                doneFlag.countDown();
            }).start();
        }
        try {
            doneFlag.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
